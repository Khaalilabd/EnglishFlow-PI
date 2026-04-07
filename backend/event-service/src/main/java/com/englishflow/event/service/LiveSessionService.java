package com.englishflow.event.service;

import com.englishflow.event.dto.live.*;
import com.englishflow.event.entity.*;
import com.englishflow.event.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveSessionService {

    private final SimpMessagingTemplate ws;
    private final PollRepository pollRepo;
    private final LiveQuestionRepository questionRepo;
    private final ChatMessageRepository chatRepo;
    private final HandRaiseRepository handRaiseRepo;
    private final TranslationService translationService;

    // In-memory presence store: eventId -> Map<userId, PresenceDTO>
    private final Map<Integer, Map<Long, PresenceDTO>> presenceStore = new ConcurrentHashMap<>();

    // ─── PRESENCE ────────────────────────────────────────────────

    public void broadcastPresence(PresenceDTO dto) {
        presenceStore.computeIfAbsent(dto.getEventId(), k -> new ConcurrentHashMap<>());
        if ("JOIN".equals(dto.getAction())) {
            dto.setJoinedAt(java.time.LocalDateTime.now());
            presenceStore.get(dto.getEventId()).put(dto.getUserId(), dto);
        } else {
            presenceStore.get(dto.getEventId()).remove(dto.getUserId());
        }
        ws.convertAndSend("/topic/session/" + dto.getEventId() + "/presence", dto);
    }

    public List<PresenceDTO> getParticipants(Integer eventId) {
        Map<Long, PresenceDTO> map = presenceStore.get(eventId);
        return map != null ? new ArrayList<>(map.values()) : new ArrayList<>();
    }

    @Transactional
    public void raiseHand(Integer eventId, Long userId, String userName) {
        // Ignore if already raised
        if (handRaiseRepo.findByEventIdAndUserIdAndDismissedFalse(eventId, userId).isPresent()) return;

        HandRaise h = new HandRaise();
        h.setEventId(eventId);
        h.setUserId(userId);
        h.setUserName(userName);
        handRaiseRepo.save(h);

        broadcastHandQueue(eventId);
    }

    @Transactional
    public void dismissHand(Integer eventId, Long userId) {
        handRaiseRepo.findByEventIdAndUserIdAndDismissedFalse(eventId, userId)
            .ifPresent(h -> {
                h.setDismissed(true);
                handRaiseRepo.save(h);
            });
        broadcastHandQueue(eventId);
    }

    private void broadcastHandQueue(Integer eventId) {
        List<HandRaiseDTO> queue = handRaiseRepo
            .findByEventIdAndDismissedFalseOrderByRaisedAtAsc(eventId)
            .stream().map(this::toHandDTO).collect(Collectors.toList());

        HandRaiseDTO msg = new HandRaiseDTO();
        msg.setQueue(queue);
        ws.convertAndSend("/topic/session/" + eventId + "/hands", msg);
    }

    // ─── POLL ────────────────────────────────────────────────────

    @Transactional
    public PollDTO createPoll(Integer eventId, String question, List<String> options, boolean multipleChoice) {
        // Deactivate previous active poll
        pollRepo.findByEventIdAndActiveTrue(eventId)
            .ifPresent(p -> { p.setActive(false); pollRepo.save(p); });

        Poll poll = new Poll();
        poll.setEventId(eventId);
        poll.setQuestion(question);
        poll.setMultipleChoice(multipleChoice);

        options.forEach(text -> {
            PollOption opt = new PollOption();
            opt.setPoll(poll);
            opt.setText(text);
            poll.getOptions().add(opt);
        });

        Poll saved = pollRepo.save(poll);
        PollDTO dto = toPollDTO(saved, null);
        ws.convertAndSend("/topic/session/" + eventId + "/poll", dto);
        return dto;
    }

    @Transactional
    public PollDTO vote(Integer eventId, Long pollId, Long optionId, Long userId) {
        Poll poll = pollRepo.findById(pollId)
            .orElseThrow(() -> new RuntimeException("Poll not found"));

        if (!poll.isMultipleChoice()) {
            // Remove previous vote from all options
            poll.getOptions().forEach(o -> o.getVoterIds().remove(userId));
        }

        poll.getOptions().stream()
            .filter(o -> o.getId().equals(optionId))
            .findFirst()
            .ifPresent(o -> {
                if (!o.getVoterIds().contains(userId)) o.getVoterIds().add(userId);
            });

        Poll saved = pollRepo.save(poll);
        PollDTO dto = toPollDTO(saved, userId);
        ws.convertAndSend("/topic/session/" + eventId + "/poll", dto);
        return dto;
    }

    // ─── Q&A ─────────────────────────────────────────────────────

    @Transactional
    public QuestionDTO askQuestion(Integer eventId, Long authorId, String authorName,
                                   String text, boolean anonymous) {
        LiveQuestion q = new LiveQuestion();
        q.setEventId(eventId);
        q.setAuthorId(authorId);
        q.setAuthorName(anonymous ? "Anonymous" : authorName);
        q.setText(text);
        q.setAnonymous(anonymous);
        LiveQuestion saved = questionRepo.save(q);

        QuestionDTO dto = toQuestionDTO(saved, authorId);
        ws.convertAndSend("/topic/session/" + eventId + "/qa", buildQaPayload(eventId, authorId));
        return dto;
    }

    @Transactional
    public void upvoteQuestion(Integer eventId, Long questionId, Long userId) {
        questionRepo.findById(questionId).ifPresent(q -> {
            if (!q.getUpvoterIds().contains(userId)) q.getUpvoterIds().add(userId);
            else q.getUpvoterIds().remove(userId); // toggle
            questionRepo.save(q);
            ws.convertAndSend("/topic/session/" + eventId + "/qa", buildQaPayload(eventId, userId));
        });
    }

    @Transactional
    public void markAnswered(Integer eventId, Long questionId) {
        questionRepo.findById(questionId).ifPresent(q -> {
            q.setAnswered(true);
            questionRepo.save(q);
            ws.convertAndSend("/topic/session/" + eventId + "/qa", buildQaPayload(eventId, null));
        });
    }

    // ─── CHAT ────────────────────────────────────────────────────

    @Transactional
    public ChatMessageDTO sendMessage(ChatMessageDTO incoming) {
        // Basic moderation: block empty or too-long messages
        if (incoming.getContent() == null || incoming.getContent().isBlank()) return null;

        // Auto-translate if targetLang provided
        if (incoming.getTargetLang() != null && !incoming.getTargetLang().isBlank()) {
            String translated = translationService.translate(
                incoming.getContent(), incoming.getTargetLang());
            incoming.setTranslatedContent(translated);
        }

        ChatMessage msg = new ChatMessage();
        msg.setEventId(incoming.getEventId());
        msg.setSenderId(incoming.getSenderId());
        msg.setSenderName(incoming.getSenderName());
        msg.setContent(incoming.getContent());
        msg.setTranslatedContent(incoming.getTranslatedContent());
        ChatMessage saved = chatRepo.save(msg);

        ChatMessageDTO dto = toChatDTO(saved);
        ws.convertAndSend("/topic/session/" + incoming.getEventId() + "/chat", dto);
        return dto;
    }

    @Transactional
    public void moderateMessage(Integer eventId, Long messageId) {
        chatRepo.findById(messageId).ifPresent(m -> {
            m.setModerated(true);
            chatRepo.save(m);
            // Notify clients to remove the message
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setId(messageId);
            dto.setModerated(true);
            ws.convertAndSend("/topic/session/" + eventId + "/chat/moderated", dto);
        });
    }

    // ─── REACTIONS ───────────────────────────────────────────────

    public void broadcastReaction(ReactionDTO reaction) {
        // Ephemeral — just relay, no DB
        ws.convertAndSend("/topic/session/" + reaction.getEventId() + "/reactions", reaction);
    }

    // ─── WHITEBOARD ──────────────────────────────────────────────

    public void broadcastWhiteboardEvent(WhiteboardEventDTO event) {
        // Relay to all participants — no DB persistence for strokes
        ws.convertAndSend("/topic/session/" + event.getEventId() + "/whiteboard", event);
    }

    // ─── WEBRTC SIGNALING ────────────────────────────────────────

    public void relayWebRTCSignal(WebRTCSignalDTO signal) {
        if (signal.getToUserId() != null) {
            // Send to a per-user topic instead of convertAndSendToUser (no Spring Security principal)
            ws.convertAndSend(
                "/topic/session/" + signal.getEventId() + "/webrtc/user/" + signal.getToUserId(),
                signal
            );
        } else {
            // Broadcast join/leave to all
            ws.convertAndSend("/topic/session/" + signal.getEventId() + "/webrtc", signal);
        }
    }

    // ─── HISTORY (REST) ──────────────────────────────────────────

    public List<ChatMessageDTO> getChatHistory(Integer eventId) {
        return chatRepo.findByEventIdAndModeratedFalseOrderBySentAtAsc(eventId)
            .stream().map(this::toChatDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestions(Integer eventId, Long currentUserId) {
        return questionRepo.findByEventIdOrderByCreatedAtDesc(eventId)
            .stream()
            .map(q -> toQuestionDTO(q, currentUserId))
            .sorted((a, b) -> b.getUpvoteCount() - a.getUpvoteCount())
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PollDTO getActivePoll(Integer eventId, Long currentUserId) {
        return pollRepo.findByEventIdAndActiveTrue(eventId)
            .map(p -> toPollDTO(p, currentUserId))
            .orElse(null);
    }

    // ─── MAPPERS ─────────────────────────────────────────────────

    private PollDTO toPollDTO(Poll poll, Long currentUserId) {
        PollDTO dto = new PollDTO();
        dto.setId(poll.getId());
        dto.setEventId(poll.getEventId());
        dto.setQuestion(poll.getQuestion());
        dto.setMultipleChoice(poll.isMultipleChoice());
        dto.setActive(poll.isActive());
        dto.setOptions(poll.getOptions().stream().map(o -> {
            PollDTO.PollOptionDTO opt = new PollDTO.PollOptionDTO();
            opt.setId(o.getId());
            opt.setText(o.getText());
            opt.setVoteCount(o.getVoteCount());
            opt.setVotedByCurrentUser(currentUserId != null && o.getVoterIds().contains(currentUserId));
            return opt;
        }).collect(Collectors.toList()));
        return dto;
    }

    private QuestionDTO toQuestionDTO(LiveQuestion q, Long currentUserId) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getId());
        dto.setEventId(q.getEventId());
        dto.setAuthorId(q.isAnonymous() ? null : q.getAuthorId());
        dto.setAuthorName(q.getAuthorName());
        dto.setText(q.getText());
        dto.setUpvoteCount(q.getUpvoteCount());
        dto.setUpvotedByCurrentUser(currentUserId != null && q.getUpvoterIds().contains(currentUserId));
        dto.setAnswered(q.isAnswered());
        dto.setAnonymous(q.isAnonymous());
        dto.setCreatedAt(q.getCreatedAt());
        return dto;
    }

    private ChatMessageDTO toChatDTO(ChatMessage m) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(m.getId());
        dto.setEventId(m.getEventId());
        dto.setSenderId(m.getSenderId());
        dto.setSenderName(m.getSenderName());
        dto.setContent(m.getContent());
        dto.setTranslatedContent(m.getTranslatedContent());
        dto.setSentAt(m.getSentAt());
        return dto;
    }

    private HandRaiseDTO toHandDTO(HandRaise h) {
        HandRaiseDTO dto = new HandRaiseDTO();
        dto.setId(h.getId());
        dto.setEventId(h.getEventId());
        dto.setUserId(h.getUserId());
        dto.setUserName(h.getUserName());
        dto.setRaisedAt(h.getRaisedAt());
        return dto;
    }

    private List<QuestionDTO> buildQaPayload(Integer eventId, Long currentUserId) {
        return getQuestions(eventId, currentUserId);
    }
}

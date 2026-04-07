package com.englishflow.event.controller;

import com.englishflow.event.dto.live.*;
import com.englishflow.event.service.LiveSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LiveSessionWsController {

    private final LiveSessionService liveService;

    // ─── HAND RAISE ──────────────────────────────────────────────
    // SEND /app/session/{eventId}/hand/raise  { userId, userName }
    @MessageMapping("/session/{eventId}/hand/raise")
    public void raiseHand(@DestinationVariable Integer eventId, @Payload HandRaiseDTO dto) {
        liveService.raiseHand(eventId, dto.getUserId(), dto.getUserName());
    }

    // SEND /app/session/{eventId}/hand/dismiss  { userId }
    @MessageMapping("/session/{eventId}/hand/dismiss")
    public void dismissHand(@DestinationVariable Integer eventId, @Payload HandRaiseDTO dto) {
        liveService.dismissHand(eventId, dto.getUserId());
    }

    // ─── POLL ────────────────────────────────────────────────────
    // SEND /app/session/{eventId}/poll/vote  { pollId, optionId, userId }
    @MessageMapping("/session/{eventId}/poll/vote")
    public void vote(@DestinationVariable Integer eventId, @Payload PollVoteRequest req) {
        liveService.vote(eventId, req.getPollId(), req.getOptionId(), req.getUserId());
    }

    // ─── Q&A ─────────────────────────────────────────────────────
    // SEND /app/session/{eventId}/qa/ask  { authorId, authorName, text, anonymous }
    @MessageMapping("/session/{eventId}/qa/ask")
    public void askQuestion(@DestinationVariable Integer eventId, @Payload QuestionDTO dto) {
        liveService.askQuestion(eventId, dto.getAuthorId(), dto.getAuthorName(),
            dto.getText(), dto.isAnonymous());
    }

    // SEND /app/session/{eventId}/qa/upvote  { id, authorId }
    @MessageMapping("/session/{eventId}/qa/upvote")
    public void upvote(@DestinationVariable Integer eventId, @Payload QuestionDTO dto) {
        liveService.upvoteQuestion(eventId, dto.getId(), dto.getAuthorId());
    }

    // SEND /app/session/{eventId}/qa/answered  { id }
    @MessageMapping("/session/{eventId}/qa/answered")
    public void markAnswered(@DestinationVariable Integer eventId, @Payload QuestionDTO dto) {
        liveService.markAnswered(eventId, dto.getId());
    }

    // ─── CHAT ────────────────────────────────────────────────────
    // SEND /app/session/{eventId}/chat  { senderId, senderName, content, targetLang? }
    @MessageMapping("/session/{eventId}/chat")
    public void chat(@DestinationVariable Integer eventId, @Payload ChatMessageDTO dto) {
        dto.setEventId(eventId);
        liveService.sendMessage(dto);
    }

    // ─── REACTIONS ───────────────────────────────────────────────
    // SEND /app/session/{eventId}/reaction  { userId, userName, emoji }
    @MessageMapping("/session/{eventId}/reaction")
    public void reaction(@DestinationVariable Integer eventId, @Payload ReactionDTO dto) {
        dto.setEventId(eventId);
        liveService.broadcastReaction(dto);
    }

    // ─── WHITEBOARD ──────────────────────────────────────────────
    // SEND /app/session/{eventId}/whiteboard  { userId, type, x, y, ... }
    @MessageMapping("/session/{eventId}/whiteboard")
    public void whiteboard(@DestinationVariable Integer eventId, @Payload WhiteboardEventDTO dto) {
        dto.setEventId(eventId);
        liveService.broadcastWhiteboardEvent(dto);
    }

    // ─── PRESENCE ────────────────────────────────────────────────
    @MessageMapping("/session/{eventId}/presence")
    public void presence(@DestinationVariable Integer eventId, @Payload PresenceDTO dto) {
        dto.setEventId(eventId);
        liveService.broadcastPresence(dto);
    }

    // ─── WEBRTC SIGNALING ────────────────────────────────────────
    // SEND /app/session/{eventId}/webrtc  { type, fromUserId, toUserId?, sdp?, candidate? }
    @MessageMapping("/session/{eventId}/webrtc")
    public void webrtcSignal(@DestinationVariable Integer eventId, @Payload WebRTCSignalDTO dto) {
        dto.setEventId(eventId);
        liveService.relayWebRTCSignal(dto);
    }

    // Inner request class for poll vote
    @lombok.Data
    public static class PollVoteRequest {
        private Long pollId;
        private Long optionId;
        private Long userId;
    }
}

package com.englishflow.messaging.constants;

public final class MessagingConstants {
    
    private MessagingConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // WebSocket destinations
    public static final String WS_ENDPOINT = "/ws";
    public static final String WS_APP_PREFIX = "/app";
    public static final String WS_TOPIC_PREFIX = "/topic";
    public static final String WS_CONVERSATION_TOPIC = "/topic/conversation/";
    public static final String WS_TYPING_SUFFIX = "/typing";
    public static final String WS_CHAT_MAPPING = "/chat/{conversationId}";
    public static final String WS_TYPING_MAPPING = "/typing/{conversationId}";
    
    // Message validation
    public static final int MAX_MESSAGE_LENGTH = 5000;
    public static final int MIN_MESSAGE_LENGTH = 1;
    public static final int MAX_CONVERSATION_NAME_LENGTH = 100;
    public static final int MIN_CONVERSATION_NAME_LENGTH = 1;
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;
    
    // Rate limiting
    public static final int MAX_MESSAGES_PER_MINUTE = 60;
    public static final int MAX_CONVERSATIONS_PER_HOUR = 10;
    
    // Message types
    public static final String MESSAGE_TYPE_TEXT = "TEXT";
    public static final String MESSAGE_TYPE_IMAGE = "IMAGE";
    public static final String MESSAGE_TYPE_FILE = "FILE";
    
    // Conversation types
    public static final String CONVERSATION_TYPE_DIRECT = "DIRECT";
    public static final String CONVERSATION_TYPE_GROUP = "GROUP";
    
    // Error messages
    public static final String ERROR_CONVERSATION_NOT_FOUND = "Conversation not found";
    public static final String ERROR_MESSAGE_NOT_FOUND = "Message not found";
    public static final String ERROR_UNAUTHORIZED_ACCESS = "You are not authorized to access this conversation";
    public static final String ERROR_INVALID_MESSAGE_CONTENT = "Message content is invalid";
    public static final String ERROR_MESSAGE_TOO_LONG = "Message content exceeds maximum length of " + MAX_MESSAGE_LENGTH + " characters";
    public static final String ERROR_MESSAGE_EMPTY = "Message content cannot be empty";
    public static final String ERROR_RATE_LIMIT_EXCEEDED = "Rate limit exceeded. Please slow down.";
}

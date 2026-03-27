package com.englishflow.community.exception;

public class TopicLockedException extends RuntimeException {
    public TopicLockedException(Long topicId) {
        super(String.format("Topic with id %d is locked and cannot accept new posts", topicId));
    }
}

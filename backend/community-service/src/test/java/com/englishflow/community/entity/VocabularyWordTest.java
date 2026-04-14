package com.englishflow.community.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class VocabularyWordTest {

    @Test
    void testNoArgsConstructor() {
        VocabularyWord word = new VocabularyWord();
        assertNotNull(word);
    }

    @Test
    void testSettersAndGetters() {
        VocabularyWord word = new VocabularyWord();
        LocalDateTime now = LocalDateTime.now();
        
        word.setId(1L);
        word.setUserId(100L);
        word.setWord("test");
        word.setDefinition("A test word");
        word.setExample("This is a test");
        word.setCategory("General");
        word.setCreatedAt(now);
        
        assertEquals(1L, word.getId());
        assertEquals(100L, word.getUserId());
        assertEquals("test", word.getWord());
        assertEquals("A test word", word.getDefinition());
        assertEquals("This is a test", word.getExample());
        assertEquals("General", word.getCategory());
        assertEquals(now, word.getCreatedAt());
    }

    @Test
    void testPrePersist() {
        VocabularyWord word = new VocabularyWord();
        word.prePersist();
        assertNotNull(word.getCreatedAt());
    }
}

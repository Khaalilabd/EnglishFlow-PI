package com.englishflow.community.controller;

import com.englishflow.community.dto.DictionaryResponse;
import com.englishflow.community.dto.EnrichedDictionaryResponse;
import com.englishflow.community.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community/dictionary")
@RequiredArgsConstructor
@Tag(name = "Dictionary", description = "Dictionary lookup endpoints")
public class DictionaryController {
    
    private final DictionaryService dictionaryService;
    
    @GetMapping("/{word}")
    @Operation(summary = "Look up a word", description = "Get definition, pronunciation, and examples for a word")
    public ResponseEntity<DictionaryResponse[]> lookupWord(@PathVariable String word) {
        DictionaryResponse[] response = dictionaryService.lookupWord(word);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/enriched/{word}")
    @Operation(summary = "Look up a word with enriched data", 
               description = "Get definition with CEFR level, similar words, confusions, context, and image")
    public ResponseEntity<EnrichedDictionaryResponse> lookupWordEnriched(
            @PathVariable String word,
            @RequestParam(required = false) String context) {
        EnrichedDictionaryResponse response = dictionaryService.lookupWordEnriched(word, context);
        return ResponseEntity.ok(response);
    }
}

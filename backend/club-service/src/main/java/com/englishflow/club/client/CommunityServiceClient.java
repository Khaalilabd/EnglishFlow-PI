package com.englishflow.club.client;

import com.englishflow.club.dto.CreateTopicRequest;
import com.englishflow.club.dto.SubCategoryDTO;
import com.englishflow.club.dto.TopicDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "community-service")
public interface CommunityServiceClient {
    
    @PostMapping("/community/topics")
    TopicDTO createTopic(@RequestBody CreateTopicRequest request);
    
    @GetMapping("/community/categories/subcategories")
    List<SubCategoryDTO> getAllSubCategories();
    
    @DeleteMapping("/community/topics/{topicId}")
    void deleteTopic(@PathVariable("topicId") Long topicId, @RequestHeader("X-User-Id") Long userId);
}

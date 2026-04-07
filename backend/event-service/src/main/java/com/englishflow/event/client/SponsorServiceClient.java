package com.englishflow.event.client;

import com.englishflow.event.config.FeignConfig;
import com.englishflow.event.dto.EventSponsorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "sponsors-service", configuration = FeignConfig.class)
public interface SponsorServiceClient {
    
    @GetMapping("/sponsors/{id}")
    EventSponsorDTO getSponsorById(@PathVariable("id") Long id);
    
    @GetMapping("/sponsors")
    List<EventSponsorDTO> getAllSponsors();
}

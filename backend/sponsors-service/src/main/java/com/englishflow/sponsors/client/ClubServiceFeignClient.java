package com.englishflow.sponsors.client;

import com.englishflow.sponsors.dto.ExpenseDTO;
import com.englishflow.sponsors.dto.MemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "club-service")
public interface ClubServiceFeignClient {
    
    @GetMapping("/members/club/{clubId}")
    List<MemberDTO> getClubMembers(@PathVariable("clubId") Integer clubId);
    
    @PostMapping("/expenses")
    ExpenseDTO createExpense(@RequestBody ExpenseDTO expenseDTO);
}

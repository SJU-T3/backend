package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.GoalRequest;
import com.example.demo.calendar.dto.GoalResponse;
import com.example.demo.calendar.entity.Goal;
import com.example.demo.calendar.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/calendar/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{year}/{month}")
    public String setGoal(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month,
            @RequestBody GoalRequest req
    ) {
        LocalDate monthDate = LocalDate.of(year, month, 1);

        goalService.setMonthlyGoal(userId, monthDate, req.getGoal(), req.getTargetCount());

        return "목표가 설정되었습니다.";
    }

    @GetMapping("/{year}/{month}")
    public GoalResponse getGoal(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month
    ){
        LocalDate monthDate = LocalDate.of(year, month, 1);

        Goal goal = goalService.getMonthlyGoal(userId, monthDate);

        return (goal != null) ? new GoalResponse(goal) : null;
    }

}

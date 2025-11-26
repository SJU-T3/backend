package com.example.demo.calendar.controller;
import com.example.demo.calendar.entity.MonthlySummary;
import com.example.demo.calendar.service.MonthlySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.demo.calendar.dto.GoalAmountRequest;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar/monthly")
public class MonthlySummaryController {
    private final MonthlySummaryService monthlySummaryService;

    //이번 달 목표 지출 금액 설정
    @PostMapping("/goal/{year}/{month}")
    public String setMonthlyGoalAmount(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month,
            @RequestBody GoalAmountRequest req
    ) {
        LocalDate monthKey = LocalDate.of(year, month, 1);
        monthlySummaryService.updateGoalAmount(userId, monthKey, req.getAmount());
        return "월별 목표 지출 금액이 설정되었습니다.";
    }

    // 이번 달(또는 지정한 달)의 MonthlySummary 조회
    @GetMapping("/{year}/{month}")
    public MonthlySummary getMonthlySummary(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        LocalDate monthKey = LocalDate.of(year, month, 1);
        return monthlySummaryService.getMonthlySummary(userId, monthKey);
    }
}

package com.example.demo.calendar.service;
import com.example.demo.calendar.entity.MonthlySummary;
import com.example.demo.calendar.repository.MonthlySummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MonthlySummaryService {
    private final MonthlySummaryRepository monthlySummaryRepository;

    // 특정 달 Summary 검색 및 새로 생성
    public MonthlySummary getOrCreate(Long userId, LocalDate month) {
        LocalDate monthKey = month.withDayOfMonth(1);

        return monthlySummaryRepository.findByUserIdAndMonth(userId, monthKey)
                .orElseGet(() -> {
                    MonthlySummary summary = new MonthlySummary();
                    summary.setUserId(userId);
                    summary.setMonth(monthKey);
                    summary.setTotalExpenseAmount(0);
                    summary.setTotalIncomeAmount(0);
                    summary.setTotalGoalAmount(0);
                    return monthlySummaryRepository.save(summary);
                });
    }

    // 총 지출 금액 추가
    public void addExpense(Long userId, LocalDate month, int amount) {
        MonthlySummary summary = getOrCreate(userId, month);
        summary.setTotalExpenseAmount(summary.getTotalExpenseAmount() + amount);
        monthlySummaryRepository.save(summary);
    }

    // 총 수입 금액 추가
    public void addIncome(Long userId, LocalDate month, int amount) {
        MonthlySummary summary = getOrCreate(userId, month);
        summary.setTotalIncomeAmount(summary.getTotalIncomeAmount() + amount);
        monthlySummaryRepository.save(summary);
    }

    // 월 목표 금액 설정
    public void updateGoalAmount(Long userId, LocalDate month, int goalAmount) {
        MonthlySummary summary = getOrCreate(userId, month);
        summary.setTotalGoalAmount(goalAmount);
        monthlySummaryRepository.save(summary);
    }

    // 조회 API용
    public MonthlySummary getMonthlySummary(Long userId, LocalDate month) {
        return monthlySummaryRepository.findByUserIdAndMonth(userId, month.withDayOfMonth(1))
                .orElse(null);
    }
}

package com.example.demo.calendar.service;

import com.example.demo.calendar.dto.MonthlyResponse;
import com.example.demo.calendar.entity.DaySummary;
import com.example.demo.calendar.repository.DaySummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DaySummaryService {

    private final DaySummaryRepository summaryRepository;

    public DaySummaryService(DaySummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    // 특정 날짜 summary 조회 (없으면 새로 만듦)
    public DaySummary getOrCreateSummary(Long userId, LocalDate date) {
        return summaryRepository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> {
                    DaySummary newSummary = new DaySummary();
                    newSummary.setUserId(userId);
                    newSummary.setDate(date);
                    return summaryRepository.save(newSummary);
                });
    }

    // 수입 추가
    public void addIncome(Long userId, LocalDate date, int amount) {
        DaySummary summary = getOrCreateSummary(userId, date);
        summary.addIncome(amount);
        summaryRepository.save(summary);
    }

    // 지출 추가
    public void addExpense(Long userId, LocalDate date, int amount) {
        DaySummary summary = getOrCreateSummary(userId, date);
        summary.addExpense(amount);
        summaryRepository.save(summary);
    }

    // 특정 월 전체 요약 (일별 Summary 리스트만 반환)
    public List<DaySummary> getMonthlySummary(Long userId, LocalDate start, LocalDate end) {
        return summaryRepository.findByUserIdAndDateBetween(userId, start, end);
    }

    // =============================================
    // ⭐ 추가: 월 전체 합계 + 일별 요약 리스트 한번에 반환
    // =============================================
    public MonthlyResponse getMonthlySummaryWithTotal(Long userId, int year, int month) {

        // 1) 월 시작일 / 월 마지막 일 계산
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        // 2) 일별 요약 가져오기
        List<DaySummary> dailySummaries =
                summaryRepository.findByUserIdAndDateBetween(userId, start, end);

        // 3) 월 합계 계산
        int totalIncome = dailySummaries.stream()
                .map(DaySummary::getTotalIncome)
                .mapToInt(Integer::intValue)
                .sum();

        int totalExpense = dailySummaries.stream()
                .map(DaySummary::getTotalExpense)
                .mapToInt(Integer::intValue)
                .sum();

        int balance = totalIncome - totalExpense;

        // 4) DTO로 묶어서 반환
        return new MonthlyResponse(
                totalIncome,
                totalExpense,
                balance,
                dailySummaries
        );
    }
}

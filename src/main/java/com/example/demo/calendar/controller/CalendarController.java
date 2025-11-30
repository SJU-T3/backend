package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.TransactionRequest;
import com.example.demo.calendar.dto.MonthlyResponse;
import com.example.demo.calendar.entity.Transaction;
import com.example.demo.calendar.entity.DaySummary;
import com.example.demo.calendar.entity.Report;
import com.example.demo.calendar.repository.TransactionRepository;
import com.example.demo.calendar.service.TransactionService;
import com.example.demo.calendar.service.DaySummaryService;
import com.example.demo.calendar.service.ReportService;
import com.example.demo.calendar.dto.MonthlyCountResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final DaySummaryService daySummaryService;
    private final ReportService reportService;

    public CalendarController(TransactionService transactionService,
                              TransactionRepository transactionRepository,
                              DaySummaryService daySummaryService,
                              ReportService reportService ) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.daySummaryService = daySummaryService;
        this.reportService = reportService;
    }

    // =============================================
    // 1) 거래 등록 API (POST)
    // =============================================
    @PostMapping("/transaction")
    public Transaction createTransaction(
            @AuthenticationPrincipal Long userId,
            @RequestBody TransactionRequest req
    ) {

        // ★ 엔티티로 변환 (enum 변환 직접 처리 → 오류 절대 안 생김)
        Transaction tx = new Transaction();
        tx.setItemName(req.itemName);
        tx.setMemo(req.memo);
        tx.setPrice(req.price);
        tx.setDateTime(req.dateTime);

        // ENUM 변환
        tx.setCategory(Transaction.CategoryType.valueOf(req.category));
        tx.setIncomeType(Transaction.IncomeType.valueOf(req.incomeOrExpense));

        if (req.planType != null) {
            tx.setPlanType(Transaction.PlanType.valueOf(req.planType));
        } else {
            tx.setPlanType(null);
        }

        return transactionService.save(userId, tx);
    }

    // =============================================
    // 2) 특정 날짜의 상세 거래 조회
    // =============================================
    @GetMapping("/transactions/{date}")
    public List<Transaction> getTransactionsByDate(
            @AuthenticationPrincipal Long userId,
            @PathVariable String date
    ) {
        LocalDate targetDate = LocalDate.parse(date);
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        return transactionRepository.findByUserIdAndDateTimeBetween(
                userId, start, end
        );
    }

    // =============================================
    // 3) 특정 월의 전체 요약 + 월 합계 조회
    // =============================================
    @GetMapping("/summary/{year}/{month}")
    public MonthlyResponse getMonthlySummary(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        return daySummaryService.getMonthlySummaryWithTotal(userId, year, month);
    }

    @PostMapping("/report/{year}/{month}")
    public Report generateReport(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) throws Exception {
        return reportService.generateReport(userId, year, month);
    }

    @GetMapping("/report/{year}/{month}")
    public Report getReport(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        return reportService.getReport(userId, year, month);
    }

    @GetMapping("/count/{year}/{month}")
    public MonthlyCountResponse getMonthlyCounts(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        return transactionService.getMonthlyCounts(userId, year, month);
    }

}

package com.example.demo.calendar.service;

import com.example.demo.calendar.ai.AiClient;
import com.example.demo.calendar.dto.MonthlyResponse;
import com.example.demo.calendar.entity.Report;
import com.example.demo.calendar.entity.Transaction;
import com.example.demo.calendar.repository.ReportRepository;
import com.example.demo.calendar.repository.TransactionRepository;
import com.example.demo.calendar.ai.PromptBuilder;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {
    private final DaySummaryService daySummaryService;
    private final TransactionRepository transactionRepository;
    private final ReportRepository reportRepository;
    private final AiClient aiClient;

    public ReportService(
            DaySummaryService daySummaryService,
            TransactionRepository transactionRepository,
            ReportRepository reportRepository,
            AiClient aiClient
    ) {
        this.daySummaryService = daySummaryService;
        this.transactionRepository = transactionRepository;
        this.reportRepository = reportRepository;
        this.aiClient = aiClient;
    }

    public Report generateReport(Long userId, int year, int month) throws Exception {
        MonthlyResponse monthly = daySummaryService.getMonthlySummaryWithTotal(userId, year, month);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.now();
        List<Transaction> transactions =
                transactionRepository.findByUserIdAndDateTimeBetween(userId, start.atStartOfDay(), end.atTime(23,59));

        String prompt = PromptBuilder.build(monthly, transactions);

        String aiOutput = aiClient.invoke(prompt);

        String trimmedAiOutput = aiOutput.trim();

        JSONObject json = new JSONObject(aiOutput);
        try {
            // AiClient에서 최종 content만 받았기 때문에 바로 JSONObject로 변환
            json = new JSONObject(trimmedAiOutput);
        } catch (Exception e) {
            // 만약 GPT가 JSON 형식을 지키지 않았다면 예외 처리
            throw new Exception("AI 응답 파싱 실패: GPT가 유효한 JSON을 반환하지 않았습니다. 응답: " + trimmedAiOutput, e);
        }

        String summary = json.getString("summary");
        JSONObject feedback = json.getJSONObject("category_feedback");

        Report report = reportRepository
                .findByUserIdAndMonth(userId, "%04d-%02d".formatted(year, month))
                .orElse(new Report());

        report.setUserId(userId);
        report.setMonth("%04d-%02d".formatted(year, month));
        report.setUptoDate(LocalDateTime.now());
        report.setSummary(summary);
        report.setCategoryFeedback(feedback.toString());

        return reportRepository.save(report);
    }

    public Report getReport(Long userId, int year, int month) {
        return reportRepository
                .findByUserIdAndMonth(userId, "%04d-%02d".formatted(year, month))
                .orElse(null);
    }
}

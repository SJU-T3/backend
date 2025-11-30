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

    // ==========================================================
    // 1) 리포트 생성 (기존 코드 유지)
    // ==========================================================
    public Report generateReport(Long userId, int year, int month) throws Exception {
        MonthlyResponse monthly = daySummaryService.getMonthlySummaryWithTotal(userId, year, month);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.now();

        List<Transaction> transactions =
                transactionRepository.findByUserIdAndDateTimeBetween(
                        userId, start.atStartOfDay(), end.atTime(23,59)
                );

        // GPT 프롬프트 생성
        String prompt = PromptBuilder.build(monthly, transactions);

        String aiOutput = aiClient.invoke(prompt);
        String trimmedAiOutput = aiOutput.trim();

        // GPT 응답 JSON 파싱
        JSONObject json;
        try {
            json = new JSONObject(trimmedAiOutput);
        } catch (Exception e) {
            throw new Exception("AI 응답 JSON 파싱 실패: " + trimmedAiOutput, e);
        }

        String summary = json.getString("summary");
        String summary2 = json.getString("summary2");
        JSONObject feedback = json.getJSONObject("category_feedback");

        // 리포트 저장 또는 업데이트
        Report report = reportRepository
                .findByUserIdAndMonth(userId, "%04d-%02d".formatted(year, month))
                .orElse(new Report());

        report.setUserId(userId);
        report.setMonth("%04d-%02d".formatted(year, month));
        report.setUptoDate(LocalDateTime.now());
        report.setSummary(summary);
        report.setSummary2(summary2);
        report.setCategoryFeedback(feedback.toString());

        return reportRepository.save(report);
    }

    // ==========================================================
    // 2) 리포트 조회 (기존)
    // ==========================================================
    public Report getReport(Long userId, int year, int month) {
        return reportRepository
                .findByUserIdAndMonth(userId, "%04d-%02d".formatted(year, month))
                .orElse(null);
    }

    // ==========================================================
    // 3) 🔥 ChatService에서 쓰는 “리포트 전체 JSON 생성기”
    // ==========================================================
    public String getReportJson(Long userId, int year, int month) {

        Report report = reportRepository
                .findByUserIdAndMonth(userId, "%04d-%02d".formatted(year, month))
                .orElse(null);

        if (report == null) {
            return """
            {
                "message": "리포트가 아직 생성되지 않았습니다.",
                "summary": "",
                "category_feedback": {}
            }
            """;
        }

        JSONObject json = new JSONObject();
        json.put("month", report.getMonth());
        json.put("summary", report.getSummary());
        json.put("summary2", report.getSummary2());

        try {
            json.put("category_feedback", new JSONObject(report.getCategoryFeedback()));
        } catch (Exception e) {
            json.put("category_feedback", new JSONObject());
        }

        return json.toString();
    }
}

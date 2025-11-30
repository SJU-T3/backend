package com.example.demo.calendar.ai;

import com.example.demo.calendar.dto.MonthlyResponse;
import com.example.demo.calendar.entity.Transaction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class PromptBuilder {
    public static String build(MonthlyResponse monthly, List<Transaction> transactions) {

        Map<Transaction.CategoryType, Long> categoryTotals =
                transactions.stream()
                        .filter(t -> t.getIncomeType() == Transaction.IncomeType.EXPENSE)
                        .collect(Collectors.groupingBy(
                                Transaction::getCategory,
                                Collectors.summingLong(Transaction::getPrice)
                        ));

        return """
                이번 달 소비 요약 분석을 해주세요.
                응답은 해요체로 끝나야하고, 친근한 말투면 좋겠어.
                응답은 반드시 다음 구조를 가진 JSON 객체여야 합니다:
                - "summary": 한 줄 요약
                - "category_feedback": 각 카테고리(식비, 패션 등)에 대한 평가와 조언을 담은 객체

                총 수입: %d
                총 지출: %d
                잔액: %d

                카테고리별 지출:
                %s
                """.formatted(
                monthly.getTotalIncome(),
                monthly.getTotalExpense(),
                monthly.getBalance(),
                formatCategory(categoryTotals)
        );
    }

    private static String formatCategory(Map<Transaction.CategoryType, Long> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> sb.append(k.getDisplayName()).append(": ").append(v).append("\n"));
        return sb.toString();
    }
}

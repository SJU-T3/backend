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
                응답 전체는 반드시 하나의 JSON 객체만 포함해야 합니다.
                JSON 외의 텍스트나 설명은 절대 포함하지 마세요.
                
                JSON은 아래 정확한 필드를 포함해야 합니다:
                
                {
                  "summary": "한 줄 요약",
                  "summary_detailed": "세 줄 요약",
                  "category_feedback": {
                    "식비": "...",
                    "교통비": "...",
                    "주거/공과금": "...",
                    "교육": "...",
                    "건강/의료": "...",
                    "취미/여가": "...",
                    "패션": "...",
                    "술/유흥": "...",
                    "경조사": "...",
                    "여행/숙박": "...",
                    "생필품": "...",
                    "금융/보험/세금/통신비": "...",
                    "기타 지출": "..."
                  }
                }
                
                ⚠️ category_feedback 객체는 위의 키를 '문자 그대로 정확하게' 포함해야 합니다.
                한 글자도 수정, 확장, 변형되면 안 됩니다.
                
                각 카테고리의 값은:
                - 현재 소비 패턴 평가 한 문장
                - 개선 조언 한 문장
                총 두 문장으로 작성하세요.
                
                총 수입: %d원
                총 지출: %d원
                잔액: %d원
                
                카테고리별 지출 요약:
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

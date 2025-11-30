package com.example.demo.calendar.service;

import com.example.demo.calendar.entity.Transaction;
import com.example.demo.calendar.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.calendar.dto.MonthlyCountResponse;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final DaySummaryService daySummaryService;
    private final GoalService goalService;
    private final MonthlySummaryService monthlySummaryService;

    public TransactionService(TransactionRepository transactionRepository,
                              DaySummaryService daySummaryService,
                              GoalService goalService,
                              MonthlySummaryService monthlySummaryService) {
        this.transactionRepository = transactionRepository;
        this.daySummaryService = daySummaryService;
        this.goalService = goalService;
        this.monthlySummaryService = monthlySummaryService;
    }

    // 🔥 로그인한 userId를 받아서 저장
    public Transaction save(Long userId, Transaction tx) {

        System.out.println("[TX] saved transaction: " + tx.getItemName());
        System.out.println("[TX] userId = " + userId);

        // =========================================================
        // ⭐ 카테고리 기반 자동 수입/지출 분류 -> 카테고리 오류 예방
        // =========================================================
        if (tx.getCategory() == Transaction.CategoryType.SALARY ||
                tx.getCategory() == Transaction.CategoryType.ALLOWANCE ||
                tx.getCategory() == Transaction.CategoryType.REFUND ||
                tx.getCategory() == Transaction.CategoryType.INTEREST ||
                tx.getCategory() == Transaction.CategoryType.ETC ||
                tx.getCategory() == Transaction.CategoryType.SIDE_INCOME) {

            tx.setIncomeOrExpense(Transaction.IncomeType.INCOME);
        } else {
            tx.setIncomeOrExpense(Transaction.IncomeType.EXPENSE);
        }

        System.out.println("[TX] auto-detected incomeOrExpense = " + tx.getIncomeOrExpense());

        // =========================================================
        // ⭐ 수입이면 planType 제거 (null 설정)
        // =========================================================
        if (tx.getIncomeOrExpense() == Transaction.IncomeType.INCOME) {
            tx.setPlanType(null);
        }

        // =========================================================


        // 1) 거래에 userId 세팅
        tx.setUserId(userId);

        // 2) 거래 저장
        Transaction saved = transactionRepository.save(tx);

        // 3) DaySummary / MonthlySummary 자동 업데이트
        LocalDate date = tx.getDateTime().toLocalDate();
        LocalDate monthKey = date.withDayOfMonth(1);

        if (tx.getIncomeOrExpense() == Transaction.IncomeType.INCOME) {

            daySummaryService.addIncome(userId, date, tx.getPrice());
            monthlySummaryService.addIncome(userId, monthKey, tx.getPrice());

        } else {

            System.out.println("[TX] expense detected. Checking goal...");
            daySummaryService.addExpense(userId, date, tx.getPrice());
            goalService.checkAndIncrease(userId, tx.getItemName(), tx.getDateTime().toLocalDate());
            monthlySummaryService.addExpense(userId, monthKey, tx.getPrice());
        }

        return saved;
    }

    public MonthlyCountResponse getMonthlyCounts(Long userId, int year, int month) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = startDate.plusMonths(1).atStartOfDay();

        long total = transactionRepository.countByUserIdAndDateTimeBetween(userId, start, end);
        long impulse = transactionRepository.countByUserIdAndPlanTypeAndDateTimeBetween(
                userId,
                Transaction.PlanType.IMPULSE,
                start,
                end
        );
        long planned = transactionRepository.countByUserIdAndPlanTypeAndDateTimeBetween(
                userId,
                Transaction.PlanType.PLANNED,
                start,
                end
        );

        return new MonthlyCountResponse(total, impulse, planned);
    }

}

package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 특정 유저의 특정 날짜 구간 상세 기록 조회
    List<Transaction> findByUserIdAndDateTimeBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    // 특정 유저의 전체 내역 조회 (필요할 때 사용)
    List<Transaction> findByUserId(Long userId);

    long countByUserIdAndDateTimeBetween(Long userId,
                                         LocalDateTime start,
                                         LocalDateTime end);

    // 특정 기간 "충동 소비" count
    long countByUserIdAndPlanTypeAndDateTimeBetween(
            Long userId,
            Transaction.PlanType impulseOrPlanned,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByUserIdAndIncomeTypeAndDateTimeBetween(
            Long userId,
            Transaction.IncomeType incomeOrExpense,
            LocalDateTime start,
            LocalDateTime end
    );

}

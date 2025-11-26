package com.example.demo.calendar.dto;

import com.example.demo.calendar.entity.DaySummary;
import java.util.List;

public class MonthlyResponse {

    private int totalIncome;     // 월 전체 수입
    private int totalExpense;    // 월 전체 지출
    private int balance;         // 월 잔액 (수입 - 지출)
    private List<DaySummary> dailySummaries;  // 일별 요약 리스트

    public MonthlyResponse(int totalIncome,
                                  int totalExpense,
                                  int balance,
                                  List<DaySummary> dailySummaries) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.dailySummaries = dailySummaries;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public int getTotalExpense() {
        return totalExpense;
    }

    public int getBalance() {
        return balance;
    }

    public List<DaySummary> getDailySummaries() {
        return dailySummaries;
    }

}

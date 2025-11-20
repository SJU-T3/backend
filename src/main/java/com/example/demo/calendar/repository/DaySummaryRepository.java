package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.DaySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DaySummaryRepository extends JpaRepository<DaySummary, Long> {

    // 특정 날짜의 summary 불러오기
    Optional<DaySummary> findByUserIdAndDate(Long userId, LocalDate date);

    // 한 달 전체 summary 불러오기 (달력 표시용)
    List<DaySummary> findByUserIdAndDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end
    );
}

package com.example.demo.calendar.repository;
import com.example.demo.calendar.entity.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long>{
    Optional<MonthlySummary> findByUserIdAndMonth(Long userId, LocalDate month);
}

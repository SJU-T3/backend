package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>{
    Optional<Report> findByUserIdAndMonth(Long userId, String month);
}

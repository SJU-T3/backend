package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long>{
    Optional<Goal> findByUserIdAndMonth(Long userId, LocalDate month);
}

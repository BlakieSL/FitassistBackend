package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
    Optional<DailyActivity> findByUserId(Integer userId);
}
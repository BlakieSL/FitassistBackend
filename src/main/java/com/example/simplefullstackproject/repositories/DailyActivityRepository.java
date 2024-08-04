package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
    Optional<DailyActivity> findByUserId(Integer userId);
}
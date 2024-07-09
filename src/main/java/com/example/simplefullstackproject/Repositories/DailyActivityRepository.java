package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Integer> {
    Optional<DailyActivity> findByUserId(Integer userId);
}
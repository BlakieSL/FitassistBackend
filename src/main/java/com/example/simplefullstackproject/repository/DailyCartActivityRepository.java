package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.DailyCartActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartActivityRepository extends JpaRepository<DailyCartActivity, Integer> {
}
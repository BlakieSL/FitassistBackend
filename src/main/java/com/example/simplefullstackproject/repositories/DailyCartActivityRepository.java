package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.DailyCartActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartActivityRepository extends JpaRepository<DailyCartActivity, Integer> {
}
package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.DailyCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {
}
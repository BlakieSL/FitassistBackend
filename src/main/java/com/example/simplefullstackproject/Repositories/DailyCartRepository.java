package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.DailyCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {
    Optional<DailyCart> findByUserId(Integer id);
}
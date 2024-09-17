package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.DailyCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {
    Optional<DailyCart> findByUserId(int id);

    Optional<DailyCart> findByUserIdAndDate(int id, LocalDate date);

    void removeDailyCartByUserId(int userId);
}
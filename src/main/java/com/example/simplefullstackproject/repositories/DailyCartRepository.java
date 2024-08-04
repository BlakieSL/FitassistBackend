package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.DailyCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {
    Optional<DailyCart> findByUserId(Integer id);

    Optional<DailyCart> findByUserIdAndDate(Integer id, LocalDate date);

    void removeDailyCartByUserId(Integer userId);
}
package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.DailyCart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCartRepository extends JpaRepository<DailyCart, Integer> {
    Optional<DailyCart> findByUserId(Integer id);
    Optional<DailyCart> findByUserIdAndDate(Integer id, LocalDate date);
    void removeDailyCartByUserId(Integer userId);
}
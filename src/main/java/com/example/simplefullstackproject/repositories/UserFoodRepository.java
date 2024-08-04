package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.UserFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFoodRepository extends JpaRepository<UserFood, Integer> {
    boolean existsByUserIdAndFoodId(Integer userId, Integer foodId);
    Optional<UserFood> findByUserIdAndFoodId(Integer userId, Integer foodId);
    List<UserFood> findByUserId(Integer userId);
}
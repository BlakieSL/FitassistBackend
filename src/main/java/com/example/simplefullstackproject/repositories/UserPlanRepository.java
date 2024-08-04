package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPlanRepository extends JpaRepository<UserPlan, Long> {
    List<UserPlan> findByUserId(Integer userId);
    Optional<UserPlan> findByUserIdAndPlanId(Integer userId, Integer planId);
    boolean existsByUserIdAndPlanId(Integer userId, Integer planId);
}
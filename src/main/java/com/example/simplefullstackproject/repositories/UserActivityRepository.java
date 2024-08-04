package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    boolean existsByUserIdAndActivityId(Integer userId, Integer activityId);
    Optional<UserActivity> findByUserIdAndActivityId(Integer userId, Integer activityId);
    List<UserActivity> findByUserId(Integer userId);
}
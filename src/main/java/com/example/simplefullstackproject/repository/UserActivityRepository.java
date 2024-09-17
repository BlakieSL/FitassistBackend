package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    boolean existsByUserIdAndActivityIdAndType(int userId, int activityId, short type);
    Optional<UserActivity> findByUserIdAndActivityIdAndType(int userId, int activityId, short type);
    List<UserActivity> findByUserId(int userId);
    long countByActivityIdAndType(int activityId, short type);
}
package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPlanRepository extends JpaRepository<UserPlan, Integer> {
}
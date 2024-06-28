package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
}
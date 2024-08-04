package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    List<Activity> findAllByActivityCategory_Id(Integer categoryId);
    List<Activity> findAllByNameContainingIgnoreCase(String name);
}
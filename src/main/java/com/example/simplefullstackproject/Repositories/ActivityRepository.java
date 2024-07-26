package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Dtos.ActivityDto;
import com.example.simplefullstackproject.Models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    List<Activity> findAllByActivityCategory_Id(Integer categoryId);
    List<Activity> findAllByNameContainingIgnoreCase(String name);
}
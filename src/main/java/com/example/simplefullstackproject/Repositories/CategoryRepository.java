package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
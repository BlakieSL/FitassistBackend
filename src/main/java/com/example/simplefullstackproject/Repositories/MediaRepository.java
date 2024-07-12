package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Integer> {
}
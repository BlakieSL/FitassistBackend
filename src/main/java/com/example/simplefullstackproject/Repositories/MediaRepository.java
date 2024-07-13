package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Integer> {
    List<Media> findByParentId(Integer parentId);
    Optional<Media> findByIdAndParentId(Integer id, Integer parentId);
}
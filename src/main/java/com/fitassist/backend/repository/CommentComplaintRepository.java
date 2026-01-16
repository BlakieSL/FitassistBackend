package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.complaint.CommentComplaint;

public interface CommentComplaintRepository extends JpaRepository<CommentComplaint, Integer> {

}

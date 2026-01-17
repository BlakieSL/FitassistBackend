package com.fitassist.backend.repository;

import com.fitassist.backend.model.complaint.CommentComplaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentComplaintRepository extends JpaRepository<CommentComplaint, Integer> {

}

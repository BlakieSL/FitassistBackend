package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.complaint.ThreadComplaint;

public interface ThreadComplaintRepository extends JpaRepository<ThreadComplaint, Integer> {

}

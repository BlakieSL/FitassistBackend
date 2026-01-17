package com.fitassist.backend.repository;

import com.fitassist.backend.model.complaint.ThreadComplaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadComplaintRepository extends JpaRepository<ThreadComplaint, Integer> {

}

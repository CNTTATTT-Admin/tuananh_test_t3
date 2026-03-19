package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission,Long>,JpaSpecificationExecutor<Submission>{

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);
    
    List<Submission> findByAssignment_ClassRoom_LecturerId(Long lecturerId);
}

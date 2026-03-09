package com.checkplagiarism.plagiarism.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission,Long>,JpaSpecificationExecutor<Submission>{
    
}

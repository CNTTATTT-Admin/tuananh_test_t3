package com.checkplagiarism.plagiarism.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;

@Repository
public interface PlagiarismCheckRepository extends JpaRepository<PlagiarismCheck,Long>{

    PlagiarismCheck findBySubmissionId(Long submissionId);
    
}

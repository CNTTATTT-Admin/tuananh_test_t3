package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.PlagiarismMatch;

@Repository
public interface PlagiarismMatchRepository extends JpaRepository<PlagiarismMatch,Long>{

    List<PlagiarismMatch> findByCheckId(Long id);
    
}

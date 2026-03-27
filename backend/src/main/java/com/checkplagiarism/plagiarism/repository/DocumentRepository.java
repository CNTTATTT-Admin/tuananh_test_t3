package com.checkplagiarism.plagiarism.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long>{

    Optional<Document> findByHash(String hash);
    
}

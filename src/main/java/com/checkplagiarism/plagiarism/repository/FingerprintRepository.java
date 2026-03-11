package com.checkplagiarism.plagiarism.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.DocumentFingerprint;

@Repository
public interface FingerprintRepository extends JpaRepository<DocumentFingerprint,Long>{

    List<DocumentFingerprint> findByHashValueIn(Set<Long> hashes);
    
}

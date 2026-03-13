package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.PlagiarismThresholds;

@Repository
public interface PlagiarismThresholdRepository extends JpaRepository<PlagiarismThresholds,Long>,JpaSpecificationExecutor<PlagiarismThresholds>{

    List<PlagiarismThresholds> findByClassRoomId(Long classId);

    List<PlagiarismThresholds> findByIsDefaultTrue();

    List<PlagiarismThresholds> findByClassRoomIdOrderByMaxAsc(Long classId);

    List<PlagiarismThresholds> findByClassRoomIdOrderByMinAsc(Long id);
    
}

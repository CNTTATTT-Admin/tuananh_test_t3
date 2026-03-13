package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment,Long>, JpaSpecificationExecutor<Assignment>{
    List<Assignment> findByClassRoomId(Long classId);
}

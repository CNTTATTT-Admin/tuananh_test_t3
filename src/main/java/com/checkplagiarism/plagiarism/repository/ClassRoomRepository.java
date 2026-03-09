package com.checkplagiarism.plagiarism.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.ClassRoom;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long>,JpaSpecificationExecutor<ClassRoom>{
    ClassRoom findByClassCode(String classCode);
}

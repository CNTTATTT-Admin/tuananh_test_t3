package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.ClassStudent;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent,Long>,JpaSpecificationExecutor<ClassStudent>{
    List<ClassStudent> findByClassRoomId(Long Id);
    ClassStudent findByUserAndClassRoom(Long userId, Long classId);


}

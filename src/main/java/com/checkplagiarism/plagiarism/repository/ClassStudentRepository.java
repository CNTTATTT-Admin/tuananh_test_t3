package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.ClassStudent;
import com.checkplagiarism.plagiarism.util.constants.ClassStatusEnum;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent,Long>,JpaSpecificationExecutor<ClassStudent>{
    List<ClassStudent> findByClassRoomId(Long Id);
    ClassStudent findByUserIdAndClassRoomId(Long userId, Long classId);

    List<ClassStudent> findByUserId(Long id);

    List<ClassStudent> findByUserIdAndStatus(Long id, ClassStatusEnum status);

    boolean existsByUserIdAndClassRoomId(Long userId,Long classId);

}

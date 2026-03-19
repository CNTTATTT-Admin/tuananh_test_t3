package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.ClassRoom;
import com.checkplagiarism.plagiarism.domain.User;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long>, JpaSpecificationExecutor<ClassRoom> {
    ClassRoom findByClassCode(String classCode);

    List<ClassRoom> findByLecturer(User user);

    Page<ClassRoom> findByLecturer(User user, Pageable pageable);

    Page<ClassRoom> findByIdIn(List<Long> ids, Pageable pageable);
}

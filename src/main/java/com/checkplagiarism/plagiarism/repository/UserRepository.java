package com.checkplagiarism.plagiarism.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>,JpaSpecificationExecutor<User>{
    User findByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);
}

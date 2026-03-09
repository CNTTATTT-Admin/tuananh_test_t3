package com.checkplagiarism.plagiarism.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.request.user.ReqCreateUserDTO;
import com.checkplagiarism.plagiarism.domain.request.user.ReqUpdateUserDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResCreateUserDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResFetchUserDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResUpdateUserDTO;
import com.checkplagiarism.plagiarism.service.UserService;
import com.checkplagiarism.plagiarism.util.err.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody ReqCreateUserDTO req) throws IdInvalidException {
        User user = this.userService.findByEmail(req.getEmail());
        if (user!=null) {
            throw new IdInvalidException("email is exist");
        }
        user= this.userService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUser(user));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser( @RequestBody ReqUpdateUserDTO req) throws IdInvalidException {
        User user= this.userService.getUserById(req.getId());
        if (user==null) {
            throw new IdInvalidException("user not found");
        }
        
        return ResponseEntity.ok().body(this.userService.convertToResUpdateUser(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable ("id") Long id) throws IdInvalidException {
        User user=this.userService.getUserById(id);
        if(user==null){
            throw new IdInvalidException("user not found");
        }
        this.userService.handleDelete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResFetchUserDTO> getUserById(@PathVariable ("id") Long id) throws IdInvalidException {
        User cur=this.userService.getUserById(id);
        if (cur==null) {
            throw new IdInvalidException("user not found");
        }
        return ResponseEntity.ok().body(this.userService.convertToResFetchUser(cur));
    }


    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec,Pageable page) {
        return ResponseEntity.ok().body(this.userService.handleGetAll(spec, page));
    }
    
    
}

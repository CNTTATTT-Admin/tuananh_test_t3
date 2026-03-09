package com.checkplagiarism.plagiarism.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.request.auth.ReqRegisterDTO;
import com.checkplagiarism.plagiarism.domain.request.user.ReqCreateUserDTO;
import com.checkplagiarism.plagiarism.domain.request.user.ReqUpdateUserDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResCreateUserDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResFetchUserDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResUpdateUserDTO;
import com.checkplagiarism.plagiarism.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(ReqCreateUserDTO req){
        User user=new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        String password= this.passwordEncoder.encode(req.getPassword());
        user.setPassword(password);
        user.setAge(req.getAge());
        user.setGender(req.getGender());
        return this.userRepository.save(user);
    }

    public User getUserById(long id){
        return this.userRepository.findById(id).orElse(null);
    }

    public User updateUser(ReqUpdateUserDTO req){
        User user= this.getUserById(req.getId());
        if (user!=null) {
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPassword(req.getPassword());
            user.setAge(req.getAge());
            user.setGender(req.getGender());
            user= this.userRepository.save(user);
        }
        return user;
    }

    public void handleDelete(Long id) {
        this.userRepository.deleteById(id);
    }

      public ResultPaginationDTO handleGetAll(Specification<User> spec, Pageable page){
        Page<User> user= this.userRepository.findAll(spec, page);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(user.getNumber() + 1);
        meta.setPageSize(user.getSize());
        meta.setPages(user.getTotalPages());
        meta.setTotal(user.getTotalElements());

        List<ResFetchUserDTO> list=user.getContent().stream().map(item->this.convertToResFetchUser(item)).collect(Collectors.toList());

        rs.setMeta(meta);
        rs.setResults(list);
        return rs;
    }

    public ResFetchUserDTO convertToResFetchUser(User user) {
        ResFetchUserDTO res = new ResFetchUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        return res;
    }
    
    public ResCreateUserDTO convertToResCreateUser(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        return res;
    }
    
    public ResUpdateUserDTO convertToResUpdateUser(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        return res;
    }
    public User findByEmail(String email){
      return  this.userRepository.findByEmail(email);
    }

    public User findByUsername(String email){
        return this.userRepository.findByEmail(email);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.findByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public User register(ReqRegisterDTO req) {
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        return this.userRepository.save(user);
    }
}

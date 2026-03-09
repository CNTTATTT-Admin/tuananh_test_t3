package com.checkplagiarism.plagiarism.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.ClassRoom;
import com.checkplagiarism.plagiarism.domain.ClassStudent;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqAddToClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqCreateClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqUpdateStudentClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqUpdateClassDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.domain.response.classes.ResClassStudentDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResFetchUserDTO;
import com.checkplagiarism.plagiarism.repository.ClassRoomRepository;
import com.checkplagiarism.plagiarism.repository.ClassStudentRepository;
import com.checkplagiarism.plagiarism.repository.UserRepository;
import com.checkplagiarism.plagiarism.util.SecurityUtil;
import com.checkplagiarism.plagiarism.util.constants.ClassStatusEnum;
import com.checkplagiarism.plagiarism.util.constants.RoleEnum;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ClassRoomService {
    private final ClassRoomRepository classRoomRepository;
    private final UserRepository userRepository;
    private final ClassStudentRepository classStudentRepository;
    private final SecurityUtil securityUtil;

    //role lecturer
    public ClassRoom createClass(ReqCreateClassDTO req){
        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(req.getName());
        classRoom.setDescription(req.getDescription());

        return this.classRoomRepository.save(classRoom);
    }
    
    // role lecturer
    public ClassRoom updateClass(ReqUpdateClassDTO req){
        ClassRoom classRoom = this.classRoomRepository.findById(req.getId()).orElse(null);
        if (classRoom!=null) {   
            classRoom.setName(req.getName());
            classRoom.setDescription(req.getDescription());
            classRoom= this.classRoomRepository.save(classRoom);
        }

        return classRoom;
    }

    // role lecturer
    public ClassRoom getByClassRoomId(Long id){
        return this.classRoomRepository.findById(id).orElse(null);
    }

    // role lecturer
        public ResultPaginationDTO handleGetAll(Specification<ClassRoom> spec, Pageable page){
        Page<ClassRoom> classRoom= this.classRoomRepository.findAll(spec, page);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(classRoom.getNumber() + 1);
        meta.setPageSize(classRoom.getSize());
        meta.setPages(classRoom.getTotalPages());
        meta.setTotal(classRoom.getTotalElements());

        rs.setMeta(meta);
        rs.setResults(classRoom.getContent());
        return rs;
    }

    // role lecturer
    public void deleteByClassId(Long id){
        List<ClassStudent> classStudents= this.classStudentRepository.findByClassRoomId(id);
        for(ClassStudent cl:classStudents){
            this.classStudentRepository.deleteById(cl.getId());
        }
        this.classRoomRepository.deleteById(id);
    }

    // role lecturer
    public void addStudentToClass(ReqAddToClassDTO req){
        ClassRoom classRoom= this.classRoomRepository.findById(req.getClassId()).orElse(null);
        User user= this.userRepository.findByEmail(req.getEmail());
        ClassStudent classStudent = new ClassStudent();
        if (classRoom !=null && user!=null) {
            classStudent.setUser(user);
            classStudent.setClassRoom(classRoom);
            classStudent.setJoinAt(Instant.now());
            classStudent.setStatus(ClassStatusEnum.APPROVED);
            classStudent.setRoleInClass(RoleEnum.STUDENT);
            this.classStudentRepository.save(classStudent);
        }

    }

    // role lecturer
    public void removeStudentFromClass(Long userId, Long classId){
        ClassStudent classStudent = this.classStudentRepository.findByUserAndClassRoom(userId,classId);
        this.classStudentRepository.deleteById(classStudent.getId());

    }
    
    // role lecturer
    public void approved(ReqUpdateStudentClassDTO req) {
        ClassStudent classStudent = this.classStudentRepository.findByUserAndClassRoom(req.getUserId(),
                req.getClassId());
        classStudent.setStatus(ClassStatusEnum.APPROVED);
        this.classStudentRepository.save(classStudent);
    }

    // role lecturer
    public void rejected(ReqUpdateStudentClassDTO req) {
        ClassStudent classStudent = this.classStudentRepository.findByUserAndClassRoom(req.getUserId(),
                req.getClassId());
        classStudent.setStatus(ClassStatusEnum.REJECTED);
        this.classStudentRepository.save(classStudent);
    }

    public void joinClass(String classCode){
        ClassRoom classRoom= this.classRoomRepository.findByClassCode(classCode);
        String email= this.securityUtil.getCurrentUserLogin().isPresent()? this.securityUtil.getCurrentUserLogin().get() :null;
        User user= this.userRepository.findByEmail(email);

        if (classRoom!=null) {
            ClassStudent classStudent= new ClassStudent();
            classStudent.setUser(user);
            classStudent.setClassRoom(classRoom);
            classStudent.setJoinAt(Instant.now());
            classStudent.setStatus(ClassStatusEnum.PENDING);
            classStudent.setRoleInClass(RoleEnum.STUDENT);
            this.classStudentRepository.save(classStudent);
        }
    }

    public void leaveClass(Long classId){
        String email= this.securityUtil.getCurrentUserLogin().isPresent()? this.securityUtil.getCurrentUserLogin().get() :null;
        User user= this.userRepository.findByEmail(email);

        ClassStudent classStudent= this.classStudentRepository.findByUserAndClassRoom(user.getId(),classId);
        classStudent.setStatus(ClassStatusEnum.REMOVED);
        this.classStudentRepository.save(classStudent);
    }

    public ClassRoom findByClassCode(String classCode){
        return this.classRoomRepository.findByClassCode(classCode);
    }

    public ClassStudent findByUserAndClass(Long userId, Long classId){
        return this.classStudentRepository.findByUserAndClassRoom(userId, classId);
    }

    public ResClassStudentDTO getUserFromClass(long classId){
        List<ClassStudent> list = this.classStudentRepository.findByClassRoomId(classId);
        ClassRoom classRoom= this.classRoomRepository.findById(classId).orElse(null);
        ResClassStudentDTO res= new ResClassStudentDTO();
        ResClassStudentDTO.classRoomInner cRoomInner= new ResClassStudentDTO.classRoomInner(classRoom.getId(),classRoom.getName(),classRoom.getDescription(),classRoom.getClassCode());
        res.setClassRoom(cRoomInner);
        List<ResClassStudentDTO.userInner> listUser= new ArrayList<>();
        for(ClassStudent cl: list){
            ResClassStudentDTO.userInner user= new ResClassStudentDTO.userInner();
            user.setId(cl.getUser().getId());
            user.setEmail(cl.getUser().getEmail());
            user.setName(cl.getUser().getName());
            listUser.add(user);
        }
        res.setUser(listUser);
        return res;
    }
}

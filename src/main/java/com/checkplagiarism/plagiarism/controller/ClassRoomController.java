package com.checkplagiarism.plagiarism.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkplagiarism.plagiarism.domain.ClassRoom;
import com.checkplagiarism.plagiarism.domain.ClassStudent;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqAddToClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqCreateClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqJoinClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqLeaveClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqUpdateClassDTO;
import com.checkplagiarism.plagiarism.domain.request.classroom.ReqUpdateStudentClassDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.domain.response.classes.ResClassLecturerDTO;
import com.checkplagiarism.plagiarism.domain.response.classes.ResClassStudentDTO;
import com.checkplagiarism.plagiarism.domain.response.classes.ResGetClassByLecturerDTO;
import com.checkplagiarism.plagiarism.domain.response.classes.ResGetClassByUserDTO;
import com.checkplagiarism.plagiarism.repository.ClassStudentRepository;
import com.checkplagiarism.plagiarism.service.ClassRoomService;
import com.checkplagiarism.plagiarism.service.UserService;
import com.checkplagiarism.plagiarism.util.SecurityUtil;
import com.checkplagiarism.plagiarism.util.err.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ClassRoomController {
    private final ClassRoomService classRoomService;
    private final UserService userService;

    @PostMapping("/classes")
    public ResponseEntity<ResClassLecturerDTO> createClassRoom(@RequestBody ReqCreateClassDTO req) {
        ClassRoom classRoom= this.classRoomService.createClass(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.classRoomService.convResClassLecturerDTO(classRoom));
    }

    @PutMapping("/classes")
    public ResponseEntity<ResClassLecturerDTO> updateClassRoom( @RequestBody ReqUpdateClassDTO req) throws IdInvalidException {
        ClassRoom classRoom= this.classRoomService.getByClassRoomId(req.getId());
        if (classRoom==null) {
            throw new IdInvalidException("class room not found");
        }
        return ResponseEntity.ok().body(this.classRoomService.convResClassLecturerDTO(classRoom));
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<ResClassLecturerDTO> getClassRoomById(@PathVariable Long id) throws IdInvalidException {
        ClassRoom classRoom = this.classRoomService.getByClassRoomId(id);
        if (classRoom == null) {
            throw new IdInvalidException("class room not found");
        }
        return ResponseEntity.ok().body(this.classRoomService.convResClassLecturerDTO(classRoom));
    }

        @GetMapping("/classes")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<ClassRoom> spec,Pageable page) {
        return ResponseEntity.ok().body(this.classRoomService.handleGetAll(spec, page));
    }
    
    @DeleteMapping("/classes/{id}")
    public ResponseEntity<?> deleteClassRoomById(@PathVariable Long id) throws IdInvalidException {
        ClassRoom classRoom = this.classRoomService.getByClassRoomId(id);
        if (classRoom == null) {
            throw new IdInvalidException("class room not found");
        }
        this.classRoomService.deleteByClassId(id);
        return ResponseEntity.ok().body("delete success");
    }

    @PostMapping("/classes/add")
    public ResponseEntity<?> addToClassRoom(@RequestBody ReqAddToClassDTO req) throws IdInvalidException {
        User user= this.userService.findByEmail(req.getEmail());
        if (user==null) {
            throw new IdInvalidException("user not found");
        }
        this.classRoomService.addStudentToClass(req);
        return ResponseEntity.ok().body("add student to class success");
    }

    @DeleteMapping("/classes/{classId}/remove/{userId}")
    public ResponseEntity<?> deleteClassRoomById(@PathVariable Long classId,
            @PathVariable Long userId ) throws IdInvalidException {
        User user = this.userService.getUserById(userId);
        if (user == null) {
            throw new IdInvalidException("user not found");
        }
        this.classRoomService.removeStudentFromClass(userId,classId);
        return ResponseEntity.ok().body("delete success");
    }

    @PutMapping("/classes/approved")
    public ResponseEntity<?> approvedStudent(@RequestBody ReqUpdateStudentClassDTO req) throws IdInvalidException {
        ClassStudent classStudent= this.classRoomService.findByUserAndClass(req.getUserId(),req.getClassId());
        if (classStudent == null) {
            throw new IdInvalidException("user not found in class");
        }
        this.classRoomService.approved(req);
        return ResponseEntity.ok().body("approved success");
    }
    
    @PutMapping("/classes/rejected")
    public ResponseEntity<?> rejectedStudent(@RequestBody ReqUpdateStudentClassDTO req) throws IdInvalidException {
        ClassStudent classStudent = this.classRoomService.findByUserAndClass(req.getUserId(), req.getClassId());
        if (classStudent == null) {
            throw new IdInvalidException("user not found in class");
        }
        this.classRoomService.rejected(req);
        return ResponseEntity.ok().body("approved success");
    }



    @PostMapping("/classes/join")
    public ResponseEntity<?> joinClassRoom(@RequestBody ReqJoinClassDTO req) throws IdInvalidException {
        ClassRoom classRoom= this.classRoomService.findByClassCode(req.getClassCode());
        if (classRoom == null) {
            throw new IdInvalidException("class not found");
        }
        this.classRoomService.joinClass(req);
        return ResponseEntity.ok().body("join class success");
    }

    @PutMapping("/classes/leave")
    public ResponseEntity<?> leaveClassRoom(@RequestBody ReqLeaveClassDTO req) throws IdInvalidException {
        ClassRoom classRoom = this.classRoomService.getByClassRoomId(req.getClassId());
        if (classRoom == null) {
            throw new IdInvalidException("class room not found");
        }
        this.classRoomService.leaveClass(req);
        return ResponseEntity.ok().body("leave class success");
    }

    @GetMapping("/classes/user/{classId}")
    public ResponseEntity<ResClassStudentDTO> getAllStudent(@PathVariable("classId") Long classId) {
        return ResponseEntity.ok().body(this.classRoomService.getUserFromClass(classId));
    }

    @GetMapping("/classes/user")
    public ResponseEntity<ResGetClassByUserDTO> getClassByUser() {
        return ResponseEntity.ok().body(this.classRoomService.getClassByUserId());
    }

    @GetMapping("/classes/lecturer")
    public ResponseEntity<ResGetClassByLecturerDTO> getClassByLecturer() {
        return ResponseEntity.ok().body(this.classRoomService.getClassByLecturer());
    }

    

    
}

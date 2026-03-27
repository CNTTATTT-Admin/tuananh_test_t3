package com.checkplagiarism.plagiarism.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    // role lecturer
    public ClassRoom createClass(ReqCreateClassDTO req) {
        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(req.getName());
        classRoom.setDescription(req.getDescription());

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email != null) {
            User user = this.userRepository.findByEmail(email);
            classRoom.setLecturer(user);
        }

        return this.classRoomRepository.save(classRoom);
    }

    // role lecturer
    public ClassRoom updateClass(ReqUpdateClassDTO req) {
        ClassRoom classRoom = this.classRoomRepository.findById(req.getId()).orElse(null);
        if (classRoom != null) {
            classRoom.setName(req.getName());
            classRoom.setDescription(req.getDescription());
            classRoom = this.classRoomRepository.save(classRoom);
        }

        return classRoom;
    }

    // role lecturer
    public ClassRoom getByClassRoomId(Long id) {
        return this.classRoomRepository.findById(id).orElse(null);
    }

    // role lecturer & student
    public ResultPaginationDTO handleGetAll(Specification<ClassRoom> spec, Pageable page) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User user = this.userRepository.findByEmail(email);

        if (user != null && user.getRole() != null) {
            String roleName = user.getRole().getName();
            // If lecturer, only see classes they created
            if (roleName.equals("ROLE_LECTURER") || roleName.equals("LECTURER")) {
                Page<ClassRoom> classes = this.classRoomRepository.findByLecturer(user, page);
                return this.buildPaginationDTO(classes);
            }
            // If student, see only classes they joined AND were approved
            if (roleName.equals("ROLE_STUDENT") || roleName.equals("STUDENT")) {
                List<ClassStudent> memberships = this.classStudentRepository.findByUserId(user.getId());
                // Only take approved ones
                List<Long> approvedClassIds = memberships.stream()
                        .filter(m -> m.getStatus() == ClassStatusEnum.APPROVED)
                        .map(m -> m.getClassRoom().getId())
                        .toList();

                if (approvedClassIds.isEmpty()) {
                    return this.buildPaginationDTO(Page.empty(page));
                }
                Page<ClassRoom> classes = this.classRoomRepository.findByIdIn(approvedClassIds, page);
                return this.buildPaginationDTO(classes);
            }
        }

        // Default or Admin: see all
        Page<ClassRoom> classRoom = this.classRoomRepository.findAll(spec, page);
        return this.buildPaginationDTO(classRoom);
    }

    private ResultPaginationDTO buildPaginationDTO(Page<ClassRoom> page) {
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        rs.setMeta(meta);
        rs.setResults(page.getContent());
        return rs;
    }

    // role lecturer
    public void deleteByClassId(Long id) {
        List<ClassStudent> classStudents = this.classStudentRepository.findByClassRoomId(id);
        for (ClassStudent cl : classStudents) {
            this.classStudentRepository.deleteById(cl.getId());
        }
        this.classRoomRepository.deleteById(id);
    }

    // role lecturer
    public void addStudentToClass(ReqAddToClassDTO req) {
        ClassRoom classRoom = this.classRoomRepository.findById(req.getClassId()).orElse(null);
        User user = this.userRepository.findByEmail(req.getEmail());
        ClassStudent classStudent = new ClassStudent();
        if (classRoom != null && user != null) {
            classStudent.setUser(user);
            classStudent.setClassRoom(classRoom);
            classStudent.setJoinAt(Instant.now());
            classStudent.setStatus(ClassStatusEnum.APPROVED);
            classStudent.setRoleInClass(RoleEnum.STUDENT);
            this.classStudentRepository.save(classStudent);
        }

    }

    // role lecturer
    public void removeStudentFromClass(Long userId, Long classId) {
        ClassStudent classStudent = this.classStudentRepository.findByUserIdAndClassRoomId(userId, classId);
        if (classStudent != null) {
            this.classStudentRepository.deleteById(classStudent.getId());
        }
    }

    // role lecturer
    public void approved(ReqUpdateStudentClassDTO req) {
        ClassStudent classStudent = this.classStudentRepository.findByUserIdAndClassRoomId(req.getUserId(),
                req.getClassId());
        if (classStudent != null) {
            classStudent.setStatus(ClassStatusEnum.APPROVED);
            this.classStudentRepository.save(classStudent);
        }
    }

    // role lecturer
    public void rejected(ReqUpdateStudentClassDTO req) {
        ClassStudent classStudent = this.classStudentRepository.findByUserIdAndClassRoomId(req.getUserId(),
                req.getClassId());
        if (classStudent != null) {
            classStudent.setStatus(ClassStatusEnum.REJECTED);
            this.classStudentRepository.save(classStudent);
        }
    }

    public void joinClass(String classCode) {
        ClassRoom classRoom = this.classRoomRepository.findByClassCode(classCode);
        if (classRoom == null)
            return;

        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null) {
            throw new RuntimeException("Bạn cần đăng nhập để thực hiện chức năng này!");
        }
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Tài khoản người dùng không tồn tại!");
        }

        // Check if already in class or already requested
        ClassStudent existing = this.classStudentRepository.findByUserIdAndClassRoomId(user.getId(), classRoom.getId());
        if (existing != null) {
            // Re-apply if previously rejected or removed
            if (existing.getStatus() == ClassStatusEnum.REJECTED || existing.getStatus() == ClassStatusEnum.REMOVED) {
                existing.setStatus(ClassStatusEnum.PENDING);
                existing.setJoinAt(Instant.now());
                this.classStudentRepository.save(existing);
            }
            return;
        }

        ClassStudent classStudent = new ClassStudent();
        classStudent.setUser(user);
        classStudent.setClassRoom(classRoom);
        classStudent.setJoinAt(Instant.now());
        classStudent.setStatus(ClassStatusEnum.PENDING);
        classStudent.setRoleInClass(RoleEnum.STUDENT);
        this.classStudentRepository.save(classStudent);
    }

    public void leaveClass(Long classId) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User user = this.userRepository.findByEmail(email);

        ClassStudent classStudent = this.classStudentRepository.findByUserIdAndClassRoomId(user.getId(), classId);
        classStudent.setStatus(ClassStatusEnum.REMOVED);
        this.classStudentRepository.save(classStudent);
    }

    public ClassRoom findByClassCode(String classCode) {
        return this.classRoomRepository.findByClassCode(classCode);
    }

    public ClassStudent findByUserAndClass(Long userId, Long classId) {
        return this.classStudentRepository.findByUserIdAndClassRoomId(userId, classId);
    }

    public ResClassStudentDTO getUserFromClass(long classId) {
        List<ClassStudent> list = this.classStudentRepository.findByClassRoomId(classId);
        ClassRoom classRoom = this.classRoomRepository.findById(classId).orElse(null);
        ResClassStudentDTO res = new ResClassStudentDTO();
        if (classRoom == null)
            return res;

        ResClassStudentDTO.classRoomInner cRoomInner = new ResClassStudentDTO.classRoomInner(classRoom.getId(),
                classRoom.getName(), classRoom.getDescription(), classRoom.getClassCode());
        res.setClassRoom(cRoomInner);
        List<ResClassStudentDTO.userInner> listUser = new ArrayList<>();
        for (ClassStudent cl : list) {
            ResClassStudentDTO.userInner userInner = new ResClassStudentDTO.userInner();
            userInner.setId(cl.getUser().getId());
            userInner.setEmail(cl.getUser().getEmail());
            userInner.setName(cl.getUser().getName());
            userInner.setStatus(cl.getStatus().toString());
            listUser.add(userInner);
        }
        res.setUser(listUser);
        return res;
    }
}

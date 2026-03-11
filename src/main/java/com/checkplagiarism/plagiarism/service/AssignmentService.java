package com.checkplagiarism.plagiarism.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Assignment;
import com.checkplagiarism.plagiarism.domain.ClassRoom;
import com.checkplagiarism.plagiarism.domain.request.assignment.ReqCreateAssignmentDTO;
import com.checkplagiarism.plagiarism.domain.request.assignment.ReqUpdateAssignmentDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.repository.AssignmentRepository;
import com.checkplagiarism.plagiarism.repository.ClassRoomRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final ClassRoomRepository classRoomRepository;

    public Assignment createAssignment(ReqCreateAssignmentDTO req){
        Assignment assignment= new Assignment();
        assignment.setTitle(req.getTitle());
        assignment.setDescription(req.getDescription());
        assignment.setDueDate(req.getDueDate());

        ClassRoom classRoom= this.classRoomRepository.findById(req.getClassId()).orElse(null);
        if (classRoom!=null) {
            assignment.setClassRoom(classRoom);
        }
        return this.assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(ReqUpdateAssignmentDTO req){
        Assignment assignment= this.assignmentRepository.findById(req.getId()).orElse(null);
        if (assignment!=null) {
            assignment.setTitle(req.getTitle());
            assignment.setDescription(req.getDescription());
            assignment.setDueDate(req.getDueDate());
            assignment= this.assignmentRepository.save(assignment);
        }
        return assignment;
    }

    public Assignment getAssignmentById(Long id){
        return this.assignmentRepository.findById(id).orElse(null);
    }

        public ResultPaginationDTO handleGetAll(Specification<Assignment> spec, Pageable page){
        Page<Assignment> assignment= this.assignmentRepository.findAll(spec, page);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta=new ResultPaginationDTO.Meta();
        meta.setPage(assignment.getNumber() + 1);
        meta.setPageSize(assignment.getSize());
        meta.setPages(assignment.getTotalPages());
        meta.setTotal(assignment.getTotalElements());

        rs.setMeta(meta);
        rs.setResults(assignment.getContent());
        return rs;
    }

    public void deleteAssignment(Long id){
        this.assignmentRepository.deleteById(id);
    }
}

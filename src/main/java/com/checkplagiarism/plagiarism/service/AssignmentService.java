package com.checkplagiarism.plagiarism.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Assignment;
import com.checkplagiarism.plagiarism.domain.ClassRoom;
import com.checkplagiarism.plagiarism.domain.request.assignment.ReqCreateAssignmentDTO;
import com.checkplagiarism.plagiarism.domain.request.assignment.ReqUpdateAssignmentDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.domain.response.assignment.ResAssignmentDTO;
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

        List<ResAssignmentDTO> res= assignment.getContent().stream().map(item->this.convResAssignmentDTO(item)).collect(Collectors.toList());

        rs.setMeta(meta);
        rs.setResults(res);
        return rs;
    }

    public void deleteAssignment(Long id){
        this.assignmentRepository.deleteById(id);
    }

    public List<ResAssignmentDTO> getAssignmentByClassId(Long classId){
        List<Assignment> assignments=this.assignmentRepository.findByClassRoomId(classId);
        List<ResAssignmentDTO> list= new ArrayList<>();
        if (!assignments.isEmpty()) {
            
            for(Assignment as:assignments)
            {
                ResAssignmentDTO res=this.convResAssignmentDTO(as);
                list.add(res);
            }
        }
        return list;
    }

    public ResAssignmentDTO convResAssignmentDTO(Assignment assignment){
        ResAssignmentDTO res= new ResAssignmentDTO();
        res.setId(assignment.getId());
        res.setTitle(assignment.getTitle());
        res.setDescription(assignment.getDescription());
        res.setDueDate(assignment.getDueDate());
        res.setCreatedAt(assignment.getCreatedAt());
        ResAssignmentDTO.ClassRoomInner inner= new ResAssignmentDTO.ClassRoomInner();
        inner.setId(assignment.getClassRoom()!=null ? assignment.getClassRoom().getId() : null);
        inner.setClassName(assignment.getClassRoom()!=null ? assignment.getClassRoom().getName() : null);
        res.setClassRoom(inner);
        return res;
    }
    
}

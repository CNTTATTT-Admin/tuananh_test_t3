package com.checkplagiarism.plagiarism.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkplagiarism.plagiarism.domain.Assignment;
import com.checkplagiarism.plagiarism.domain.request.assignment.ReqCreateAssignmentDTO;
import com.checkplagiarism.plagiarism.domain.request.assignment.ReqUpdateAssignmentDTO;
import com.checkplagiarism.plagiarism.domain.request.user.ReqCreateUserDTO;
import com.checkplagiarism.plagiarism.domain.request.user.ReqUpdateUserDTO;
import com.checkplagiarism.plagiarism.domain.response.ResultPaginationDTO;
import com.checkplagiarism.plagiarism.domain.response.assignment.ResAssignmentDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResCreateUserDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResFetchUserDTO;
import com.checkplagiarism.plagiarism.domain.response.user.ResUpdateUserDTO;
import com.checkplagiarism.plagiarism.service.AssignmentService;
import com.checkplagiarism.plagiarism.util.err.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;
    
      @PostMapping("/assignments")
    public ResponseEntity<ResAssignmentDTO> createAssignment(@RequestBody ReqCreateAssignmentDTO req) throws IdInvalidException {
        Assignment as= this.assignmentService.createAssignment(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.assignmentService.convResAssignmentDTO(as));
    }

    @PutMapping("/assignments")
    public ResponseEntity<ResAssignmentDTO> updateAssinment( @RequestBody ReqUpdateAssignmentDTO req) throws IdInvalidException {
        Assignment assignment= this.assignmentService.getAssignmentById(req.getId());
        if (assignment==null) {
            throw new IdInvalidException("assignment not found");
        }
        assignment= this.assignmentService.updateAssignment(req);
        
        return ResponseEntity.ok().body(this.assignmentService.convResAssignmentDTO(assignment));
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable ("id") Long id) throws IdInvalidException {
        Assignment assignment = this.assignmentService.getAssignmentById(id);
        if (assignment == null) {
            throw new IdInvalidException("assignment not found");
        }
        this.assignmentService.deleteAssignment(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/assignments/{id}")
    public ResponseEntity<ResAssignmentDTO> getUserById(@PathVariable ("id") Long id) throws IdInvalidException {
        Assignment assignment = this.assignmentService.getAssignmentById(id);
        if (assignment == null) {
            throw new IdInvalidException("assignment not found");
        }
        return ResponseEntity.ok().body(this.assignmentService.convResAssignmentDTO(assignment));
    }


    @GetMapping("/assignments")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<Assignment> spec,Pageable page) {
        return ResponseEntity.ok().body(this.assignmentService.handleGetAll(spec, page));
    }

    @GetMapping("/assignments/class/{classRoomId}")
    public ResponseEntity<List<ResAssignmentDTO>> getUserByClass(@PathVariable Long classRoomId) throws IdInvalidException {
        return ResponseEntity.ok().body(this.assignmentService.getAssignmentByClassId(classRoomId));
    }
}

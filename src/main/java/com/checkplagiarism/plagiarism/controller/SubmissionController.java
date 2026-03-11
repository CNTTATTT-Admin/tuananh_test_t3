package com.checkplagiarism.plagiarism.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.domain.response.SubmissionResponse;
import com.checkplagiarism.plagiarism.service.SubmissionService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

     @PostMapping(value = "/submissions",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionResponse> submitAssignment(
            @RequestParam Long assignmentId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile file
    ) throws Exception {

        Submission submission = submissionService.submitAssignment(
                assignmentId,
                file);

        SubmissionResponse response = submissionService.convertToResponse(submission);

        return ResponseEntity.ok().body(response);
    }

        @GetMapping("/my-history")
    public ResponseEntity<List<SubmissionResponse>> getMyHistory(
            @RequestParam Long studentId){

        return ResponseEntity.ok(
                submissionService.getMySubmissions(studentId)
        );
    }

        @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId){

        return ResponseEntity.ok(
                submissionService.getSubmissionsByAssignment(assignmentId)
        );
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<SubmissionResponse>> getAll() {

        return ResponseEntity.ok(
                submissionService.getAllSubmissions());
    }


}

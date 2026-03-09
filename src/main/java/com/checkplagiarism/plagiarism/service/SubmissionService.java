package com.checkplagiarism.plagiarism.service;

import java.io.IOException;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.checkplagiarism.plagiarism.domain.Assignment;
import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.response.ResSubmissionDTO;
import com.checkplagiarism.plagiarism.repository.AssignmentRepository;
import com.checkplagiarism.plagiarism.repository.SubmissionRepository;
import com.checkplagiarism.plagiarism.repository.UserRepository;
import com.checkplagiarism.plagiarism.util.SecurityUtil;
import com.checkplagiarism.plagiarism.util.err.IdInvalidException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final SecurityUtil securityUtil;

    public Submission submitAssignment(Long assignmentId,String content, MultipartFile file) throws IdInvalidException, IOException{
        Assignment assignment= this.assignmentRepository.findById(assignmentId).orElseThrow(()-> new IdInvalidException("assignment not found"));
        String email=this.securityUtil.getCurrentUserLogin().isPresent()?this.securityUtil.getCurrentUserLogin().get():null;
        User user = this.userRepository.findByEmail(email);

        String extractText="";

        if (content!=null && !content.isEmpty()) {
            extractText=content;
        }

        if ( file!=null && !file.isEmpty()) {
            extractText=this.fileService.extractText(file);
        }

        Submission submission= new Submission();
        submission.setContent(extractText);
        submission.setSubmittedAt(Instant.now());
        submission.setStudent(user);
        submission.setAssignment(assignment);

        if (file!=null && !file.isEmpty()) {
            String fileUrl= fileService.saveFile(file);
            submission.setFileUrl(fileUrl);
        }
        return this.submissionRepository.save(submission);
    }

    public ResSubmissionDTO convertToResSubmissionDTO(Submission submission){
        ResSubmissionDTO res= new ResSubmissionDTO();
        res.setId(submission.getId());
        res.setContent(submission.getContent());
        res.setFileUrl(submission.getFileUrl());
        res.setSubmittedAt(submission.getSubmittedAt());
        res.setAssignmentId(submission.getAssignment().getId());
        res.setUserId(submission.getStudent().getId());

        return res;
    }
}

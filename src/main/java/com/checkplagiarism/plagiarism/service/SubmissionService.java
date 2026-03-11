package com.checkplagiarism.plagiarism.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.checkplagiarism.plagiarism.domain.Assignment;
import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.PlagiarismThresholds;
import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.response.ResSubmissionDTO;
import com.checkplagiarism.plagiarism.domain.response.SubmissionResponse;
import com.checkplagiarism.plagiarism.repository.AssignmentRepository;
import com.checkplagiarism.plagiarism.repository.DocumentRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismThresholdRepository;
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
    private final PlagiarismService plagiarismService;
    private final DocumentService documentService;
    private final PlagiarismCheckService checkService;

    public Submission submitAssignment(Long assignmentId, MultipartFile file) throws IdInvalidException, IOException{
        Assignment assignment= this.assignmentRepository.findById(assignmentId).orElseThrow(()-> new IdInvalidException("assignment not found"));
        String email=this.securityUtil.getCurrentUserLogin().isPresent()?this.securityUtil.getCurrentUserLogin().get():null;
        User user = this.userRepository.findByEmail(email);
        String text = this.fileService.extractText(file);

        Submission submission = new Submission();
        if (!file.isEmpty() && file!=null) {
            submission.setFileUrl(file.getOriginalFilename());
        }

    submission.setAssignment(assignment);
    submission.setStudent(user);
    submission.setContent(text);
    submission.setSubmittedAt(LocalDateTime.now());

    submissionRepository.save(submission);

    PlagiarismCheck check =
            checkService.createCheck(submission);

    double percent =
            plagiarismService.checkPlagiarism(text,check);

    checkService.finishCheck(check,percent);

    documentService.saveDocument(text,"file_url");

    submission.setPlagiarismPercent(percent);

    submissionRepository.save(submission);

    return submission;
    }

    public SubmissionResponse convertToResponse(Submission submission) {

        return SubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment().getId())
                .assignmentTitle(submission.getAssignment().getTitle())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getName())
                .plagiarismPercent(submission.getPlagiarismPercent())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }

    public List<SubmissionResponse> getMySubmissions(Long studentId) {

        List<Submission> submissions = submissionRepository.findByStudentId(studentId);

        return submissions.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {

        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);

        return submissions.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<SubmissionResponse> getAllSubmissions() {

        return submissionRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


}

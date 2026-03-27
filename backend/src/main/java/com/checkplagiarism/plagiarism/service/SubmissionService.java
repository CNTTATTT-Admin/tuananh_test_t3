package com.checkplagiarism.plagiarism.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.checkplagiarism.plagiarism.domain.Assignment;
import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.response.SubmissionResponse;
import com.checkplagiarism.plagiarism.repository.AssignmentRepository;
import com.checkplagiarism.plagiarism.repository.SubmissionRepository;
import com.checkplagiarism.plagiarism.repository.UserRepository;
import com.checkplagiarism.plagiarism.util.SecurityUtil;
import com.checkplagiarism.plagiarism.repository.PlagiarismCheckRepository;
import com.checkplagiarism.plagiarism.util.err.IdInvalidException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final PlagiarismService plagiarismService;
    private final DocumentService documentService;
    private final PlagiarismCheckService checkService;
    private final PlagiarismCheckRepository plagiarismCheckRepository;

    public Submission submitAssignment(Long assignmentId, String content, MultipartFile file)
            throws IdInvalidException, IOException {
        Assignment assignment = null;
        if (assignmentId != null) {
            assignment = this.assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new IdInvalidException("assignment not found"));
        }
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        User user = (email != null) ? this.userRepository.findByEmail(email) : null;

        String text = "";
        if (file != null && !file.isEmpty()) {
            text = this.fileService.extractText(file);
        } else if (content != null) {
            text = content;
        }

        if (text == null || text.trim().isEmpty()) {
            throw new IdInvalidException("No content found to analyze. Please provide text or upload a file.");
        }

        Submission submission = new Submission();
        if (file != null && !file.isEmpty()) {
            submission.setFileUrl(file.getOriginalFilename());
        }

        submission.setAssignment(assignment);
        submission.setStudent(user);
        submission.setContent(text);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setPlagiarismPercent(0.0); // Default to 0 until check finishes

        submissionRepository.save(submission);

        // 1. Create the check record
        PlagiarismCheck check = checkService.createCheck(submission);

        // 2. Start ASYNC plagiarism check
        plagiarismService.checkPlagiarismAsync(text, check);

        // 3. Save the document to the database and index its fingerprints
        String uploaderName = (user != null) ? user.getName() : "Guest";
        String baseTitle = (assignment != null) ? assignment.getTitle()
                : (file != null && !file.getOriginalFilename().isEmpty() ? file.getOriginalFilename()
                        : "Text Submission");
        String docTitle = baseTitle + " - " + uploaderName;
        documentService.saveDocument(text, submission.getFileUrl(), docTitle, user);

        return submission;
    }

    public SubmissionResponse convertToResponse(Submission submission) {

        return SubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment() != null ? submission.getAssignment().getId() : null)
                .assignmentTitle(
                        submission.getAssignment() != null ? submission.getAssignment().getTitle() : "Quick Check")
                .studentId(submission.getStudent() != null ? submission.getStudent().getId() : null)
                .studentName(submission.getStudent() != null ? submission.getStudent().getName() : "Guest")
                .studentEmail(submission.getStudent() != null ? submission.getStudent().getEmail() : "anonymous@guest")
                .classId((submission.getAssignment() != null && submission.getAssignment().getClassRoom() != null)
                        ? submission.getAssignment().getClassRoom().getId()
                        : null)
                .content(submission.getContent())
                .plagiarismPercent(submission.getPlagiarismPercent())
                .status(plagiarismCheckRepository.findBySubmissionId(submission.getId()) != null
                        ? plagiarismCheckRepository.findBySubmissionId(submission.getId()).getResultStatus()
                        : "NOT_STARTED")
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
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null) return new ArrayList<>();

        User user = userRepository.findByEmail(email);
        if (user == null) return new ArrayList<>();

        List<Submission> submissions;
        if (user.getRole() != null && "LECTURER".equalsIgnoreCase(user.getRole().getName())) {
            submissions = submissionRepository.findByAssignment_ClassRoom_LecturerId(user.getId());
        } else if (user.getRole() != null && "STUDENT".equalsIgnoreCase(user.getRole().getName())) {
            submissions = submissionRepository.findByStudentId(user.getId());
        } else if (user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            submissions = submissionRepository.findAll();
        } else {
            submissions = new ArrayList<>();
        }

        return submissions.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public SubmissionResponse getSubmissionById(Long id) {
        return this.submissionRepository.findById(id)
                .map(this::convertToResponse)
                .orElse(null);
    }

}

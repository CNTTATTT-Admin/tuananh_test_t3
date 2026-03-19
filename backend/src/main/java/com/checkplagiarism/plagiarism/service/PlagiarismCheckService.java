package com.checkplagiarism.plagiarism.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.repository.PlagiarismCheckRepository;
import com.checkplagiarism.plagiarism.repository.SubmissionRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PlagiarismCheckService {
    private final PlagiarismCheckRepository plagiarismCheckRepository;
    private final SubmissionRepository submissionRepository;

    public PlagiarismCheck createCheck(Submission submission){
        PlagiarismCheck check = new PlagiarismCheck();
        check.setSubmission(submission);
        check.setUser(submission.getStudent()); // Map student from submission
        check.setResultStatus("PROCESSING");
        check.setCreatedAt(LocalDateTime.now());
        return plagiarismCheckRepository.save(check);
    }

    public void finishCheck(PlagiarismCheck check, double percent){
        log.info("Finishing plagiarism check for submission ID {}. Result: {}%", 
                 check.getSubmission() != null ? check.getSubmission().getId() : "null", 
                 String.format("%.2f", percent));

        check.setPlagiarismPercent(percent);
        check.setResultStatus("COMPLETED");
        
        // Calculate processing time
        if (check.getCreatedAt() != null) {
            long duration = java.time.Duration.between(check.getCreatedAt(), LocalDateTime.now()).toMillis();
            check.setProcessingTimeMs(duration);
        }

        plagiarismCheckRepository.save(check);

        // Update the Submission as well
        if (check.getSubmission() != null) {
            Submission submission = check.getSubmission();
            submission.setPlagiarismPercent(percent);
            submissionRepository.save(submission);
        }
    }

    public void failCheck(PlagiarismCheck check, String reason) {
        log.error("Plagiarism check failed for submission {}. Reason: {}", 
                 check.getSubmission() != null ? check.getSubmission().getId() : "null", 
                 reason);
        check.setResultStatus("FAILED");
        plagiarismCheckRepository.save(check);
    }
}

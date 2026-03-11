package com.checkplagiarism.plagiarism.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.repository.PlagiarismCheckRepository;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class PlagiarismCheckService {
    private final PlagiarismCheckRepository plagiarismCheckRepository;

    public PlagiarismCheck createCheck(Submission submission){

        PlagiarismCheck check = new PlagiarismCheck();

        check.setSubmission(submission);
        check.setResultStatus("PROCESSING");
        check.setCreatedAt(LocalDateTime.now());

        return plagiarismCheckRepository.save(check);
    }

    public void finishCheck(PlagiarismCheck check, double percent){

        check.setPlagiarismPercent(percent);
        check.setResultStatus("COMPLETED");

        plagiarismCheckRepository.save(check);
    }
}

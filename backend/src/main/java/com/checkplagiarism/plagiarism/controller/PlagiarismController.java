package com.checkplagiarism.plagiarism.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.PlagiarismMatch;
import com.checkplagiarism.plagiarism.repository.PlagiarismCheckRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismMatchRepository;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PlagiarismController {
    private final PlagiarismCheckRepository checkRepository;
    private final PlagiarismMatchRepository matchRepository;

    @GetMapping("/checks/{submissionId}")
    public List<PlagiarismMatch> getMatches(@PathVariable(name = "submissionId") Long submissionId) {

        PlagiarismCheck check = checkRepository
                .findBySubmissionId(submissionId);

        if (check == null) {
            return List.of();
        }

        return this.matchRepository.findByCheckId(check.getId());
    }
    
}

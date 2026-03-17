package com.checkplagiarism.plagiarism.service;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.PlagiarismMatch;
import com.checkplagiarism.plagiarism.repository.PlagiarismMatchRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlagiarismMatchService {

    private final PlagiarismMatchRepository plagiarismMatchRepository;

    public void saveMatch(
            PlagiarismCheck check,
            Document document,
            double percent,
            String matchedText,
            int start, int end) {

        PlagiarismMatch match = new PlagiarismMatch();

        match.setCheck(check);
        match.setDocument(document);
        match.setSimilarityPercent(percent);
        match.setMatchedText(matchedText);
        match.setStartPosition(start);
        match.setEndPosition(end);

        plagiarismMatchRepository.save(match);
    }
}

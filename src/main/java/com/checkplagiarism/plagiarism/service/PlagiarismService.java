package com.checkplagiarism.plagiarism.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.DocumentFingerprint;
import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.PlagiarismMatch;
import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.repository.DocumentRepository;
import com.checkplagiarism.plagiarism.repository.FingerprintRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismCheckRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismMatchRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlagiarismService {
  private final FingerprintService fingerprintService;
    private final FingerprintRepository fingerprintRepository;
    private final PlagiarismMatchService matchService;

    public double checkPlagiarism(
            String text,
            PlagiarismCheck check) {

        Set<Long> hashes = fingerprintService.generateFingerprints(text);

        List<DocumentFingerprint> matches = fingerprintRepository.findByHashValueIn(hashes);

        Map<Long, Integer> counter = new HashMap<>();

        Map<Long, Document> documentMap = new HashMap<>();

        for (DocumentFingerprint f : matches) {

            Long docId = f.getDocument().getId();

            documentMap.put(docId, f.getDocument());

            counter.put(docId,
                    counter.getOrDefault(docId, 0) + 1);
        }

        int total = hashes.size();

        double maxSimilarity = 0;

        for (Long docId : counter.keySet()) {

            int matchCount = counter.get(docId);

            double percent = (double) matchCount / total * 100;

            if (percent > maxSimilarity) {
                maxSimilarity = percent;
            }

            if (percent > 5) {

                this.matchService.saveMatch(
                        check,
                        documentMap.get(docId),
                        percent,
                        "matched text example");
            }
        }

        return maxSimilarity;
    }
}

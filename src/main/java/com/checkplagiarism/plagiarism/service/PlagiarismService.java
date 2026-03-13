package com.checkplagiarism.plagiarism.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.DocumentFingerprint;
import com.checkplagiarism.plagiarism.domain.FingerPrint;
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

               // 1. Generate fingerprint của submission
        List<FingerPrint> fingerprints = fingerprintService.generateFingerprints(text);
        if (fingerprints.size() > 20000) {
            fingerprints = fingerprints.subList(0, 20000);
        }

        // 2. Map hash -> list position trong submission
        Map<Long, List<Integer>> submissionPositions = new HashMap<>();

        for (FingerPrint f : fingerprints) {
            submissionPositions
                    .computeIfAbsent(f.getHash(), k -> new ArrayList<>())
                    .add(f.getPosition());
        }

        // 3. Lấy tất cả hash để query DB
        List<Long> hashList = new ArrayList<>(submissionPositions.keySet());

        List<DocumentFingerprint> matches = new ArrayList<>();

        int batchSize = 1000;

        for (int i = 0; i < hashList.size(); i += batchSize) {

            List<Long> batch = hashList.subList(
                    i,
                    Math.min(i + batchSize, hashList.size()));

            matches.addAll(
                    fingerprintRepository.findByHashValueIn(batch));
        }

        // 4. Đếm số fingerprint trùng theo document
        Map<Long, Integer> counter = new HashMap<>();
        Map<Long, Document> documentMap = new HashMap<>();

        for (DocumentFingerprint f : matches) {

            Long docId = f.getDocument().getId();

            documentMap.put(docId, f.getDocument());

            counter.put(
                    docId,
                    counter.getOrDefault(docId, 0) + 1
            );
        }

        Map<Long, List<DocumentFingerprint>> docFingerprintMap = new HashMap<>();

        for (DocumentFingerprint f : matches) {

            Long docId = f.getDocument().getId();

            docFingerprintMap
                    .computeIfAbsent(docId, k -> new ArrayList<>())
                    .add(f);
        }

        // 5. Tổng fingerprint của submission
        int total = fingerprints.size();

        double maxSimilarity = 0;

        String[] words = text.split("\\s+");

        // 6. Tính similarity
        for (Long docId : counter.keySet()) {

            int matchCount = counter.get(docId);

            double percent = (double) matchCount / total * 100;

            if (percent > maxSimilarity) {
                maxSimilarity = percent;
            }
            List<DocumentFingerprint> docMatches = docFingerprintMap.get(docId);

            if (docMatches == null)
                continue;

            // 7. Nếu vượt threshold thì lưu match
            if (percent > 10) {

                for (DocumentFingerprint f : docMatches) {

                    if (!f.getDocument().getId().equals(docId)) continue;

                    Long hash = f.getHashValue();

                    List<Integer> positions = submissionPositions.get(hash);

                    if (positions == null) continue;

                    for (Integer pos : positions) {

                        if (pos >= words.length)
                            continue;

                        int start = pos;
                        int end = Math.min(pos + 5, words.length);

                        if (start >= end)
                            continue;

                        String matchedText = String.join(
                                " ",
                                Arrays.copyOfRange(words, start, end));

                        matchService.saveMatch(
                                check,
                                documentMap.get(docId),
                                percent,
                                matchedText,
                                start,
                                end
                        );
                    }
                }
            }
        }

        return maxSimilarity;
    }
}

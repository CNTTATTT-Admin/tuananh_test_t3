package com.checkplagiarism.plagiarism.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.DocumentFingerprint;
import com.checkplagiarism.plagiarism.domain.FingerPrint;
import com.checkplagiarism.plagiarism.domain.FingerprintIndex;
import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.PlagiarismMatch;
import com.checkplagiarism.plagiarism.domain.Submission;
import com.checkplagiarism.plagiarism.repository.DocumentRepository;
import com.checkplagiarism.plagiarism.repository.FingerprintRepository;
import com.checkplagiarism.plagiarism.repository.FingerprintSearchRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismCheckRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismMatchRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlagiarismService {
  private final FingerprintService fingerprintService;
    private final FingerprintRepository fingerprintRepository;
    private final PlagiarismMatchService matchService;
    private final FingerprintSearchRepository fingerprintSearchRepository;

   public double checkPlagiarism(String text, Long currentDocId){
   // 1. Generate fingerprint
    List<FingerPrint> fingerprints = fingerprintService.generateFingerprints(text);

    if (fingerprints.size() > 20000) {
        fingerprints = fingerprints.subList(0, 20000);
    }

    if (fingerprints.isEmpty()) return 0;

    // 2. Map hash -> positions (giữ để highlight sau này)
    Map<Long, List<Integer>> hashPositions = new HashMap<>();
    for (FingerPrint fp : fingerprints) {
        hashPositions
            .computeIfAbsent(fp.getHash(), k -> new ArrayList<>())
            .add(fp.getPosition());
    }

    // 3. Unique hash (TRÁNH double count)
        Set<Long> uniqueHashes = new HashSet<>(hashPositions.keySet());

        List<Long> hashList = new ArrayList<>(uniqueHashes);

        // ✅ LIMIT xuống
        if (hashList.size() > 3000) {
            hashList = hashList.subList(0, 3000);
}

    // 4. SEARCH ELASTIC (batch)
        List<FingerprintIndex> results = new ArrayList<>();

        int batchSize = 1000;

        for (int i = 0; i < hashList.size(); i += batchSize) {
            List<Long> batch = hashList.subList(i, Math.min(i + batchSize, hashList.size()));
            results.addAll(fingerprintSearchRepository.findByHashIn(batch));
        }

    if (results.isEmpty()) return 0;

    // 5. Group theo document
    Map<Long, Set<Long>> docToMatchedHashes = new HashMap<>();

        for (FingerprintIndex fi : results) {


            docToMatchedHashes
                .computeIfAbsent(fi.getDocumentId(), k -> new HashSet<>())
                .add(fi.getHash());
        }

    // 6. Tính plagiarism % (lấy doc match nhiều nhất)
    double maxPercent = 0;

    for (Map.Entry<Long, Set<Long>> entry : docToMatchedHashes.entrySet()) {
        int matchedCount = entry.getValue().size();

        double percent = (double) matchedCount / (hashList.size() * 1.5) * 100;

        if (percent > maxPercent) {
            maxPercent = percent;
        }
    }
    System.out.println("HASH INPUT: " + hashList.size());
System.out.println("ES RESULTS: " + results.size());

    return maxPercent;
    }
}
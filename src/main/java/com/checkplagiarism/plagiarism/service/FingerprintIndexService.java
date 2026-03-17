package com.checkplagiarism.plagiarism.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.FingerPrint;
import com.checkplagiarism.plagiarism.domain.FingerprintIndex;
import com.checkplagiarism.plagiarism.repository.FingerprintSearchRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FingerprintIndexService {

    private final FingerprintService fingerprintService;
    private final FingerprintSearchRepository repository;

    public void indexDocument(Long documentId, String text) {

        // 1. Generate fingerprint
        List<FingerPrint> fingerprints =
                fingerprintService.generateFingerprints(text);

        System.out.println("INDEX DOC: " + documentId);
        System.out.println("FP SIZE: " + fingerprints.size());

        if (fingerprints.isEmpty()) {
            System.out.println(" NO FINGERPRINT GENERATED");
            return;
        }

        // 2. Convert -> Elasticsearch model
        List<FingerprintIndex> indexList = fingerprints.stream()
                .map(fp -> FingerprintIndex.builder()
                        .id(UUID.randomUUID().toString())
                        .hash(fp.getHash())          // ⚠️ phải là Long
                        .documentId(documentId)
                        .position(fp.getPosition())
                        .build()
                )
                .toList();

        // 3. Save to ES
        repository.saveAll(indexList);

        System.out.println("✅ INDEX DONE: " + indexList.size());
    }
}
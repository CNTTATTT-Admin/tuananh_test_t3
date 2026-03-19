package com.checkplagiarism.plagiarism.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.PlagiarismCheck;
import com.checkplagiarism.plagiarism.domain.elasticsearch.DocumentFingerprintES;
import com.checkplagiarism.plagiarism.repository.DocumentRepository;
import com.checkplagiarism.plagiarism.repository.elasticsearch.FingerprintESRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PlagiarismService {
    private final FingerprintService fingerprintService;
    private final FingerprintESRepository fingerprintESRepository;
    private final PlagiarismMatchService matchService;
    private final DocumentRepository documentRepository;
    private final PlagiarismCheckService checkService;

    @Async("processExecutor")
    public CompletableFuture<Double> checkPlagiarismAsync(
            String text,
            PlagiarismCheck check) {

        log.info("Starting plagiarism check for submission ID: {}", check.getSubmission().getId());

        try {
            double percent = checkPlagiarism(text, check);
            checkService.finishCheck(check, percent);
            log.info("Finished plagiarism check for submission ID: {}. Result: {}%", check.getSubmission().getId(),
                    percent);
            return CompletableFuture.completedFuture(percent);
        } catch (Throwable e) {
            log.error("Error during plagiarism check", e);
            checkService.failCheck(check, e.getMessage());
            return CompletableFuture.completedFuture(0.0);
        }
    }

    public double checkPlagiarism(
            String text,
            PlagiarismCheck check) {

        Map<Long, List<Integer>> hashToPositions = fingerprintService.generateFingerprintsWithPositions(text);
        Set<Long> hashes = hashToPositions.keySet();
        if (hashes.isEmpty())
            return 0.0;

        log.info("Checking plagiarism for {} unique hashes", hashes.size());

        // Query Elasticsearch for matching hashes. For texts under 2000 fingerprints,
        // we check everything.
        List<DocumentFingerprintES> matches;
        if (hashes.size() > 2000) {
            // Take a subset only if the text is exceptionally large
            List<Long> subset = hashes.stream().limit(2000).toList();
            matches = fingerprintESRepository.findByHashValueIn(subset);
        } else {
            matches = fingerprintESRepository.findByHashValueIn(new ArrayList<>(hashes));
        }

        log.info("Total matches found in Elasticsearch: {}", matches.size());

        if (matches.isEmpty())
            return 0.0;

        // To allow matches from the same student during testing, we'll keep ownDocIds
        // empty
        java.util.Set<Long> ownDocIds = new java.util.HashSet<>();
        /*
         * Long currentStudentId = (check.getSubmission().getStudent() != null)
         * ? check.getSubmission().getStudent().getId()
         * : null;
         * if (currentStudentId != null) {
         * java.util.Set<Long> docIds =
         * matches.stream().map(DocumentFingerprintES::getDocumentId)
         * .collect(java.util.stream.Collectors.toSet());
         * if (!docIds.isEmpty()) {
         * List<com.checkplagiarism.plagiarism.domain.Document> documents =
         * documentRepository.findAllById(docIds);
         * ownDocIds = documents.stream()
         * .filter(d -> d.getUploadedBy() != null &&
         * d.getUploadedBy().getId().equals(currentStudentId))
         * .map(com.checkplagiarism.plagiarism.domain.Document::getId)
         * .collect(java.util.stream.Collectors.toSet());
         * }
         * }
         */

        // Group matches by document ID, mapping to target document's title and input
        // positions
        Map<Long, String> titleMap = new HashMap<>();
        Map<Long, Set<Integer>> docToMatchedInputPos = new HashMap<>();

        for (DocumentFingerprintES f : matches) {
            Long docId = f.getDocumentId();
            if (ownDocIds.contains(docId))
                continue;

            titleMap.put(docId, f.getDocumentTitle());

            // For this hash, find all positions it appeared in the INPUT text
            List<Integer> inputPositions = hashToPositions.get(f.getHashValue());
            if (inputPositions != null) {
                docToMatchedInputPos.computeIfAbsent(docId, k -> new HashSet<>()).addAll(inputPositions);
            }
        }

        int totalHashesInInput = hashToPositions.values().stream().mapToInt(List::size).sum();
        double maxSimilarity = 0;

        for (Long docId : docToMatchedInputPos.keySet()) {
            Set<Integer> matchedPosSet = docToMatchedInputPos.get(docId);
            int matchCount = matchedPosSet.size();
            double percent = (double) matchCount / totalHashesInInput * 100;

            if (percent > maxSimilarity) {
                maxSimilarity = percent;
            }

            // Save matches as regions for UI highlighting
            if (percent > 1.0) { // Threshold for showing in UI
                Document doc = documentRepository.findById(docId).orElse(null);
                if (doc != null) {
                    // Group contiguous positions into ranges
                    List<Integer> sortedPos = new ArrayList<>(matchedPosSet);
                    java.util.Collections.sort(sortedPos);

                    if (!sortedPos.isEmpty()) {
                        int start = sortedPos.get(0);
                        int end = start;

                        for (int i = 1; i < sortedPos.size(); i++) {
                            int current = sortedPos.get(i);
                            if (current == end + 1) {
                                end = current;
                            } else {
                                saveRangeMatch(check, doc, percent, start, end, titleMap.get(docId), matchCount);
                                start = current;
                                end = current;
                            }
                        }
                        // Save last range
                        saveRangeMatch(check, doc, percent, start, end, titleMap.get(docId), matchCount);
                    }
                }
            }
        }

        return maxSimilarity;
    }

    private void saveRangeMatch(PlagiarismCheck check, Document doc, double totalPercent, int startWordIdx,
            int endWordIdx, String title, int totalMatchCount) {
        // N-grams use 3 words, so the end of the last n-gram is startWordIdx + 2?
        // Let's just store the word indices.
        // Frontend can use these to highlight.

        int blockLength = endWordIdx - startWordIdx + 1;
        String description = "Matched block of " + blockLength + " fingerprints (" + String.format("%.1f", totalPercent)
                + "%) with: " + title;

        // We set startPosition and endPosition to the word indices
        this.matchService.saveMatch(
                check,
                doc,
                totalPercent,
                description,
                startWordIdx,
                endWordIdx + 2 // approx word end since it's 3-grams
        );
    }
}

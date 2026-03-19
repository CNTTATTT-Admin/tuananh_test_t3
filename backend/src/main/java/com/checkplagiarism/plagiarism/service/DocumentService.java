package com.checkplagiarism.plagiarism.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.DocumentFingerprint;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.domain.elasticsearch.DocumentFingerprintES;
import com.checkplagiarism.plagiarism.repository.DocumentRepository;
import com.checkplagiarism.plagiarism.repository.FingerprintRepository;
import com.checkplagiarism.plagiarism.repository.elasticsearch.FingerprintESRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final FingerprintRepository fingerprintRepository;
    private final FingerprintService fingerprintService;
    private final FingerprintESRepository fingerprintESRepository;

    public Document saveDocument(String text, String fileUrl, String title, User uploader) {

        String hash = DigestUtils.sha256Hex(text);

        Optional<Document> existing = documentRepository.findByHash(hash);

        if (existing.isPresent()) {
            return existing.get();
        }

        Document doc = new Document();

        doc.setContentText(text);
        doc.setFileUrl(fileUrl);
        doc.setTitle(title);
        doc.setHash(hash);
        doc.setUploadedBy(uploader);
        doc.setCreatedAt(LocalDateTime.now());

        documentRepository.save(doc);

        saveFingerprints(doc, text);

        return doc;
    }

    private void saveFingerprints(Document document, String text) {

        java.util.Map<Long, java.util.List<Integer>> hashToPositions = fingerprintService
                .generateFingerprintsWithPositions(text);

        List<DocumentFingerprint> list = new ArrayList<>();
        List<DocumentFingerprintES> esList = new ArrayList<>();

        for (java.util.Map.Entry<Long, java.util.List<Integer>> entry : hashToPositions.entrySet()) {
            Long h = entry.getKey();
            for (Integer currentPos : entry.getValue()) {
                DocumentFingerprint fp = new DocumentFingerprint();
                fp.setHashValue(h);
                fp.setDocument(document);
                fp.setPosition(currentPos);
                list.add(fp);

                // Create ES document with unique position to avoid overwriting
                DocumentFingerprintES esFp = DocumentFingerprintES.builder()
                        .id(document.getId() + "_" + h + "_" + currentPos)
                        .hashValue(h)
                        .documentId(document.getId())
                        .documentTitle(document.getTitle())
                        .position(currentPos)
                        .build();
                esList.add(esFp);
            }
        }

        fingerprintRepository.saveAll(list);
        fingerprintESRepository.saveAll(esList);
    }
}

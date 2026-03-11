package com.checkplagiarism.plagiarism.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Document;
import com.checkplagiarism.plagiarism.domain.DocumentFingerprint;
import com.checkplagiarism.plagiarism.repository.DocumentRepository;
import com.checkplagiarism.plagiarism.repository.FingerprintRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DocumentService {
private final DocumentRepository documentRepository;
    private final FingerprintRepository fingerprintRepository;
    private final FingerprintService fingerprintService;

    public Document saveDocument(String text, String fileUrl) {

        String hash = DigestUtils.sha256Hex(text);

        Optional<Document> existing = documentRepository.findByHash(hash);

        if (existing.isPresent()) {
            return existing.get();
        }

        Document doc = new Document();

        doc.setContentText(text);
        doc.setFileUrl(fileUrl);
        doc.setHash(hash);
        doc.setCreatedAt(LocalDateTime.now());

        documentRepository.save(doc);

        saveFingerprints(doc, text);

        return doc;
    }

    private void saveFingerprints(Document document, String text) {

        Set<Long> hashes = fingerprintService.generateFingerprints(text);

        List<DocumentFingerprint> list = new ArrayList<>();

        for (Long h : hashes) {

            DocumentFingerprint fp = new DocumentFingerprint();

            fp.setHashValue(h);
            fp.setDocument(document);

            list.add(fp);
        }

        fingerprintRepository.saveAll(list);
    }
}

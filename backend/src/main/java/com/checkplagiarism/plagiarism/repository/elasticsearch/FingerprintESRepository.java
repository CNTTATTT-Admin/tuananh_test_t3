package com.checkplagiarism.plagiarism.repository.elasticsearch;

import java.util.Collection;
import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.checkplagiarism.plagiarism.domain.elasticsearch.DocumentFingerprintES;

@Repository
public interface FingerprintESRepository extends ElasticsearchRepository<DocumentFingerprintES, String> {
    
    // Find docs that contain ANY of these hashes
    List<DocumentFingerprintES> findByHashValueIn(Collection<Long> hashValues);
}

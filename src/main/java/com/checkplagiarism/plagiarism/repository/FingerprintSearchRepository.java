package com.checkplagiarism.plagiarism.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.checkplagiarism.plagiarism.domain.FingerprintIndex;

public interface FingerprintSearchRepository
        extends ElasticsearchRepository<FingerprintIndex, String> {

    List<FingerprintIndex> findByHash(Long hash);

    List<FingerprintIndex> findByHashIn(List<Long> hashes); 
}
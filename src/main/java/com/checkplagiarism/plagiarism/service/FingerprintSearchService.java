package com.checkplagiarism.plagiarism.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.FingerprintIndex;
import com.checkplagiarism.plagiarism.repository.FingerprintSearchRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FingerprintSearchService {

    private final FingerprintSearchRepository repository;

    public List<FingerprintIndex> findByHash(Long hash) {
        return repository.findByHash(hash);
    }
}
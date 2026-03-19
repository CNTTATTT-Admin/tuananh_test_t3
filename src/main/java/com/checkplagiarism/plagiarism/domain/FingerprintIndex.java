package com.checkplagiarism.plagiarism.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.persistence.Id;
import lombok.*;
@Data
@Document(indexName = "fingerprints")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FingerprintIndex {

    @Id
    private String id;

    private Long hash;

    private Long documentId;

    private Integer position;
}

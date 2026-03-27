package com.checkplagiarism.plagiarism.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "document_fingerprint")
@Entity
public class DocumentFingerprint {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hashValue;

    private Integer position;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
}

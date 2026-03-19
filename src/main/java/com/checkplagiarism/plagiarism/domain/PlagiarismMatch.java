package com.checkplagiarism.plagiarism.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "plagiarism_match")
@Entity
public class PlagiarismMatch {
      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String matchedText;

    private Double similarityPercent;

    private Integer startPosition;

    private Integer endPosition;

    @ManyToOne
    @JoinColumn(name = "check_id")
    private PlagiarismCheck check;

  @ManyToOne
  @JoinColumn(name = "document_id")
  private Document document;
}

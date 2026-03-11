package com.checkplagiarism.plagiarism.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "plagiarism_checks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlagiarismCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double plagiarismPercent;

    private String resultStatus;

    private LocalDateTime createdAt;

    private Long processingTimeMs;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @OneToMany(mappedBy = "check")
    private List<PlagiarismMatch> matches;
}
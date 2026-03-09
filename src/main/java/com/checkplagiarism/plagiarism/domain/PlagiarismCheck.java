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

    @Column(columnDefinition = "TEXT")
    private String inputText;

    private String fileUrl;

    private Double plagiarismPercent;

    private String resultStatus;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @OneToMany(mappedBy = "check")
    private List<PlagiarismMatch> matches;
}
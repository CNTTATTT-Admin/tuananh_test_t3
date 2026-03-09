package com.checkplagiarism.plagiarism.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}
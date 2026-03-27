package com.checkplagiarism.plagiarism.domain;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

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
@Table(name = "plagiarism_thresholds")
@Entity
public class PlagiarismThresholds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String levelName;
    private int min;
    private int max;
    private String color;
    private String description;
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassRoom classRoom;
}

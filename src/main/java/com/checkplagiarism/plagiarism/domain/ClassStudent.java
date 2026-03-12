package com.checkplagiarism.plagiarism.domain;

import java.time.Instant;
import java.time.LocalDateTime;

import com.checkplagiarism.plagiarism.util.constants.ClassStatusEnum;
import com.checkplagiarism.plagiarism.util.constants.RoleEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "class_students")
@Entity
public class ClassStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime joinAt;
    @Enumerated(EnumType.STRING)
    private RoleEnum roleInClass;
    @Enumerated(EnumType.STRING)
    private ClassStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassRoom classRoom;
}

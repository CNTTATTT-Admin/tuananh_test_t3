package com.checkplagiarism.plagiarism.domain.request.assignment;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateAssignmentDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
}
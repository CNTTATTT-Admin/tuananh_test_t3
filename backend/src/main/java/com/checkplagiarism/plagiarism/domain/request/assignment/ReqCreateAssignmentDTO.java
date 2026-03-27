package com.checkplagiarism.plagiarism.domain.request.assignment;
import java.time.Instant;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateAssignmentDTO {
    private String title;
    private String description;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;
    private Long classId;
}

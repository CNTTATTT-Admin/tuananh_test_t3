package com.checkplagiarism.plagiarism.domain.response;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResSubmissionDTO {
    private Long id;
    private String content;
    private String fileUrl;
    private Instant submittedAt;
    private Long userId;
    private Long assignmentId;
}

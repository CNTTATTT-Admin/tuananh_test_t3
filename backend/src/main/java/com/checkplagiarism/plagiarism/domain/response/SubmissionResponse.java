package com.checkplagiarism.plagiarism.domain.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;

    private Long assignmentId;

    private String assignmentTitle;

    private Long studentId;

    private String studentName;
    private String studentEmail;
    private Long classId;
    private String content;

    private double plagiarismPercent;

    private String status;
    private LocalDateTime submittedAt;
}

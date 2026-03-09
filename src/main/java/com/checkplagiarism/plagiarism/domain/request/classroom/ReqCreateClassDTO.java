package com.checkplagiarism.plagiarism.domain.request.classroom;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateClassDTO {
    private String name;
    private String description;
}

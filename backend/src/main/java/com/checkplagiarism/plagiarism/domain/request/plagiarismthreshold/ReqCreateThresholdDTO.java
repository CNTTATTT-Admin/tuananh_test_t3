package com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateThresholdDTO {
    private String levelName;
    private int min;
    private int max;
    private String color;
    private String description;
    private Long classId;
}

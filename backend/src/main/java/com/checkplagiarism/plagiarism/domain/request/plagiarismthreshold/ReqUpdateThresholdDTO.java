package com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateThresholdDTO {
    private Long id;
    private String levelName;
    private int min;
    private int max;
    private String color;
    private String description;
    private Long classId;
}

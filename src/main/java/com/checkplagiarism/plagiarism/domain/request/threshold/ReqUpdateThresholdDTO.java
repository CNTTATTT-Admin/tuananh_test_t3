package com.checkplagiarism.plagiarism.domain.request.threshold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateThresholdDTO {
    private Long id;
    private Integer min;
    private Integer max;

}

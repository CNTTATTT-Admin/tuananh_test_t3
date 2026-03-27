package com.checkplagiarism.plagiarism.domain.request.plagiarismthreshold;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSaveAllThresholdDTO {
    private Long classId;
    private List<ThresholdItem> thresholds;

    @Getter
    @Setter
    public static class ThresholdItem {
        private String levelName;
        private int min;
        private int max;
        private String color;
        private String description;
    }
}

package com.checkplagiarism.plagiarism.domain.request.classroom;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ReqUpdateStudentClassDTO {
    private Long classId;
    private Long userId;
}

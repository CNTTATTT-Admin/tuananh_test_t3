package com.checkplagiarism.plagiarism.domain.request.classroom;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ReqAddToClassDTO {
    private Long classId;
    private String email;
}

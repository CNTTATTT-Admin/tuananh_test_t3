package com.checkplagiarism.plagiarism.domain.request.classroom;
import jakarta.persistence.Column;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateClassDTO {
    private Long id;
    private String name;
    private String description;

}

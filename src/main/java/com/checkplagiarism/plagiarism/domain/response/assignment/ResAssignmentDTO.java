package com.checkplagiarism.plagiarism.domain.response.assignment;

import java.time.Instant;
import java.time.LocalDateTime;

import com.checkplagiarism.plagiarism.domain.Assignment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResAssignmentDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private ClassRoomInner classRoom;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassRoomInner {
    private Long Id;
    private String className;
        
    }
}

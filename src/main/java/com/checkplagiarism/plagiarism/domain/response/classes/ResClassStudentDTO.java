package com.checkplagiarism.plagiarism.domain.response.classes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResClassStudentDTO {
    private classRoomInner classRoom;
    private List<userInner> user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class classRoomInner {
    private Long id;
    private String name;
    private String description;
    private String code;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class userInner {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime joinAt;
        
    }
}

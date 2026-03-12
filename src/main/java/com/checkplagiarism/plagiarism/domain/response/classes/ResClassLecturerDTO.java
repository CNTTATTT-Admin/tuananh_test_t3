package com.checkplagiarism.plagiarism.domain.response.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResClassLecturerDTO {
    private classRoomInner classRoom;
    private userInner lecturer;

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
        
    }
}

package com.checkplagiarism.plagiarism.domain.response.classes;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResGetClassByUserDTO {
    private userInner student;
    private List<classRoomInner> classRoom;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class classRoomInner {
        private Long id;
        private String name;
        private String description;
        private String code;
        private String lecturer;
        private int studentCount;
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
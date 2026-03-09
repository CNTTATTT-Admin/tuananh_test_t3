package com.checkplagiarism.plagiarism.domain.response.user;

import java.time.Instant;

import com.checkplagiarism.plagiarism.util.constants.GenderEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResFetchUserDTO {
    private long id;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private int age;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}

package com.checkplagiarism.plagiarism.domain.request.user;

import com.checkplagiarism.plagiarism.util.constants.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateUserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private GenderEnum gender;
    private int age;
    private Long roleId;
}

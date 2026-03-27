package com.checkplagiarism.plagiarism.domain.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqLoginDTO {
    @NotBlank(message = "username is not blank")
    private String username;
    @NotBlank(message = "password is not blank")
    private String password;
}

package com.checkplagiarism.plagiarism.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}

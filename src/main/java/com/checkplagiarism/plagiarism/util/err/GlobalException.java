package com.checkplagiarism.plagiarism.util.err;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.checkplagiarism.plagiarism.domain.response.ResResponse;

@RestControllerAdvice
public class GlobalException {
        @ExceptionHandler(Exception.class)
    public ResponseEntity<ResResponse<Object>> handleAllException(Exception ex){
        ResResponse<Object> res=new ResResponse<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Exception");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(value ={
        UsernameNotFoundException.class,
        BadCredentialsException.class})
    public ResponseEntity<ResResponse<Object>> IdInvalidException(Exception ex) {
       ResResponse<Object> res=new ResResponse<Object>();
       res.setStatusCode(HttpStatus.BAD_REQUEST.value());
       res.setError("Exception");
       res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(res);
    }
}

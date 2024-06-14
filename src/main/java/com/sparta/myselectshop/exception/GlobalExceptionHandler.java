package com.sparta.myselectshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 클래스 레벨의 애노테이션
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RestApiException> handleException(IllegalArgumentException e) {
        RestApiException restApiException = new RestApiException(HttpStatus.BAD_REQUEST.value(), e.getMessage());

        return new ResponseEntity<>(
                restApiException, // HTTP body
                HttpStatus.BAD_REQUEST // HTTP status code
        );
    }

}
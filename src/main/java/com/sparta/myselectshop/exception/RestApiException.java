package com.sparta.myselectshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestApiException {

    private int statusCode;
    private String errorMessage;

}
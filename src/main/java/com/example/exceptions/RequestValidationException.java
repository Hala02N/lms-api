package com.example.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


public class RequestValidationException extends RuntimeException{
    private String message;

    public RequestValidationException(String message){
        super(message);
        this.message = message;
    }
}


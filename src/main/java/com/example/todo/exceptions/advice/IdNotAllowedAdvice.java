package com.example.todo.exceptions.advice;

import com.example.todo.exceptions.IdNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class IdNotAllowedAdvice {
    @ResponseBody
    @ExceptionHandler(IdNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> idNotAllowedHandler(IdNotAllowedException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", e.getMessage());
        return body;
    }
}

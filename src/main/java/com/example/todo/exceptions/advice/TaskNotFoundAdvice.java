package com.example.todo.exceptions.advice;

import com.example.todo.exceptions.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class TaskNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> taskNotFoundHandler(TaskNotFoundException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", e.getMessage());
        return body;
    }
}

package com.example.todo.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super(String.format("Task with id %s does not exist", id));
    }
}

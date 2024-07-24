package com.example.todo.exceptions;

public class PriorityRequiredException extends RuntimeException {
    public PriorityRequiredException() {
        super(MessageConstants.PRIORITY_REQUIRED_MESSAGE);
    }
}

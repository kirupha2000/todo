package com.example.todo.exceptions;

public class IdNotAllowedException extends RuntimeException {
    public IdNotAllowedException() {
        super("id is not allowed in this request");
    }
}

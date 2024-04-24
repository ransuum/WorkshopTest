package com.example.test.exception;

public class ValueNotCorrectException extends RuntimeException {
    public ValueNotCorrectException(String message) {
        super(message);
    }

    public ValueNotCorrectException(String message, Throwable cause) {
        super(message, cause);
    }
}

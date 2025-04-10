package com.example.backend.exception;

public class ClienteException extends Exception {

    public ClienteException(String message) {
        super(message);
    }

    public ClienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
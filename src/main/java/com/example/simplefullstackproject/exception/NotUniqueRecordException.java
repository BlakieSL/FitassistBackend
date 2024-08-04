package com.example.simplefullstackproject.exception;

public class NotUniqueRecordException extends RuntimeException {
    public NotUniqueRecordException(String message) {
        super(message);
    }
}
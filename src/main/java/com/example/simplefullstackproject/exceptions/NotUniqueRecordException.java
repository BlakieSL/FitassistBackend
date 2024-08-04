package com.example.simplefullstackproject.exceptions;

public class NotUniqueRecordException extends RuntimeException {
    public NotUniqueRecordException(String message) {
        super(message);
    }
}
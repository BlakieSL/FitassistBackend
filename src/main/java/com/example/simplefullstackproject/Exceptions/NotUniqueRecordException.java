package com.example.simplefullstackproject.Exceptions;

public class NotUniqueRecordException extends RuntimeException {
    public NotUniqueRecordException(String message) {
        super(message);
    }
}
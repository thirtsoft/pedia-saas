package com.wokite.net.shared.web.error;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
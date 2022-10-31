package com.andrey.exceptions;

public class BadRequestException extends ChatAppException{
    public BadRequestException(String message) {
        super(message);
    }
}

package com.andrey.exceptions;

public class BadEnumValueException extends ChatAppException{
    public BadEnumValueException(String message) {
        super(message);
    }
}

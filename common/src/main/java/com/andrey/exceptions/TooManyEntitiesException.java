package com.andrey.exceptions;

public class TooManyEntitiesException extends ChatAppException{
    public TooManyEntitiesException(String message) {
        super(message);
    }
}

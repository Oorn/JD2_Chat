package com.andrey.exceptions;

public class InteractionWithSelfException extends ChatAppException{
    public InteractionWithSelfException(String message) {
        super(message);
    }
}

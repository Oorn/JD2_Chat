package com.andrey.exceptions;

public class NoSuchEntityException extends ChatAppException{
    public NoSuchEntityException(String message) {
        super(message);
    }
}

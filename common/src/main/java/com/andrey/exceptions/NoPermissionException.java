package com.andrey.exceptions;

public class NoPermissionException extends ChatAppException {
    public NoPermissionException(String message) {
        super(message);
    }
}

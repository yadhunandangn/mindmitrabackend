package com.MindMitra.Rolebased.Exception;

public class AccountExistsException extends RuntimeException{
    public AccountExistsException() {
        super();
    }

    public AccountExistsException(String message) {
        super(message);
    }

    public AccountExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

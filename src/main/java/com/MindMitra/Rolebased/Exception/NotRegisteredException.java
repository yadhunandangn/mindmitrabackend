package com.MindMitra.Rolebased.Exception;

public class NotRegisteredException extends RuntimeException{
    public NotRegisteredException() {
        super();
    }

    public NotRegisteredException(String message) {
        super(message);
    }

    public NotRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
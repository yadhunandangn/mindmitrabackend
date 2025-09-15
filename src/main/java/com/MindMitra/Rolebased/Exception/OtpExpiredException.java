package com.MindMitra.Rolebased.Exception;

public class OtpExpiredException extends RuntimeException {

    public OtpExpiredException() {
        super();
    }

    public OtpExpiredException(String message) {
        super(message);
    }

    public OtpExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}

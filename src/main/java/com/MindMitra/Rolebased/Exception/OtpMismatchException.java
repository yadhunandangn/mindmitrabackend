package com.MindMitra.Rolebased.Exception;

public class OtpMismatchException extends RuntimeException {

    public OtpMismatchException() {
        super();
    }

    public OtpMismatchException(String message) {
        super(message);
    }

    public OtpMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

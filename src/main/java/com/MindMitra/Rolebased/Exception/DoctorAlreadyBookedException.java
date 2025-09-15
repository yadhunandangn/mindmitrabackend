package com.MindMitra.Rolebased.Exception;


public class DoctorAlreadyBookedException extends RuntimeException {
    public DoctorAlreadyBookedException(String message) {
        super(message);
    }
}

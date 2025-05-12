package com.prototype.arpartment_managing.exception;

public class ComplaintNotFoundException extends RuntimeException {
    public ComplaintNotFoundException(Long id) {
        super("Could not find complaint with id " + id);
    }
} 
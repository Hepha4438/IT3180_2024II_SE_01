package com.prototype.arpartment_managing.exception;

public class RevenueNotFoundException extends RuntimeException {
    public RevenueNotFoundException(long id) {

        super("Could not found revenue with id " + id);
    }
}

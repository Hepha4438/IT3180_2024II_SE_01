package com.prototype.arpartment_managing.exception;

public class RevenueNotFoundException extends RuntimeException {
    public RevenueNotFoundException(long id) {

        super("not found revenue with id " + id);
    }
}

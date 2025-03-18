package com.prototype.arpartment_managing.exception;

public class ApartmentNotFoundException extends RuntimeException{
    public ApartmentNotFoundException(Long apartmentId){
        super("Could not found user with number "+apartmentId);
    }
}

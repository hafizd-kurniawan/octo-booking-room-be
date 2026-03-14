package com.octo.booking_room.exception;

public class EmailAlreadyExists extends RuntimeException {
    public EmailAlreadyExists() {
        super("email already exists");
    } 
}

package com.sesac.foodtruckorder.exception;

public class DuplicateStoreNameException extends RuntimeException{
    public DuplicateStoreNameException(String message) {
        super(message);
    }
}

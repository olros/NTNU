package com.ntnu.gidd.exception;


public class UserNotFoundException extends EntityNotFoundException {

    private static final String DEFAULT_MESSAGE = "User was not found";

    public UserNotFoundException(String error){
        super(error);
    }

    public UserNotFoundException(){ super(DEFAULT_MESSAGE);}
}


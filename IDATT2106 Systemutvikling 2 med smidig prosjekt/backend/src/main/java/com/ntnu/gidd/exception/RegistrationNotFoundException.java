package com.ntnu.gidd.exception;

public class RegistrationNotFoundException extends EntityNotFoundException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE = "Registration does not exist";

    public RegistrationNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public RegistrationNotFoundException(){
        super(DEFAULT_MESSAGE);
    }
}

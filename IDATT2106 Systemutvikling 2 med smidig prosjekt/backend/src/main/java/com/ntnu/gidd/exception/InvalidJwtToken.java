package com.ntnu.gidd.exception;

public class InvalidJwtToken extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Invalid Jwt token";

    public InvalidJwtToken(String error) {
        super(error);
    }

    public InvalidJwtToken() {
        super(DEFAULT_MESSAGE);
    }
}

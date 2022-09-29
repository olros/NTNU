package com.ntnu.gidd.exception;

public class InvalidUnInviteException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "You can't uninvite a user that is registered for the activity";

    public InvalidUnInviteException(String error) {
        super(error);
    }

    public InvalidUnInviteException() {
        super(DEFAULT_MESSAGE);
    }
}

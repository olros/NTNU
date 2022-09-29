package com.ntnu.gidd.exception;

public class NotInvitedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "This user is not invited to this private activity";

    public NotInvitedException(String error) {
        super(error);
    }

    public NotInvitedException() {
        super(DEFAULT_MESSAGE);
    }
}

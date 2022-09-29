package com.ntnu.gidd.exception;

public class InvalidFollowRequestException extends RuntimeException {
    public InvalidFollowRequestException(String message) {
        super(message);
    }
}

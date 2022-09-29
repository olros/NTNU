package com.ntnu.gidd.exception;

import org.springframework.security.core.AuthenticationException;


public class JwtExpiredTokenException extends AuthenticationException {
    public JwtExpiredTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

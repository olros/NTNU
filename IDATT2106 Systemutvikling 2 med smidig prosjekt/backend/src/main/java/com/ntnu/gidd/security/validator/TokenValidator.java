package com.ntnu.gidd.security.validator;

public interface TokenValidator {
    void validate(String jti);
}

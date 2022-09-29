package com.ntnu.gidd.exception;

public class InvalidResetPasswordToken extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Invalid reset password token";

	public InvalidResetPasswordToken(String error) {
		super(error);
	}

	public InvalidResetPasswordToken() {
		super(DEFAULT_MESSAGE);
	}
}


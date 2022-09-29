package com.ntnu.gidd.exception;

public class ResetPasswordTokenNotFoundException extends EntityNotFoundException {

	private static final String DEFAULT_MESSAGE = "Reset-password token was not found";

	public ResetPasswordTokenNotFoundException(String errorMessage) {
		super(errorMessage);
	}
	
	public ResetPasswordTokenNotFoundException() {
		super(DEFAULT_MESSAGE);
	}
}




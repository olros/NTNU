package com.ntnu.gidd.exception;

import javax.persistence.EntityExistsException;

public class EmailInUseException extends EntityExistsException {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MESSAGE = "Email is already associated with another user";

	public EmailInUseException(String errorMessage) {
		super(errorMessage);
	}

	public EmailInUseException() {
		super(DEFAULT_MESSAGE);
	}
}

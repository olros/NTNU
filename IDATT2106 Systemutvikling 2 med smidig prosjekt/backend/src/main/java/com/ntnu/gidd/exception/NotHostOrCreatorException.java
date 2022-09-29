package com.ntnu.gidd.exception;

public class NotHostOrCreatorException extends RuntimeException{

  public NotHostOrCreatorException(String errorMessage) {
    super(errorMessage);
  }

  public NotHostOrCreatorException() {
    super("User is neither host or creator");
  }
}

package com.ntnu.gidd.exception;


public class CommentNotFoundException extends EntityNotFoundException {

  public CommentNotFoundException(String errorMessage) {
    super(errorMessage);
  }

  public CommentNotFoundException() {
    super("Comment was not found");
  }
}

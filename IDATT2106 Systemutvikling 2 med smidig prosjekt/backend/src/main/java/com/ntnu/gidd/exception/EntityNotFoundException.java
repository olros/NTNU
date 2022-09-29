package com.ntnu.gidd.exception;

public class EntityNotFoundException extends RuntimeException{
      
      private static final String DEFAULT_MESSAGE = "Entity is not found";
      
      public EntityNotFoundException(String errorMessage){
            super(errorMessage);
      }
      
      public EntityNotFoundException() {
            super(DEFAULT_MESSAGE);
      }
      
}

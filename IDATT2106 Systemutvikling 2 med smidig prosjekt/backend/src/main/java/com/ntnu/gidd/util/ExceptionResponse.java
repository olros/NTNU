package com.ntnu.gidd.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionResponse extends Response{
      private final Object data;

      public ExceptionResponse(String message, Object data) {
            super(message);
            this.data = data;
      }
}

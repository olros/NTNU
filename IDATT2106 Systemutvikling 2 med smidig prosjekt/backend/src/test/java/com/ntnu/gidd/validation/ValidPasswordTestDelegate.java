package com.ntnu.gidd.validation;

import lombok.*;

@AllArgsConstructor
public class ValidPasswordTestDelegate {
      @ValidPassword
      String password;
}

package com.ntnu.gidd.dto.User;

import com.ntnu.gidd.validation.FieldMatch;
import com.ntnu.gidd.validation.ValidPassword;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {
      @NotNull
      @NotEmpty
      private String firstName;

      @NotNull
      @NotEmpty
      private String surname;

      
      @NotNull
      @NotEmpty
      @ValidPassword
      private String password;

      @NotNull
      @NotEmpty
      @Email
      private String email;

      private LocalDate birthDate;
}
package com.ntnu.gidd.dto.Registration;

import com.ntnu.gidd.dto.User.UserDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationUserDto {
    private UserDto user;
}

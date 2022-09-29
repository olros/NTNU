package com.ntnu.gidd.dto.User;

import com.ntnu.gidd.model.PasswordResetToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordForgotDto {
		String email;
	}

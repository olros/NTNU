package com.ntnu.gidd.dto.User;

import com.ntnu.gidd.model.PasswordResetToken;
import com.ntnu.gidd.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordResetDto {
	
	String email;
	@ValidPassword
	String newPassword;
	
}

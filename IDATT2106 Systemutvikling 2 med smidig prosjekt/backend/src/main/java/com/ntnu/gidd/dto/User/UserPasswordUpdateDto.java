package com.ntnu.gidd.dto.User;

import com.ntnu.gidd.validation.ValidPassword;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordUpdateDto {
	@NonNull
	private String oldPassword;
	@NonNull
	@ValidPassword
	private String newPassword;
}

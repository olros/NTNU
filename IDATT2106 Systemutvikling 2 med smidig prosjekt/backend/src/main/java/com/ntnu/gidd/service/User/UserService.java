package com.ntnu.gidd.service.User;

import com.ntnu.gidd.dto.User.UserDto;
import com.ntnu.gidd.dto.User.UserPasswordResetDto;
import com.ntnu.gidd.dto.User.UserPasswordUpdateDto;
import com.ntnu.gidd.dto.User.UserRegistrationDto;
import com.ntnu.gidd.model.User;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ntnu.gidd.model.PasswordResetToken;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.UUID;

@Service
public interface UserService {
      UserDto updateUser(UUID id, UserDto user);
      UserDto getUserDtoByEmail(String email);
      UserDto saveUser(UserRegistrationDto user);
      UserDto deleteUser(String email);
      void changePassword(Principal principal, UserPasswordUpdateDto user);
      Page<UserDto> getAll(Predicate predicate, Pageable pageable);
      UserDto getUserByUUID(UUID userId);
      void forgotPassword(String email) throws MessagingException;
      void validateResetPassword(UserPasswordResetDto user, UUID passwordResetTokenId);

      User getUserById(UUID actorId);
}

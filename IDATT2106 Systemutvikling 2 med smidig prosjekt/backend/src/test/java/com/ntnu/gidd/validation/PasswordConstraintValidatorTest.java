package com.ntnu.gidd.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.*;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PasswordConstraintValidatorTest {

      @Autowired
      private Validator validator;

      private ValidPasswordTestDelegate testDelegate;

      @BeforeEach
      public void setUp() {
      }

      /**
       * Ensures that there isn't any errors when password isn't sufficiently strong
       */
      @ParameterizedTest
      @MethodSource("invalidPasswords")
      public void testInvalidPassword(String password) {
            testDelegate = new ValidPasswordTestDelegate(password);

            Set<ConstraintViolation<ValidPasswordTestDelegate>> violations = validator.validate(testDelegate);
            violations.forEach(v -> {
            });
            assertFalse(violations.isEmpty());
      }

      /**
       * Ensures that there isn't any errors when password is sufficiently strong
       */
      @ParameterizedTest
      @MethodSource("validPasswords")
      public void testValidPassword(String password) {
            testDelegate = new ValidPasswordTestDelegate(password);

            Set<ConstraintViolation<ValidPasswordTestDelegate>> violations = validator.validate(testDelegate);
            assertTrue(violations.isEmpty());
      }

      public static Stream<Arguments> invalidPasswords(){
            return Stream.of(
                  Arguments.of("onlylower"),
                  Arguments.of("ONLYUPPER"),
                  Arguments.of("19040951943"),
                  Arguments.of("2chargroups"),
                  Arguments.of("2CHARGROUPS"),
                  Arguments.of("two#chargroups$[{"),
                  Arguments.of("<8Char"),
                  Arguments.of("$Password With Space$")
            );
      }

      public static Stream<Arguments> validPasswords(){
            return Stream.of(
                  Arguments.of("3GroupsofChars"),
                  Arguments.of("3group$ofchars"),
                  Arguments.of("3GROUP$OFCHARS"),
                  Arguments.of(">8Characters")
            );
      }
}

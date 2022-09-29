package com.ntnu.gidd.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MatchingFieldValidatorTest {

      private Validator validator;

      @BeforeEach
      public void setUp() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
      }

      /**
       * Ensures that an error occurs when two fields is unequal
       */
      @Test
      public void testNonMatchingFields() {
            MatchingFieldsTestDelegate testDelegate = new MatchingFieldsTestDelegate("word", "differentword");

            Set<ConstraintViolation<MatchingFieldsTestDelegate>> violations = validator.validate(testDelegate);
            assertFalse(violations.isEmpty());
      }

      /**
       * Ensures that there isn't any errors when two fields are the same
       */
      @Test
      public void testMatchingFields() {
            MatchingFieldsTestDelegate testDelegate = new MatchingFieldsTestDelegate("same word", "same word");

            Set<ConstraintViolation<MatchingFieldsTestDelegate>> violations = validator.validate(testDelegate);
            assertTrue(violations.isEmpty());
      }

}

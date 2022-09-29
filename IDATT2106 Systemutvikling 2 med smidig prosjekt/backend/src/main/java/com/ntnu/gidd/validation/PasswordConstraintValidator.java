package com.ntnu.gidd.validation;

import com.google.common.base.Joiner;
import com.ntnu.gidd.util.NordicCharacterData;
import lombok.SneakyThrows;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
      public void initialize(ValidPassword constraint) {
      }

      @SneakyThrows
      public boolean isValid(String obj, ConstraintValidatorContext context) {
            Properties props = new Properties();
            InputStream inputStream = getClass()
                  .getClassLoader().getResourceAsStream("password/messages.properties");
            assert inputStream != null;
            props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            MessageResolver resolver = new PropertiesMessageResolver(props);

            CharacterCharacteristicsRule containsCharacterRules = new CharacterCharacteristicsRule(
                  3,
                  new CharacterRule(NordicCharacterData.LowerCase, 1),
                  new CharacterRule(NordicCharacterData.UpperCase, 1),
                  new CharacterRule(NordicCharacterData.Digit, 1),
                  new CharacterRule(NordicCharacterData.Special, 1)
            );

            PasswordValidator passwordValidator = new PasswordValidator(resolver, Arrays.asList(
                  new LengthRule(8, 100),
                  containsCharacterRules,
                  new WhitespaceRule()
            ));

            RuleResult result = passwordValidator.validate(new PasswordData(obj));
            if (result.isValid()) {
                  return true;
            }

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                  Joiner.on(",").join(passwordValidator.getMessages(result)))
                  .addConstraintViolation();
            return false;
      }
}

package com.ntnu.gidd.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@FieldMatch(field = "field", fieldMatch = "field2")
public class MatchingFieldsTestDelegate {
      String field;
      String field2;
}

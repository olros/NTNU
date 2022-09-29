package com.ntnu.gidd.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class HtmlTemplate {
      private String template;
      private Map<String, Object> props;
}
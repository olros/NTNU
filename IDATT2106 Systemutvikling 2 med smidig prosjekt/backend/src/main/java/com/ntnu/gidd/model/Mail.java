package com.ntnu.gidd.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Mail {

      @NotNull
      private String from;

      @NotNull
      private String to;

      private String subject;

      @NotNull
      private HtmlTemplate htmlTemplate;
}

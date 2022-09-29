package com.ntnu.gidd.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.passay.CharacterData;

@Getter
@AllArgsConstructor
public enum NordicCharacterData implements CharacterData {
      LowerCase("INSUFFICIENT_LOWERCASE", "abcdefghijklmnopqrstuvwxyzæøå"),
      UpperCase("INSUFFICIENT_UPPERCASE", "ABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ"),
      Digit("INSUFFICIENT_DIGIT", "0123456789"),
      Alphabetical("INSUFFICIENT_ALPHABETICAL", UpperCase.getCharacters() + LowerCase.getCharacters()),
      Special("INSUFFICIENT_SPECIAL", "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~¡¢£¤¥¦§¨©ª«¬\u00ad®¯°±²³´µ¶·¸¹º»¼½¾¿×÷–—―‗‘’‚‛“”„†‡•…‰′″‹›‼‾⁄⁊₠₡₢₣₤₥₦₧₨₩₪₫€₭₮₯₰₱₲₳₴₵₶₷₸₹₺₻₼₽₾");

      private final String errorCode;
      private final String characters;

      public String getErrorCode() {
            return this.errorCode;
      }

      public String getCharacters() {
            return this.characters;
      }
}

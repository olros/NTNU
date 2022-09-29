package com.ntnu.gidd.utils;

import java.util.Random;

public class StringRandomizer {
    public static String getRandomString(int length){

        String upperCaseLetters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++){
            int j = random.nextInt(upperCaseLetters.length());
            stringBuilder.append(upperCaseLetters.charAt(j));
        }
        return stringBuilder.toString();
    }

    public static String getRandomEmail() {
        return getRandomString(8) + "@" + getRandomString(4) + ".com";
    }
}

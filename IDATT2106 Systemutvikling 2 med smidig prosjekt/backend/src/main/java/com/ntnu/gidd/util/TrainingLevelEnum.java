package com.ntnu.gidd.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum TrainingLevelEnum {
    High("High"),
    Medium("Medium"),
    Low("Low");

    private String code;

    private TrainingLevelEnum(String code) {
        this.code=code;
    }

    @JsonCreator
    public static TrainingLevelEnum decode(final String code) {
        return Stream.of(TrainingLevelEnum.values()).filter(targetEnum -> targetEnum.code.equals(code)).findFirst().orElse(null);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}

package com.ntnu.gidd.exception;

public class ActivityFullExecption extends RuntimeException {


    public ActivityFullExecption(String errorMessage) {
        super(errorMessage);
    }

    public ActivityFullExecption() {

        super("This activity is full");
    }
}

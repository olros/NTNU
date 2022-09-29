package com.ntnu.gidd.exception;

public class ActivityClosedExecption extends RuntimeException {


    public ActivityClosedExecption(String errorMessage) {
        super(errorMessage);
    }

    public ActivityClosedExecption() {

        super("This activity is closed");
    }
}

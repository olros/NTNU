package com.ntnu.gidd.exception;

public class PostNotFoundExecption extends EntityNotFoundException {
    public PostNotFoundExecption() {
        super("Can't find Post");
    }
}

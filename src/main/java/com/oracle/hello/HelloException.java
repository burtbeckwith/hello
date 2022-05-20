package com.oracle.hello;

import java.io.Serial;

public class HelloException extends RuntimeException {

    @Serial private static final long serialVersionUID = 1;

    public HelloException(String message) {
        super(message);
    }
}

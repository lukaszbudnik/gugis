package com.github.lukaszbudnik.gugis;

public class GugisException extends RuntimeException {
    public GugisException() {
        super();
    }

    public GugisException(String message) {
        super(message);
    }

    public GugisException(String message, Throwable cause) {
        super(message, cause);
    }

    public GugisException(Throwable cause) {
        super(cause);
    }

    protected GugisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

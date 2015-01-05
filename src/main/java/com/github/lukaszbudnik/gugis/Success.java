package com.github.lukaszbudnik.gugis;

public class Success<T> implements Try<T> {

    private final T result;

    public Success(T result) {
        this.result = result;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public T get() {
        return result;
    }

    @Override
    public Exception failure() {
        throw new IllegalStateException("Success does not have failure");
    }
}

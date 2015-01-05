package com.github.lukaszbudnik.gugis;

public class Failure<T> implements Try<T> {

    private Throwable failure;

    public Failure(Throwable failure) {
        this.failure = failure;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public T get() {
        throw new IllegalStateException("Success does not have failure");
    }

    @Override
    public Throwable failure() {
        return failure;
    }
}

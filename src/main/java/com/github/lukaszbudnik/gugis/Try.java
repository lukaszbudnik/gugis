package com.github.lukaszbudnik.gugis;

public interface Try<T> {
    boolean isFailure();
    boolean isSuccess();
    T get();
    Throwable failure();
}

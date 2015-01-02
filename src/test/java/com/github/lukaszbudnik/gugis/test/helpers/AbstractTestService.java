package com.github.lukaszbudnik.gugis.test.helpers;

// Mockito and EasyMock are swallowing annotations when creating mocks
// thus a home made workaround for tracing if method was called
public class AbstractTestService {
    private boolean called;

    public void called() {
        called = true;
    }

    public boolean wasCalled() {
        return called;
    }

    public void reset() {
        called = false;
    }
}

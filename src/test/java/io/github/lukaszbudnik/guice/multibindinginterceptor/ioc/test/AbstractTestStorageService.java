package io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.test;

// Mockito and EasyMock are swallowing annotations when creating mocks
// thus a home made workaround for tracing if method was called
public class AbstractTestStorageService {
    private boolean called;

    void called() {
        called = true;
    }

    boolean wasCalled() {
        return called;
    }

    void reset() {
        called = false;
    }
}

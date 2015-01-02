package com.github.lukaszbudnik.guice.multibindinginterceptor.ioc.test;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<StorageService> binder = Multibinder.newSetBinder(binder(), StorageService.class);
        binder.addBinding().to(StorageService1Impl.class);
        binder.addBinding().to(StorageService2Impl.class);

        bind(CompositeStorageService.class);
    }
}

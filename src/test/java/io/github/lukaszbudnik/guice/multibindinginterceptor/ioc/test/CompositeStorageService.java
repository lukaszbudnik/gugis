package io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.test;

import io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.Propagation;
import io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.Replicate;

public class CompositeStorageService implements StorageService {

    @Replicate(propagation = Propagation.ALL)
    @Override
    public int put(String item) {
        return 0;
    }

    @Replicate(propagation = Propagation.ANY)
    @Override
    public String get(int id) {
        return null;
    }

    @Replicate(propagation = Propagation.SECONDARY_ONLY)
    @Override
    public void refresh(int id) {
    }

    @Replicate(propagation = Propagation.PRIMARY_ONLY)
    @Override
    public void delete(int id) {
    }
}

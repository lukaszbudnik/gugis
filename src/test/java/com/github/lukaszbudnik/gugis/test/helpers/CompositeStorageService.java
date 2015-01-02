package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Composite;
import com.github.lukaszbudnik.gugis.Propagation;
import com.github.lukaszbudnik.gugis.Replicate;

import javax.inject.Singleton;

@Composite
@Singleton
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

    @Replicate(propagation = Propagation.SECONDARY)
    @Override
    public void refresh(int id) {
    }

    @Replicate(propagation = Propagation.PRIMARY)
    @Override
    public void delete(int id) {
    }
}

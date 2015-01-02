package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Primary;
import lombok.extern.log4j.Log4j2;

import javax.inject.Singleton;

@Primary
@Singleton
@Log4j2
public class StorageService2Impl extends AbstractTestService implements StorageService {

    @Override
    public int put(String item) {
        log.trace(this.getClass().getSimpleName() + ".put = " + item);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        called();
        return 2;
    }

    @Override
    public String get(int id) {
        called();
        log.trace(this.getClass().getSimpleName() + ".get = " + id);
        return "null 2";
    }

    @Override
    public void refresh(int id) {
        called();
        log.trace(this.getClass().getSimpleName() + ".refresh = " + id);
    }

    @Override
    public void delete(int id) {
        called();
        log.trace(this.getClass().getSimpleName() + ".delete = " + id);
    }
}

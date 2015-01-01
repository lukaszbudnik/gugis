package io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.test;

import com.google.inject.Singleton;
import io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.Secondary;
import lombok.extern.log4j.Log4j2;

@Secondary
@Singleton
@Log4j2
public class StorageService1Impl extends AbstractTestStorageService implements StorageService {

    @Override
    public int put(String item) {
        log.trace(this.getClass().getSimpleName() + ".put = " + item);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        called();
        return 1;
    }

    @Override
    public String get(int id) {
        called();
        log.trace(this.getClass().getSimpleName() + ".get = " + id);
        return "null 1";
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

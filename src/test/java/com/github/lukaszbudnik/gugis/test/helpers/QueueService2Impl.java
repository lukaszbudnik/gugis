package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Primary;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Singleton
@Primary
public class QueueService2Impl extends AbstractTestService implements QueueService {

    @Override
    public void publish(String item) {
        log.trace("publish = " + item);
        called();
        throw new RuntimeException(this.getClass().getCanonicalName());
    }

    @Override
    public String consume() {
        log.trace("consume");
        called();
        throw new RuntimeException(this.getClass().getCanonicalName());
    }

    @Override
    public void delete(String item) {
        log.trace("delete " + item);
        called();
        throw new RuntimeException(this.getClass().getCanonicalName());
    }
}

package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Primary;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Singleton
@Primary
public class QueueService1Impl extends AbstractTestService implements QueueService {

    @Override
    public void publish(String item) {
        log.trace("publish = " + item);
        called();
    }

    @Override
    public String consume() {
        log.trace("consume");
        called();
        return "consumed 1";
    }

    @Override
    public void delete(String item) {
        log.trace("delete " + item);
        called();
        throw new RuntimeException(this.getClass().getCanonicalName());
    }
}

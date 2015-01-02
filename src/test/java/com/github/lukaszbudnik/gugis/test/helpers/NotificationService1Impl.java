package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Primary;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Primary
@Singleton
@Slf4j
public class NotificationService1Impl extends AbstractTestService implements NotificationService {
    @Override
    public void sendNotification(String to) {
        log.trace(this.getClass().getSimpleName() + ".sendNotification = " + to);
        called();
    }
}

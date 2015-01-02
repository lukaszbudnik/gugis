package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Primary;
import lombok.extern.log4j.Log4j2;

import javax.inject.Singleton;

@Primary
@Singleton
@Log4j2
public class NotificationService1Impl extends AbstractTestService implements NotificationService {
    @Override
    public void sendNotification(String to) {
        log.trace(this.getClass().getSimpleName() + ".sendNotification = " + to);
        called();
    }
}

package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Composite;
import com.github.lukaszbudnik.gugis.Propagation;
import com.github.lukaszbudnik.gugis.Replicate;

import javax.inject.Singleton;

@Composite(autodiscover = false)
@Singleton
public class CompositeNotificationService implements NotificationService {

    @Replicate(propagation = Propagation.ALL)
    @Override
    public void sendNotification(String to) {
    }
}

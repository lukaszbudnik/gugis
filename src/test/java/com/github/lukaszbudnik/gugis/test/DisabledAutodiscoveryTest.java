package com.github.lukaszbudnik.gugis.test;

import com.github.lukaszbudnik.gugis.GugisException;
import com.github.lukaszbudnik.gugis.GugisModule;
import com.github.lukaszbudnik.gugis.test.helpers.CompositeNotificationService;
import com.github.lukaszbudnik.gugis.test.helpers.NotificationService1Impl;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(OnamiRunner.class)
@GuiceModules(GugisModule.class)
public class DisabledAutodiscoveryTest {

    @Inject
    private CompositeNotificationService compositeNotificationService;

    @Inject
    private NotificationService1Impl notificationService;

    @After
    @Before
    public void reset() {
        notificationService.reset();
    }

    @Test
    public void shouldInjectInstances() {
        // Guice will inject instances but replication will not work
        Assert.assertNotNull(compositeNotificationService);
        Assert.assertNotNull(notificationService);
    }

    @Test(expected = GugisException.class)
    public void shouldNotReplicateWhenAutodiscoverSetToFalse() {
        Assert.assertFalse(notificationService.wasCalled());

        compositeNotificationService.sendNotification("to");
    }
}

package com.github.lukaszbudnik.gugis.test;

import com.github.lukaszbudnik.gugis.GugisModule;
import com.github.lukaszbudnik.gugis.test.helpers.CompositeNotificationService;
import com.github.lukaszbudnik.gugis.test.helpers.NotificationService;
import com.github.lukaszbudnik.gugis.test.helpers.NotificationService1Impl;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(OnamiRunner.class)
public class DisabledAutodiscoveryManualDiscoveryTest {

    @Inject
    private CompositeNotificationService compositeNotificationService;

    @Inject
    private NotificationService1Impl notificationService;

    @GuiceProvidedModules
    public static Module[] modules() {
        return new Module[]{
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(CompositeNotificationService.class);

                        Multibinder<NotificationService> binder = Multibinder.newSetBinder(binder(), NotificationService.class);
                        binder.addBinding().to(NotificationService1Impl.class);
                    }
                },
                new GugisModule()
        };
    }

    @After
    @Before
    public void reset() {
        notificationService.reset();
    }

    @Test
    public void shouldInjectInstances() {
        Assert.assertNotNull(compositeNotificationService);
        // composite instance is enhanced by Guice
        Assert.assertNotEquals(CompositeNotificationService.class, compositeNotificationService.getClass());
        Assert.assertTrue(compositeNotificationService instanceof CompositeNotificationService);
    }

    @Test
    public void shouldReplicateWhenAutodiscoverSetToFalseManualDiscoveryUsedAnd() {
        Assert.assertFalse(notificationService.wasCalled());

        compositeNotificationService.sendNotification("to");

        Assert.assertTrue(notificationService.wasCalled());
    }
}

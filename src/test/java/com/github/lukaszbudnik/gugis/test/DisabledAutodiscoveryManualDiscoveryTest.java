/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.gugis.test;

import com.github.lukaszbudnik.gugis.GugisModule;
import com.github.lukaszbudnik.gugis.test.helpers.NotificationServiceComposite;
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
    private NotificationServiceComposite notificationServiceComposite;

    @Inject
    private NotificationService1Impl notificationService;

    @GuiceProvidedModules
    public static Module[] modules() {
        return new Module[]{
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(NotificationServiceComposite.class);

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
        Assert.assertNotNull(notificationServiceComposite);
        // composite instance is enhanced by Guice
        Assert.assertNotEquals(NotificationServiceComposite.class, notificationServiceComposite.getClass());
        Assert.assertTrue(notificationServiceComposite instanceof NotificationServiceComposite);
    }

    @Test
    public void shouldReplicateWhenAutodiscoverSetToFalseManualDiscoveryUsedAnd() {
        Assert.assertFalse(notificationService.wasCalled());

        notificationServiceComposite.sendNotification("to");

        Assert.assertTrue(notificationService.wasCalled());
    }
}

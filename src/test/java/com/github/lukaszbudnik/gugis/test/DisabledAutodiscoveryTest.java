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

import com.github.lukaszbudnik.gugis.GugisException;
import com.github.lukaszbudnik.gugis.GugisModule;
import com.github.lukaszbudnik.gugis.test.helpers.NotificationService1Impl;
import com.github.lukaszbudnik.gugis.test.helpers.NotificationServiceComposite;
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
    private NotificationServiceComposite notificationServiceComposite;

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
        Assert.assertNotNull(notificationServiceComposite);
        Assert.assertNotNull(notificationService);
    }

    @Test(expected = GugisException.class)
    public void shouldNotReplicateWhenAutodiscoverSetToFalse() {
        Assert.assertFalse(notificationService.wasCalled());

        notificationServiceComposite.sendNotification("to");
    }
}

/**
 * Copyright (C) 2015-2017 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.gugis.test;

import com.github.lukaszbudnik.gugis.GugisException;
import com.github.lukaszbudnik.gugis.NonValidatingGugisModule;
import com.github.lukaszbudnik.gugis.test.helpers.QueueService;
import com.github.lukaszbudnik.gugis.test.helpers.QueueService1Impl;
import com.github.lukaszbudnik.gugis.test.helpers.QueueService2Impl;
import com.github.lukaszbudnik.gugis.test.helpers.QueueServiceComposite;
import com.google.common.base.Throwables;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Set;

@RunWith(OnamiRunner.class)
@GuiceModules(NonValidatingGugisModule.class)
public class ErrorHandlingTest {

    @Inject
    private Set<QueueService> queueServices;

    @Inject
    private QueueServiceComposite queueServiceComposite;

    @Inject
    private QueueService1Impl primary1;

    @Inject
    private QueueService2Impl primary2;

    @After
    @Before
    public void reset() {
        primary1.reset();
        primary2.reset();
    }

    @Test
    public void shouldInjectAllImplementations() {
        Assert.assertEquals(2, queueServices.size());
    }

    @Test
    public void shouldInjectCompositeService() {
        Assert.assertNotNull(queueServiceComposite);
    }

    @Test
    public void shouldFailWithGugisException() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());

        // QueueService2Impl is throwing exception
        // by default allowFailure is set to false
        // this invocation will throw GugisException
        try {
            queueServiceComposite.publish("test");
        } catch (GugisException e) {
            Assert.assertEquals(QueueService2Impl.class.getSimpleName() + " exception in publish!", Throwables.getRootCause(e).getMessage());
        } catch (Throwable t) {
            Assert.fail("GugisException expected but got " + t.getClass());
        }
    }

    @Test
    public void shouldAllowFailureAndReturnValue() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());

        // QueueService2Impl is throwing exception
        // but consume has allowFailure set to true
        String result = queueServiceComposite.consume();

        Assert.assertTrue(primary1.wasCalled());
        Assert.assertTrue(primary2.wasCalled());
        Assert.assertEquals("consumed 1", result);
    }

    @Test
    public void shouldFailWithGugisExceptionWhenAllowFailureSetToTrueAndAllFailed() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());

        // QueueService1Impl and QueueService2Impl are throwing exception
        // delete has allowFailure set to true, but because both implementations
        // are throwing exception this composite call will fail with GugisException
        try {
            queueServiceComposite.delete("item");
            Assert.fail("GugisException expected");
        } catch (GugisException e) {
            // general information that QueueService.delete failed
            Assert.assertTrue(e.getMessage().contains("No result for ALL found for " + QueueService.class.getCanonicalName() + ".delete"));
            // detailed errors follow
            // QueueService1Impl
            Assert.assertTrue(e.getMessage().contains("java.lang.RuntimeException: " + QueueService1Impl.class.getSimpleName() + " exception in delete!"));
            // QueueService2Impl
            Assert.assertTrue(e.getMessage().contains("java.lang.IllegalArgumentException: " + QueueService2Impl.class.getSimpleName() + " exception in delete!"));
        } catch (Throwable t) {
            Assert.fail("GugisException expected but got " + t.getClass());
        }
    }

    @Test
    public void shouldSelectSlowerBecauseFasterThrowsException() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());

        // QueueService1Impl is slow (Thread.sleep) but passes
        // QueueService2Impl is fast but throws exception
        // stats has allowFailure set to true it should pass
        // and return value from QueueService1Impl
        int result = queueServiceComposite.stats();

        Assert.assertTrue(primary1.wasCalled());
        Assert.assertTrue(primary2.wasCalled());
        Assert.assertEquals(123, result);
    }

    @Test
    public void shouldSelectAlwaysFirstImplAsSecondThrowsException() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());

        // permissions' propagation is set to Random with allowFailure = true
        // but QueueService1Impl is throwing exception
        // for all calls to permissions QueueService2Impl will succeed

        for (int i = 0; i < 100; i++) {
            String result = queueServiceComposite.permissions();

            // primary1 throws exception and doesn't call called()
            Assert.assertFalse(primary1.wasCalled());
            // primary2 is executed fine
            Assert.assertTrue(primary2.wasCalled());

            // result is taken from primary2
            Assert.assertEquals("usera:write;userb:read", result);

            // reset test services
            reset();
        }
    }

}

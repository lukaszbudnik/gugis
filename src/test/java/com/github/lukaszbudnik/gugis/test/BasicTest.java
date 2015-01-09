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

import com.github.lukaszbudnik.gugis.NonValidatingGugisModule;
import com.github.lukaszbudnik.gugis.test.helpers.*;
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
public class BasicTest {

    @Inject
    private Set<StorageService> storageServices;

    @Inject
    private StorageServiceComposite storageServiceComposite;

    @Inject
    private StorageService1Impl secondary;

    @Inject
    private StorageService2Impl primary1;

    @Inject
    private StorageService3Impl primary2;

    @After
    @Before
    public void reset() {
        primary1.reset();
        primary2.reset();
        secondary.reset();
    }

    @Test
    public void shouldInjectAllImplementations() {
        Assert.assertEquals(3, storageServices.size());
    }

    @Test
    public void shouldInjectCompositeService() {
        Assert.assertNotNull(storageServiceComposite);
    }

    @Test
    public void shouldPropagateToAll() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        storageServiceComposite.put("test");

        Assert.assertTrue(primary1.wasCalled());
        Assert.assertTrue(primary2.wasCalled());
        Assert.assertTrue(secondary.wasCalled());
    }

    @Test(timeout = 1000)
    public void shouldPropagateToRandom() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        storageServiceComposite.get(0);

        Assert.assertTrue(primary1.wasCalled() | primary2.wasCalled() | secondary.wasCalled());
        Assert.assertFalse(primary1.wasCalled() & primary2.wasCalled() & secondary.wasCalled());

        // now loop until all three implementations will be called
        // JUnit will timeout after 1000 milliseconds
        // one call was already made above
        int counter = 1;
        do {
            storageServiceComposite.get(0);
            counter++;
        } while (!(primary1.wasCalled() & primary2.wasCalled() & secondary.wasCalled()));

        Assert.assertTrue(primary1.wasCalled() & primary2.wasCalled() & secondary.wasCalled());
    }

    @Test
    public void shouldPropagateToPrimary() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        storageServiceComposite.delete(0);

        Assert.assertTrue(primary1.wasCalled());
        Assert.assertTrue(primary2.wasCalled());
        Assert.assertFalse(secondary.wasCalled());
    }

    @Test
    public void shouldPropagateToSecondary() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        storageServiceComposite.refresh(0);

        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());
        Assert.assertTrue(secondary.wasCalled());
    }

    @Test
    public void shouldPropagateToFastest() {
        Assert.assertFalse(primary1.wasCalled());
        Assert.assertFalse(primary2.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        String result = storageServiceComposite.fastGet(0);

        // all components were called
        Assert.assertTrue(primary1.wasCalled());
        Assert.assertTrue(secondary.wasCalled());
        Assert.assertTrue(primary2.wasCalled());

        // the fastest is StorageService3Impl
        Assert.assertEquals("null 3 - the fastest", result);
    }

}

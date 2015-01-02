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
import com.github.lukaszbudnik.gugis.test.helpers.CompositeStorageService;
import com.github.lukaszbudnik.gugis.test.helpers.StorageService;
import com.github.lukaszbudnik.gugis.test.helpers.StorageService1Impl;
import com.github.lukaszbudnik.gugis.test.helpers.StorageService2Impl;
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
@GuiceModules(GugisModule.class)
public class BasicTest {

    @Inject
    private Set<StorageService> storageServices;

    @Inject
    private CompositeStorageService compositeStorageService;

    @Inject
    private StorageService1Impl secondary;

    @Inject
    private StorageService2Impl primary;

    @After
    @Before
    public void reset() {
        primary.reset();
        secondary.reset();
    }

    @Test
    public void shouldInjectAllImplementations() {
        Assert.assertEquals(2, storageServices.size());
    }

    @Test
    public void shouldInjectCompositeService() {
        Assert.assertNotNull(compositeStorageService);
    }

    @Test
    public void shouldPropagateToAll() {
        Assert.assertFalse(primary.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        compositeStorageService.put("test");

        Assert.assertTrue(primary.wasCalled());
        Assert.assertTrue(secondary.wasCalled());
    }

    @Test
    public void shouldPropagateToAny() {
        Assert.assertFalse(primary.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        compositeStorageService.get(0);

        Assert.assertTrue(primary.wasCalled() | secondary.wasCalled());
        Assert.assertFalse(primary.wasCalled() & secondary.wasCalled());
    }

    @Test
    public void shouldPropagateToPrimary() {
        Assert.assertFalse(primary.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        compositeStorageService.delete(0);

        Assert.assertTrue(primary.wasCalled());
        Assert.assertFalse(secondary.wasCalled());
    }

    @Test
    public void shouldPropagateToSecondary() {
        Assert.assertFalse(primary.wasCalled());
        Assert.assertFalse(secondary.wasCalled());

        compositeStorageService.refresh(0);

        Assert.assertFalse(primary.wasCalled());
        Assert.assertTrue(secondary.wasCalled());
    }

}


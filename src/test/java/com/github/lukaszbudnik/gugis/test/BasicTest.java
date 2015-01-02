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


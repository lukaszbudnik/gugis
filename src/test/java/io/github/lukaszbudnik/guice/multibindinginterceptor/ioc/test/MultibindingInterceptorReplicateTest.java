package io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.test;

import io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.MultibindingInterceptorModule;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceModules;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Set;

@RunWith(OnamiRunner.class)
@GuiceModules({MultibindingInterceptorModule.class, TestModule.class})
public class MultibindingInterceptorReplicateTest {

    @Inject
    private Set<StorageService> storageServices;

    @Inject
    private CompositeStorageService compositeStorageService;

    @Inject
    private StorageService1Impl secondary;

    @Inject
    private StorageService2Impl primary;

    @After
    public void after() {
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


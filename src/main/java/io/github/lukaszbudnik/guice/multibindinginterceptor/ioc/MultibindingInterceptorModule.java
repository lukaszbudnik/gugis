package io.github.lukaszbudnik.guice.multibindinginterceptor.ioc;


import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class MultibindingInterceptorModule extends AbstractModule {
    @Override
    protected void configure() {
        ReplicatorInterceptor replicatorInterceptor = new ReplicatorInterceptor();
        requestInjection(replicatorInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Replicate.class), replicatorInterceptor);
    }
}

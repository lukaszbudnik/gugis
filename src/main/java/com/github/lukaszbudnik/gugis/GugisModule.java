package com.github.lukaszbudnik.gugis;


import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import lombok.extern.slf4j.Slf4j;
import org.atteo.classindex.ClassIndex;

import java.lang.annotation.Annotation;
import java.util.stream.StreamSupport;

@Slf4j
public class GugisModule extends AbstractModule {
    @Override
    protected void configure() {
        GugisReplicatorInterceptor gugisReplicatorInterceptor = new GugisReplicatorInterceptor();
        requestInjection(gugisReplicatorInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Replicate.class), gugisReplicatorInterceptor);

        for (Class<?> compositeClass : ClassIndex.getAnnotated(Composite.class)) {
            Composite compositeAnnotation = compositeClass.getAnnotation(Composite.class);
            if (!compositeAnnotation.autodiscover()) {
                if (log.isDebugEnabled()) {
                    log.debug("Composite class " + compositeClass.getCanonicalName() + " has autodiscover flag set to false. Skipping.");
                }
                continue;
            }

            bind(compositeClass);

            Class classInterface = compositeClass.getInterfaces()[0];
            Multibinder<?> multibinder = Multibinder.newSetBinder(binder(), classInterface);
            bind(multibinder, classInterface, Primary.class);
            bind(multibinder, classInterface, Secondary.class);
        }
    }

    private void bind(Multibinder multibinder, Class<?> classInterface, Class<? extends Annotation> annotation) {
        StreamSupport.stream(ClassIndex.getAnnotated(annotation).spliterator(), true)
                .filter(c -> classInterface.isAssignableFrom(c))
                .forEach(c -> multibinder.addBinding().to(c));
    }
}

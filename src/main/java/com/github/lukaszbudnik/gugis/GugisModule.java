/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
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
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Propagate.class), gugisReplicatorInterceptor);

        for (Class<?> compositeClass : ClassIndex.getAnnotated(Composite.class)) {
            Composite compositeAnnotation = compositeClass.getAnnotation(Composite.class);
            if (!compositeAnnotation.autodiscover()) {
                if (log.isDebugEnabled()) {
                    log.debug("Composite class " + compositeClass.getCanonicalName() + " has autodiscover flag set to false. Skipping.");
                }
                continue;
            }

            if (log.isDebugEnabled()) {
                log.debug("About to bind composite component " + compositeClass);
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
                .forEach(c -> {
                    if (log.isDebugEnabled()) {
                        log.debug("Binding " + c + " to " + classInterface);
                    }
                    multibinder.addBinding().to(c);
                });
    }
}

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

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.MethodUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class GugisReplicatorInterceptor implements MethodInterceptor {

    @Inject
    Injector injector;

    @Override
    public Object invoke(MethodInvocation i) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Method " + i.getMethod() + " is called on " + i.getThis() + " with args " + i.getArguments());
        }

        Replicate replicate = i.getMethod().getDeclaredAnnotation(Replicate.class);

        if (log.isDebugEnabled()) {
            log.debug("Propagation set to " + replicate.propagation());
        }

        Class clazz = i.getMethod().getDeclaringClass().getInterfaces()[0];

        List<Binding<Object>> bindings = injector.findBindingsByType(TypeLiteral.get(clazz));

        if (bindings.size() == 0) {
            log.error("No bindings found for " + clazz);
            throw new GugisException("No bindings found for " + clazz);
        }

        if (log.isDebugEnabled()) {
            log.debug("Found " + bindings.size() + " bindings for " + clazz);
        }

        List<Object> results;
        switch (replicate.propagation()) {
            case PRIMARY: {
                Stream<Binding<Object>> filtered = bindings.stream().filter(b -> b.getProvider().get().getClass().isAnnotationPresent(Primary.class));
                results = executeBindings(filtered, i.getMethod().getName(), i.getArguments());
                if (results.size() == 0) {
                    log.error("No results for primary implementation found for " + clazz);
                    throw new GugisException("No primary implementation found for " + clazz);
                }
                break;
            }
            case SECONDARY: {
                Stream<Binding<Object>> filtered = bindings.stream().filter(b -> b.getProvider().get().getClass().isAnnotationPresent(Secondary.class));
                results = executeBindings(filtered, i.getMethod().getName(), i.getArguments());
                if (results.size() == 0) {
                    log.error("No results for secondary implementation found for " + clazz);
                    throw new GugisException("No secondary implementation found for " + clazz);
                }
                break;
            }
            default: {
                // handles both ALL and ANY
                Stream<Binding<Object>> bindingStream;
                if (replicate.propagation() == Propagation.ANY) {
                    bindingStream = bindings.stream().limit(1);
                } else {
                    bindingStream = bindings.stream();
                }
                results = executeBindings(bindingStream, i.getMethod().getName(), i.getArguments());

                if (results.size() == 0) {
                    log.error("None of the bindings returned value for " + clazz);
                    throw new GugisException("None of the bindings returned value for " + clazz);
                }

                break;
            }
        }

        // all implementations should be homogeneous and should return same value for same arguments
        Object object = results.get(0);

        if (log.isDebugEnabled()) {
            log.debug("Method " + i.getMethod() + " returns " + object);
        }

        return object;
    }

    public List<Object> executeBindings(Stream<Binding<Object>> bindings, String methodName, Object[] arguments) {
        List<Object> results = bindings.parallel().map(binding -> {
            try {
                Object component = binding.getProvider().get();
                return MethodUtils.invokeMethod(component, methodName, arguments);
            } catch (Exception e) {
                throw new GugisException(e);
            }
        }).collect(Collectors.toList());
        return results;
    }
}

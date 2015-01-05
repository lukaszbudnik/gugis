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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
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

        Propagate propagate = i.getMethod().getDeclaredAnnotation(Propagate.class);

        if (log.isDebugEnabled()) {
            log.debug("Propagation set to " + propagate.propagation());
            log.debug("Allow failure set to " + propagate.allowFailure());
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

        Stream<Try<Object>> resultStream;
        switch (propagate.propagation()) {
            case FASTEST: {
                boolean allowFailure = false;
                Stream<Try<Object>> executedStream = executeBindings(allowFailure, bindings.stream(), i.getMethod().getName(), i.getArguments());
                Optional<Try<Object>> anyResult = executedStream.findAny();
                if (!anyResult.isPresent()) {
                    log.error("Fastest implementation did not return any value");
                    throw new GugisException("Fastest implementation did not return any value");
                }
                resultStream = Stream.of(anyResult.get());
                break;
            }
            case PRIMARY: {
                Stream<Binding<Object>> filtered = bindings.stream().filter(b -> b.getProvider().get().getClass().isAnnotationPresent(Primary.class));
                resultStream = executeBindings(propagate.allowFailure(), filtered, i.getMethod().getName(), i.getArguments());
                break;
            }
            case SECONDARY: {
                Stream<Binding<Object>> filtered = bindings.stream().filter(b -> b.getProvider().get().getClass().isAnnotationPresent(Secondary.class));
                resultStream = executeBindings(propagate.allowFailure(), filtered, i.getMethod().getName(), i.getArguments());
                break;
            }
            default: {
                // handles both ALL and ANY
                Stream<Binding<Object>> bindingStream;
                boolean allowFailure = propagate.allowFailure();
                if (propagate.propagation() == Propagation.ANY) {
                    bindingStream = bindings.stream().limit(1);
                    allowFailure = false;
                } else {
                    bindingStream = bindings.stream();
                }
                resultStream = executeBindings(allowFailure, bindingStream, i.getMethod().getName(), i.getArguments());
                break;
            }
        }

        List<Try<Object>> successList = resultStream.filter(t -> t.isSuccess()).collect(Collectors.toList());

        if (successList.size() == 0) {
            log.error("No result for " + propagate.propagation() + " implementation found for " + clazz);
            throw new GugisException("No result for " + propagate.propagation() + " found for " + clazz + "." + i.getMethod().getName());
        }

        // all implementations should be homogeneous and should return same value for same arguments
        Try<Object> tryObject = successList.get(0);

        if (log.isDebugEnabled()) {
            log.debug("Method " + i.getMethod() + " returns " + tryObject.get());
        }

        return tryObject.get();
    }

    public Stream<Try<Object>> executeBindings(boolean allowFailure, Stream<Binding<Object>> bindings, String methodName, Object[] arguments) {
        Stream<Try<Object>> executedBindingsStream = bindings.parallel().map(binding -> {
            try {
                Object component = binding.getProvider().get();
                return new Success<Object>(MethodUtils.invokeMethod(component, methodName, arguments));
            } catch (InvocationTargetException e) {
                if (!allowFailure) {
                    throw new GugisException(e);
                }
                return new Failure<Object>(e.getCause());
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new GugisException(e);
            }
        });
        return executedBindingsStream;
    }

}

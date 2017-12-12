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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class GugisInterceptor implements MethodInterceptor {

    @Inject
    private Injector injector;

    private Random random = new Random();

    @Override
    public Object invoke(MethodInvocation i) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Method " + i.getMethod() + " is called on " + i.getThis() + " with args " + i.getArguments());
        }

        Propagate propagate = i.getMethod().getDeclaredAnnotation(Propagate.class);
        Class clazz = i.getMethod().getDeclaringClass().getInterfaces()[0];

        if (log.isDebugEnabled()) {
            log.debug("About to find bindings for " + clazz);
            log.debug("Propagation set to " + propagate.propagation());
            log.debug("Allow failure set to " + propagate.allowFailure());
        }

        List<Binding<Object>> bindings = injector.findBindingsByType(TypeLiteral.get(clazz));

        if (bindings.size() == 0) {
            log.error("No bindings found for " + clazz);
            throw new GugisException("No bindings found for " + clazz);
        }

        if (log.isDebugEnabled()) {
            log.debug("Found " + bindings.size() + " bindings for " + clazz);
        }

        Stream<Try<Object>> resultStream = Stream.empty();
        switch (propagate.propagation()) {
            case FASTEST: {
                Stream<Try<Object>> executedStream = executeBindings(propagate.allowFailure(), bindings.stream(), i.getMethod().getName(), i.getArguments());
                Optional<Try<Object>> anyResult = executedStream.filter(t -> t.isSuccess()).findAny();
                resultStream = anyResult.isPresent() ? Stream.of(anyResult.get()) : Stream.<Try<Object>>empty();
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
            case RANDOM: {
                ArrayList<Binding<Object>> modifiableBindings = new ArrayList<>(bindings);
                Collections.shuffle(modifiableBindings);
                for (Binding<Object> binding : modifiableBindings) {
                    Stream<Try<Object>> executedStream = executeBindings(propagate.allowFailure(), Stream.of(binding), i.getMethod().getName(), i.getArguments());
                    Optional<Try<Object>> anyResult = executedStream.filter(t -> t.isSuccess()).findFirst();
                    if (anyResult.isPresent()) {
                        resultStream = Stream.of(anyResult.get());
                        break;
                    }
                }
                break;
            }
            default: {
                // handles ALL
                Stream<Binding<Object>> bindingStream = bindings.stream();
                resultStream = executeBindings(propagate.allowFailure(), bindingStream, i.getMethod().getName(), i.getArguments());
                break;
            }
        }

        List<Try<Object>> tries = resultStream.collect(Collectors.toList());

        List<Try<Object>> successes = tries.stream().filter(t -> t.isSuccess()).collect(Collectors.toList());

        if (successes.size() == 0) {
            String errorMessage = ErrorMessageBuilder.buildErrorMessageFromTries("No result for " + propagate.propagation() + " found for " + clazz.getCanonicalName() + "." + i.getMethod().getName(), tries);
            throw new GugisException(errorMessage);
        }

        // all implementations should be homogeneous and should return same value for same arguments
        Try<Object> tryObject = successes.get(0);

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
                    // pass the original exception thrown
                    throw new GugisException(e.getCause());
                }
                return new Failure<Object>(e.getCause());
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new GugisException(e);
            }
        });
        return executedBindingsStream;
    }

}

package com.github.lukaszbudnik.guice.multibindinginterceptor.ioc;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import lombok.extern.log4j.Log4j2;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.MethodUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class ReplicatorInterceptor implements MethodInterceptor {

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
            throw new RuntimeException("No bindings found for " + clazz);
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
                    throw new RuntimeException("No primary implementation found for " + clazz);
                }
                break;
            }
            case SECONDARY: {
                Stream<Binding<Object>> filtered = bindings.stream().filter(b -> b.getProvider().get().getClass().isAnnotationPresent(Secondary.class));
                results = executeBindings(filtered, i.getMethod().getName(), i.getArguments());
                if (results.size() == 0) {
                    log.error("No results for secondary implementation found for " + clazz);
                    throw new RuntimeException("No secondary implementation found for " + clazz);
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
                    throw new RuntimeException("None of the bindings returned value for " + clazz);
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
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return results;
    }
}

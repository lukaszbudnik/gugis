package io.github.lukaszbudnik.guice.multibindinginterceptor.ioc;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import lombok.extern.log4j.Log4j2;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.MethodUtils;

import javax.inject.Inject;
import java.util.List;

@Log4j2
public class ReplicatorInterceptor implements MethodInterceptor {

    @Inject
    Injector injector;

    @Override
    public Object invoke(MethodInvocation i) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Method " + i.getMethod() + " is called on " +
                    i.getThis() + " with args " + i.getArguments());
        }

        Replicate replicate = i.getMethod().getDeclaredAnnotation(Replicate.class);

        if (log.isDebugEnabled()) {
            log.debug("Propagation set to " + replicate.propagation());
        }

        Class clazz = i.getMethod().getDeclaringClass().getInterfaces()[0];

        Object result = null;
        boolean primaryFound = false;
        boolean secondaryFound = false;
        List<Binding<Object>> bindings = injector.findBindingsByType(TypeLiteral.get(clazz));

        if (log.isDebugEnabled()) {
            log.debug("Found " + bindings.size() + " bindings for " + clazz);
        }

        for (Binding binding : bindings) {
            Object component = binding.getProvider().get();
            if (replicate.propagation() == Propagation.PRIMARY_ONLY) {
                if (component.getClass().isAnnotationPresent(Primary.class)) {
                    result = MethodUtils.invokeMethod(component, i.getMethod().getName(), i.getArguments());
                    primaryFound = true;
                }
            } else if (replicate.propagation() == Propagation.SECONDARY_ONLY) {
                if (component.getClass().isAnnotationPresent(Secondary.class)) {
                    result = MethodUtils.invokeMethod(component, i.getMethod().getName(), i.getArguments());
                    secondaryFound = true;
                }
            } else {
                result = MethodUtils.invokeMethod(component, i.getMethod().getName(), i.getArguments());
            }
            if (replicate.propagation() == Propagation.ANY) {
                break;
            }
        }

        if (!primaryFound && replicate.propagation() == Propagation.PRIMARY_ONLY) {
            log.error("No primary implementation found for " + clazz);
            throw new RuntimeException("No primary implementation found for " + clazz);
        }

        if (!secondaryFound && replicate.propagation() == Propagation.SECONDARY_ONLY) {
            log.error("No secondary implementation found for " + clazz);
            throw new RuntimeException("No secondary implementation found for " + clazz);
        }

        if (log.isDebugEnabled()) {
            log.debug("method " + i.getMethod() + " returns " + result);
        }

        return result;
    }
}

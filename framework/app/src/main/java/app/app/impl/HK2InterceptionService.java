package app.app.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Singleton
public class HK2InterceptionService implements InterceptionService {
    private final Map<Class<? extends Annotation>, MethodInterceptor> interceptors = Maps.newHashMap();

    public void bind(Class<? extends Annotation> annotationClass, MethodInterceptor methodInterceptor) {
        interceptors.put(annotationClass, methodInterceptor);
    }

    @Override
    public Filter getDescriptorFilter() {
        return d -> true;
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        List<MethodInterceptor> interceptors = Lists.newArrayList();
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            MethodInterceptor interceptor = this.interceptors.get(annotation.annotationType());
            if (interceptor != null) {
                interceptors.add(interceptor);
            }
        }
        return interceptors.isEmpty() ? null : interceptors;
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
        Constructor<?> constructor) {
        return null;
    }
}

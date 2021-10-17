package app.app.impl;

import app.AnnotatedBindingBuilder;
import app.Binder;
import app.LinkedBindingBuilder;
import app.ScopedBindingBuilder;
import com.google.common.collect.Lists;
import jakarta.annotation.Priority;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.Binding;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author chi
 */
public class ModuleBinder implements Binder {
    final HK2InterceptionService interceptionService;
    private final AbstractBinder jerseyBinder;
    private final List<Object> injectionRequests = Lists.newArrayList();
    private final List<Runnable> startupHooks = Lists.newArrayList();
    private final List<Runnable> shutdownHooks = Lists.newArrayList();
    private final List<Runnable> readyHooks = Lists.newArrayList();
    private BindingBuilder<?> bindingBuilder;

    public ModuleBinder() {
        interceptionService = new HK2InterceptionService();
        jerseyBinder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(interceptionService).to(InterceptionService.class).in(Singleton.class);
            }
        };
    }

    public ModuleBinder(ModuleBinder parent) {
        interceptionService = parent.interceptionService;
        jerseyBinder = parent.jerseyBinder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> AnnotatedBindingBuilder<T> bind(Type type) {
        if (bindingBuilder != null) {
            bindingBuilder.complete();
        }
        bindingBuilder = new BindingBuilder<>(type, jerseyBinder);
        return (BindingBuilder<T>) bindingBuilder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
        if (bindingBuilder != null) {
            bindingBuilder.complete();
        }
        bindingBuilder = new BindingBuilder<>(type, jerseyBinder);
        return (BindingBuilder<T>) bindingBuilder;
    }

    @Override
    public <T> Binder requestInjection(T instance) {
        injectionRequests.add(instance);
        return this;
    }

    @Override
    public void bindInterceptor(Class<? extends Annotation> annotationClass, MethodInterceptor interceptor) {
        interceptionService.bind(annotationClass, interceptor);
    }

    @Override
    public void addShutdownHook(Runnable handler) {
        shutdownHooks.add(handler);
    }

    @Override
    public void addStartupHook(Runnable handler) {
        startupHooks.add(handler);
    }

    @Override
    public void addReadyHook(Runnable handler) {
        readyHooks.add(handler);
    }

    public AbstractBinder raw() {
        return jerseyBinder;
    }

    public List<Object> injectionRequests() {
        return injectionRequests;
    }

    public List<Runnable> startupHooks() {
        return startupHooks;
    }

    public List<Runnable> shutdownHooks() {
        return shutdownHooks;
    }

    public List<Runnable> readyHooks() {
        return readyHooks;
    }

    public void complete() {
        if (bindingBuilder != null) {
            bindingBuilder.complete();
        }
    }

    static class BindingBuilder<T> implements AnnotatedBindingBuilder<T> {
        private final Type type;
        private final List<Class<?>> alias = Lists.newArrayList();
        private final List<Annotation> qualifiers = Lists.newArrayList();
        private final AbstractBinder binder;

        private Binding<?, ?> binding;
        private Binding<?, ?> providerBinding;
        private Class<? extends Annotation> scopeAnnotation;

        BindingBuilder(Type type, AbstractBinder binder) {
            this.type = type;
            this.binder = binder;

            if (type instanceof Class) {
                Class<?> current = (Class<?>) type;
                if (current.isAnnotationPresent(Singleton.class)) {
                    in(Singleton.class);
                }
            }
        }

        @Override
        public LinkedBindingBuilder<T> annotatedWith(Annotation annotation) {
            if (binding == null) {
                qualifiers.add(annotation);
            } else {
                binding.qualifiedBy(annotation);
            }
            return this;
        }

        @Override
        public AnnotatedBindingBuilder<T> alias(Class<?> type) {
            if (binding == null) {
                alias.add(type);
            } else {
                binding.addAlias(type);
            }
            return this;
        }

        @Override
        public ScopedBindingBuilder to(Class<? extends T> implementation) {
            checkNotBound();
            binding = binder.bind(implementation).to(type);
            Integer rank = rank(implementation);
            if (rank != null) {
                binding.ranked(rank);
            }
            alias.forEach(binding::addAlias);
            qualifiers.forEach(binding::qualifiedBy);
            if (scopeAnnotation != null) {
                binding.in(scopeAnnotation);
            }
            return this;
        }

        @Override
        public ScopedBindingBuilder toInstance(T instance) {
            checkNotBound();
            binding = binder.bind(instance).to(type);
            Integer rank = rank(instance.getClass());
            if (rank != null) {
                binding.ranked(rank);
            }
            alias.forEach(binding::addAlias);
            qualifiers.forEach(binding::qualifiedBy);
            if (scopeAnnotation != null) {
                binding.in(scopeAnnotation);
            }
            return this;
        }

        @Override
        public ScopedBindingBuilder toProvider(Provider<? extends T> provider) {
            checkNotBound();
            binding = binder.bindFactory((com.google.common.base.Supplier<T>) provider::get).to(type);
            Integer rank = rank(provider.getClass());
            if (rank != null) {
                binding.ranked(rank);
            }
            alias.forEach(binding::addAlias);
            qualifiers.forEach(binding::qualifiedBy);
            if (scopeAnnotation != null) {
                binding.in(scopeAnnotation);
            }
            return this;
        }

        @Override
        public ScopedBindingBuilder toProvider(Class<? extends Provider<T>> providerClass) {
            checkNotBound();
            providerBinding = binder.bind(providerClass).to(providerClass);
            binding = binder.bindFactory(new ProviderSupplierBrideBuilder<>(providerClass).build()).to(type);
            Integer rank = rank(providerClass);
            if (rank != null) {
                binding.ranked(rank);
            }
            alias.forEach(binding::addAlias);
            qualifiers.forEach(binding::qualifiedBy);
            if (scopeAnnotation != null) {
                binding.in(scopeAnnotation);
                providerBinding.in(scopeAnnotation);
            }
            return this;
        }

        private Integer rank(Class<?> type) {
            Priority priority = type.getDeclaredAnnotation(Priority.class);
            return priority == null ? null : 10000 - priority.value();
        }

        @Override
        public void in(Class<? extends Annotation> scopeAnnotation) {
            if (binding == null) {
                this.scopeAnnotation = scopeAnnotation;
            } else {
                binding.in(scopeAnnotation);
                if (providerBinding != null) {
                    providerBinding.in(scopeAnnotation);
                }
            }
        }

        public void complete() {
            if (binding == null) {
                binding = binder.bindAsContract(type);
                alias.forEach(binding::addAlias);
                qualifiers.forEach(binding::qualifiedBy);
                if (scopeAnnotation != null) {
                    binding.in(scopeAnnotation);
                }
            }
        }

        private void checkNotBound() {
            checkState(binding == null, "invalid binding, already bound");
        }
    }
}

package app.web;

import app.resource.ResourceRepository;
import app.template.Component;
import app.template.ElementProcessor;

import jakarta.inject.Provider;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;

/**
 * @author chi
 */
public interface WebConfig {
    <T> WebConfig controller(Class<T> controllerClass);

    WebConfig bindWriterInterceptor(WriterInterceptor interceptor);

    WebConfig bindReaderInterceptor(ReaderInterceptor interceptor);

    WebConfig bindRequestFilter(ContainerRequestFilter filter);

    WebConfig bindResponseFilter(ContainerResponseFilter filter);

    <T> WebConfig bindMessageBodyWriter(MessageBodyWriter<T> messageBodyWriter);

    <T> WebConfig bindMessageBodyReader(MessageBodyReader<T> messageBodyReader);

    WebConfig bindExceptionMapper(ExceptionMapper<? extends Throwable> exceptionMapper);

    <T> WebConfig bind(Class<T> contextClass, Class<? extends Provider<T>> providerClass);

    <T> WebConfig bind(Class<T> contextClass, Provider<T> provider);

    WebConfig addComponent(Component component);

    WebConfig addRepository(ResourceRepository repository);

    WebConfig addElementProcessor(ElementProcessor processor);

    WebConfig addFunctions(String namespace, Object function);

    WebCache createCache(String name);

    WebConfig addSiteMap(Sitemap sitemap);
}

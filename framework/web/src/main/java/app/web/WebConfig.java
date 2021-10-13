package app.web;

import app.resource.ResourceRepository;
import app.template.Component;
import app.template.ElementProcessor;

import javax.inject.Provider;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

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
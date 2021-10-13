package app.test.impl;

import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author chi
 */
class MockContainerResponseWriter implements ContainerResponseWriter {
    private final RequestBuilderImpl requestBuilder;
    private final Container container;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private volatile ScheduledFuture<?> suspendTimeoutFuture;
    private volatile Runnable suspendTimeoutHandler;

    MockContainerResponseWriter(RequestBuilderImpl requestBuilder, Container container) {
        this.requestBuilder = requestBuilder;
        this.container = container;
    }

    @Override
    public OutputStream writeResponseStatusAndHeaders(long contentLength, ContainerResponse responseContext) throws ContainerException {
        requestBuilder.response = responseContext;
        return output;
    }

    @Override
    public boolean suspend(long timeOut, TimeUnit timeUnit, final ContainerResponseWriter.TimeoutHandler timeoutHandler) {
        suspendTimeoutHandler = () -> timeoutHandler.onTimeout(this);
        if (timeOut <= 0) {
            return true;
        }
        suspendTimeoutFuture = getScheduledExecutorService().schedule(suspendTimeoutHandler, timeOut, timeUnit);
        return true;
    }

    @Override
    public void setSuspendTimeout(long timeOut, TimeUnit timeUnit) throws IllegalStateException {
        if (suspendTimeoutFuture != null) {
            suspendTimeoutFuture.cancel(true);
        }

        if (timeOut <= 0) {
            return;
        }

        suspendTimeoutFuture = getScheduledExecutorService().schedule(suspendTimeoutHandler, timeOut, timeUnit);
    }

    @Override
    public void commit() {
    }

    @Override
    public void failure(Throwable e) {
        requestBuilder.error = e;
    }

    @Override
    public boolean enableResponseBuffering() {
        return false;
    }

    ScheduledExecutorService getScheduledExecutorService() {
        return container.getApplicationHandler().getInjectionManager().getInstance(ScheduledExecutorServiceProvider.class).getExecutorService();
    }
}

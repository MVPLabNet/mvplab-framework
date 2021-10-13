/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package app.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author chi
 */
public class UndertowResponseWriter implements ContainerResponseWriter {
    private final HttpServerExchange exchange;
    private final UndertowHttpContainer container;
    private volatile ScheduledFuture<?> suspendTimeoutFuture;
    private volatile Runnable suspendTimeoutHandler;
    private Throwable error;

    public UndertowResponseWriter(HttpServerExchange exchange, UndertowHttpContainer container) {
        this.exchange = exchange;
        this.container = container;
    }

    @Override
    public OutputStream writeResponseStatusAndHeaders(long contentLength, ContainerResponse responseContext) throws ContainerException {
        if (error != null) {
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/plain");

            StringWriter writer = new StringWriter();
            error.printStackTrace(new PrintWriter(writer));
            exchange.getResponseSender().send(writer.toString());
            return null;
        } else {
            exchange.setStatusCode(responseContext.getStatus());
            HeaderMap responseHeaders = exchange.getResponseHeaders();
            responseContext.getStringHeaders().forEach((key, value) -> {
                for (String v : value) {
                    responseHeaders.add(new HttpString(key), v);
                }
            });
            exchange.startBlocking();
//            exchange.setResponseContentLength(contentLength);
            return exchange.getOutputStream();
        }
    }

    @Override
    public boolean suspend(long timeOut, TimeUnit timeUnit, final ContainerResponseWriter.TimeoutHandler timeoutHandler) {
        suspendTimeoutHandler = () -> timeoutHandler.onTimeout(this);
        if (timeOut <= 0) {
            return true;
        }
        suspendTimeoutFuture = container.getScheduledExecutorService().schedule(suspendTimeoutHandler, timeOut, timeUnit);
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

        suspendTimeoutFuture = container.getScheduledExecutorService().schedule(suspendTimeoutHandler, timeOut, timeUnit);
    }

    @Override
    public void commit() {
        exchange.endExchange();
    }

    @Override
    public void failure(Throwable e) {
        this.error = e;
    }

    @Override
    public boolean enableResponseBuffering() {
        return true;
    }
}

package app.undertow;

import app.ApplicationException;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public final class RequestBodyParser implements ChannelListener<StreamSourceChannel> {
    public static final AttachmentKey<RequestBody> REQUEST_BODY = AttachmentKey.create(RequestBody.class);

    private final HttpServerExchange exchange;
    private final HttpHandler handler;
    private final ByteArrayOutputStream body;
    private boolean complete;

    public RequestBodyParser(HttpServerExchange exchange, HttpHandler handler) {
        this.exchange = exchange;
        this.handler = handler;
        int length = (int) exchange.getRequestContentLength();
        if (length < 0) body = new ByteArrayOutputStream();
        else body = new ByteArrayOutputStream(length);
    }

    @Override
    public void handleEvent(StreamSourceChannel channel) {
        read(channel);
        if (complete) {
            exchange.dispatch(handler);
        }
    }

    public void read(StreamSourceChannel channel) {
        WritableByteChannel out = Channels.newChannel(body);
        try (PooledByteBuffer poolItem = exchange.getConnection().getByteBufferPool().allocate()) {
            ByteBuffer buffer = poolItem.getBuffer();
            int bytesRead;
            while (true) {
                buffer.clear();
                bytesRead = channel.read(buffer);
                if (bytesRead <= 0) break;
                buffer.flip();
                out.write(buffer);
            }
            if (bytesRead == -1) {
                complete = true;
                exchange.putAttachment(REQUEST_BODY, new RequestBody(body.toByteArray(), null));
            }
        } catch (IOException e) {
            IoUtils.safeClose(channel);
            complete = true;
            exchange.putAttachment(REQUEST_BODY, new RequestBody(null, e));
        }
    }

    public boolean complete() {
        return complete;
    }

    public static class RequestBody {
        private final byte[] body;
        private final IOException exception;

        RequestBody(byte[] body, IOException exception) {
            this.body = body;
            this.exception = exception;
        }

        public byte[] body() {
            if (exception != null) throw new ApplicationException(exception);
            return this.body;
        }
    }
}


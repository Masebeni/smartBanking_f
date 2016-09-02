package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.io.HttpDataTransmitter;

public class HttpDataOutputStream extends OutputStream {
    private boolean closed;
    private final HttpDataTransmitter datatransmitter;

    public HttpDataOutputStream(HttpDataTransmitter datatransmitter) {
        this.closed = false;
        if (datatransmitter == null) {
            throw new IllegalArgumentException("HTTP data transmitter may not be null");
        }
        this.datatransmitter = datatransmitter;
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.datatransmitter.flush();
        }
    }

    private void assertNotClosed() {
        if (this.closed) {
            throw new IllegalStateException("Stream closed");
        }
    }

    public void flush() throws IOException {
        assertNotClosed();
        this.datatransmitter.flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        assertNotClosed();
        this.datatransmitter.write(b, off, len);
    }

    public void write(int b) throws IOException {
        assertNotClosed();
        this.datatransmitter.write(b);
    }
}

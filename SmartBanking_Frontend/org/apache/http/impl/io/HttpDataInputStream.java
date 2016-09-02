package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.io.HttpDataReceiver;

public class HttpDataInputStream extends InputStream {
    private boolean closed;
    private final HttpDataReceiver datareceiver;

    public HttpDataInputStream(HttpDataReceiver datareceiver) {
        this.closed = false;
        if (datareceiver == null) {
            throw new IllegalArgumentException("HTTP data receiver may not be null");
        }
        this.datareceiver = datareceiver;
    }

    public int available() throws IOException {
        if (this.closed || !this.datareceiver.isDataAvailable(10)) {
            return 0;
        }
        return 1;
    }

    public void close() throws IOException {
        this.closed = true;
    }

    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.datareceiver.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.datareceiver.read(b, off, len);
    }
}

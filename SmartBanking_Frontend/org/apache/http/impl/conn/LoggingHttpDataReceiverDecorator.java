package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.http.io.HttpDataReceiver;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.util.CharArrayBuffer;

class LoggingHttpDataReceiverDecorator implements HttpDataReceiver {
    private final HttpDataReceiver in;
    private final Wire wire;

    public LoggingHttpDataReceiverDecorator(HttpDataReceiver in, Wire wire) {
        this.in = in;
        this.wire = wire;
    }

    public boolean isDataAvailable(int timeout) throws IOException {
        return this.in.isDataAvailable(timeout);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int l = this.in.read(b, off, len);
        if (this.wire.enabled() && l > 0) {
            this.wire.input(b, off, l);
        }
        return l;
    }

    public int read() throws IOException {
        int l = this.in.read();
        if (this.wire.enabled() && l > 0) {
            this.wire.input(l);
        }
        return l;
    }

    public int read(byte[] b) throws IOException {
        int l = this.in.read(b);
        if (this.wire.enabled() && l > 0) {
            this.wire.input(b, 0, l);
        }
        return l;
    }

    public String readLine() throws IOException {
        String s = this.in.readLine();
        if (this.wire.enabled() && s != null) {
            this.wire.input(new StringBuffer().append(s).append("[EOL]").toString());
        }
        return s;
    }

    public int readLine(CharArrayBuffer buffer) throws IOException {
        int l = this.in.readLine(buffer);
        if (this.wire.enabled() && l > 0) {
            this.wire.input(new StringBuffer().append(new String(buffer.buffer(), buffer.length() - l, l)).append("[EOL]").toString());
        }
        return l;
    }

    public HttpTransportMetrics getMetrics() {
        return this.in.getMetrics();
    }
}
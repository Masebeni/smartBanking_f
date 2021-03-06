package org.apache.http.impl.io;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.io.HttpDataTransmitter;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

public abstract class AbstractHttpDataTransmitter implements HttpDataTransmitter {
    private static final byte[] CRLF;
    private static int MAX_CHUNK;
    private boolean ascii;
    private ByteArrayBuffer buffer;
    private String charset;
    private HttpTransportMetricsImpl metrics;
    private OutputStream outstream;

    public AbstractHttpDataTransmitter() {
        this.charset = HTTP.US_ASCII;
        this.ascii = true;
    }

    static {
        CRLF = new byte[]{(byte) 13, (byte) 10};
        MAX_CHUNK = AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
    }

    protected void init(OutputStream outstream, int buffersize, HttpParams params) {
        if (outstream == null) {
            throw new IllegalArgumentException("Input stream may not be null");
        } else if (buffersize <= 0) {
            throw new IllegalArgumentException("Buffer size may not be negative or zero");
        } else if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            this.outstream = outstream;
            this.buffer = new ByteArrayBuffer(buffersize);
            this.charset = HttpProtocolParams.getHttpElementCharset(params);
            boolean z = this.charset.equalsIgnoreCase(HTTP.US_ASCII) || this.charset.equalsIgnoreCase(HTTP.ASCII);
            this.ascii = z;
            this.metrics = new HttpTransportMetricsImpl();
        }
    }

    protected void flushBuffer() throws IOException {
        int len = this.buffer.length();
        if (len > 0) {
            this.outstream.write(this.buffer.buffer(), 0, len);
            this.buffer.clear();
            this.metrics.incrementBytesTransferred((long) len);
        }
    }

    public void flush() throws IOException {
        flushBuffer();
        this.outstream.flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (b != null) {
            if (len > MAX_CHUNK || len > this.buffer.capacity()) {
                flushBuffer();
                this.outstream.write(b, off, len);
                this.metrics.incrementBytesTransferred((long) len);
                return;
            }
            if (len > this.buffer.capacity() - this.buffer.length()) {
                flushBuffer();
            }
            this.buffer.append(b, off, len);
        }
    }

    public void write(byte[] b) throws IOException {
        if (b != null) {
            write(b, 0, b.length);
        }
    }

    public void write(int b) throws IOException {
        if (this.buffer.isFull()) {
            flushBuffer();
        }
        this.buffer.append(b);
    }

    public void writeLine(String s) throws IOException {
        if (s != null) {
            if (s.length() > 0) {
                write(s.getBytes(this.charset));
            }
            write(CRLF);
        }
    }

    public void writeLine(CharArrayBuffer s) throws IOException {
        if (s != null) {
            if (this.ascii) {
                int off = 0;
                int remaining = s.length();
                while (remaining > 0) {
                    int chunk = Math.min(this.buffer.capacity() - this.buffer.length(), remaining);
                    if (chunk > 0) {
                        this.buffer.append(s, off, chunk);
                    }
                    if (this.buffer.isFull()) {
                        flushBuffer();
                    }
                    off += chunk;
                    remaining -= chunk;
                }
            } else {
                write(s.toString().getBytes(this.charset));
            }
            write(CRLF);
        }
    }

    public HttpTransportMetrics getMetrics() {
        return this.metrics;
    }
}

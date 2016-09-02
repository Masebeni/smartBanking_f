package org.apache.http.impl.io;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.io.HttpDataReceiver;

public class ContentLengthInputStream extends InputStream {
    private static int BUFFER_SIZE;
    private boolean closed;
    private long contentLength;
    private HttpDataReceiver in;
    private long pos;

    static {
        BUFFER_SIZE = AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT;
    }

    public ContentLengthInputStream(HttpDataReceiver in, long contentLength) {
        this.pos = 0;
        this.closed = false;
        this.in = null;
        if (in == null) {
            throw new IllegalArgumentException("Input stream may not be null");
        } else if (contentLength < 0) {
            throw new IllegalArgumentException("Content length may not be negative");
        } else {
            this.in = in;
            this.contentLength = contentLength;
        }
    }

    public void close() throws IOException {
        if (!this.closed) {
            try {
                while (true) {
                    if (read(new byte[BUFFER_SIZE]) < 0) {
                        break;
                    }
                }
            } finally {
                this.closed = true;
            }
        }
    }

    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        } else if (this.pos >= this.contentLength) {
            return -1;
        } else {
            this.pos++;
            return this.in.read();
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        } else if (this.pos >= this.contentLength) {
            return -1;
        } else {
            if (this.pos + ((long) len) > this.contentLength) {
                len = (int) (this.contentLength - this.pos);
            }
            int count = this.in.read(b, off, len);
            this.pos += (long) count;
            return count;
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        long remaining = Math.min(n, this.contentLength - this.pos);
        long count = 0;
        while (remaining > 0) {
            int l = read(buffer, 0, (int) Math.min((long) BUFFER_SIZE, remaining));
            if (l == -1) {
                break;
            }
            count += (long) l;
            remaining -= (long) l;
        }
        this.pos += count;
        return count;
    }
}

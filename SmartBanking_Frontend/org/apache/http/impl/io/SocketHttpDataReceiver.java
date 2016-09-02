package org.apache.http.impl.io;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import org.apache.http.params.HttpParams;

public class SocketHttpDataReceiver extends AbstractHttpDataReceiver {
    private static final Class SOCKET_TIMEOUT_CLASS;
    private final Socket socket;

    static {
        SOCKET_TIMEOUT_CLASS = SocketTimeoutExceptionClass();
    }

    private static Class SocketTimeoutExceptionClass() {
        try {
            return Class.forName("java.net.SocketTimeoutException");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static boolean isSocketTimeoutException(InterruptedIOException e) {
        if (SOCKET_TIMEOUT_CLASS != null) {
            return SOCKET_TIMEOUT_CLASS.isInstance(e);
        }
        return true;
    }

    public SocketHttpDataReceiver(Socket socket, int buffersize, HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        this.socket = socket;
        if (buffersize < 0) {
            buffersize = socket.getReceiveBufferSize();
        }
        if (buffersize < AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
            buffersize = AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
        }
        init(socket.getInputStream(), buffersize, params);
    }

    public boolean isDataAvailable(int timeout) throws IOException {
        boolean result = hasBufferedData();
        if (!result) {
            int oldtimeout = this.socket.getSoTimeout();
            try {
                this.socket.setSoTimeout(timeout);
                fillBuffer();
                result = hasBufferedData();
            } catch (InterruptedIOException e) {
                if (!isSocketTimeoutException(e)) {
                    throw e;
                }
            } finally {
                this.socket.setSoTimeout(oldtimeout);
            }
        }
        return result;
    }
}

package org.apache.http.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import org.apache.http.HttpInetConnection;
import org.apache.http.impl.io.SocketHttpDataReceiver;
import org.apache.http.impl.io.SocketHttpDataTransmitter;
import org.apache.http.io.HttpDataReceiver;
import org.apache.http.io.HttpDataTransmitter;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class SocketHttpClientConnection extends AbstractHttpClientConnection implements HttpInetConnection {
    private volatile boolean open;
    private Socket socket;

    public SocketHttpClientConnection() {
        this.socket = null;
    }

    protected void assertNotOpen() {
        if (this.open) {
            throw new IllegalStateException("Connection is already open");
        }
    }

    protected void assertOpen() {
        if (!this.open) {
            throw new IllegalStateException("Connection is not open");
        }
    }

    protected HttpDataReceiver createHttpDataReceiver(Socket socket, int buffersize, HttpParams params) throws IOException {
        return new SocketHttpDataReceiver(socket, buffersize, params);
    }

    protected HttpDataTransmitter createHttpDataTransmitter(Socket socket, int buffersize, HttpParams params) throws IOException {
        return new SocketHttpDataTransmitter(socket, buffersize, params);
    }

    protected void bind(Socket socket, HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        } else if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            socket.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
            socket.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
            int linger = HttpConnectionParams.getLinger(params);
            if (linger >= 0) {
                socket.setSoLinger(linger > 0, linger);
            }
            this.socket = socket;
            int buffersize = HttpConnectionParams.getSocketBufferSize(params);
            init(createHttpDataReceiver(socket, buffersize, params), createHttpDataTransmitter(socket, buffersize, params), params);
            this.open = true;
        }
    }

    public boolean isOpen() {
        return this.open;
    }

    protected Socket getSocket() {
        return this.socket;
    }

    public InetAddress getLocalAddress() {
        if (this.socket != null) {
            return this.socket.getLocalAddress();
        }
        return null;
    }

    public int getLocalPort() {
        if (this.socket != null) {
            return this.socket.getLocalPort();
        }
        return -1;
    }

    public InetAddress getRemoteAddress() {
        if (this.socket != null) {
            return this.socket.getInetAddress();
        }
        return null;
    }

    public int getRemotePort() {
        if (this.socket != null) {
            return this.socket.getPort();
        }
        return -1;
    }

    public void setSocketTimeout(int timeout) {
        assertOpen();
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
            } catch (SocketException e) {
            }
        }
    }

    public int getSocketTimeout() {
        int i = -1;
        if (this.socket != null) {
            try {
                i = this.socket.getSoTimeout();
            } catch (SocketException e) {
            }
        }
        return i;
    }

    public void shutdown() throws IOException {
        this.open = false;
        Socket tmpsocket = this.socket;
        if (tmpsocket != null) {
            tmpsocket.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() throws java.io.IOException {
        /*
        r1 = this;
        r0 = r1.open;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = 0;
        r1.open = r0;
        r1.doFlush();
        r0 = r1.socket;	 Catch:{ IOException -> 0x001f, UnsupportedOperationException -> 0x001b }
        r0.shutdownOutput();	 Catch:{ IOException -> 0x001f, UnsupportedOperationException -> 0x001b }
    L_0x0010:
        r0 = r1.socket;	 Catch:{ IOException -> 0x001d, UnsupportedOperationException -> 0x001b }
        r0.shutdownInput();	 Catch:{ IOException -> 0x001d, UnsupportedOperationException -> 0x001b }
    L_0x0015:
        r0 = r1.socket;
        r0.close();
        goto L_0x0004;
    L_0x001b:
        r0 = move-exception;
        goto L_0x0015;
    L_0x001d:
        r0 = move-exception;
        goto L_0x0015;
    L_0x001f:
        r0 = move-exception;
        goto L_0x0010;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.SocketHttpClientConnection.close():void");
    }
}

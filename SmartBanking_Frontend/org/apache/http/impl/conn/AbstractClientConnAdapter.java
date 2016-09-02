package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.InetAddress;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;

public abstract class AbstractClientConnAdapter implements ManagedClientConnection {
    protected ClientConnectionManager connManager;
    protected boolean markedReusable;
    protected OperatedClientConnection wrappedConnection;

    protected AbstractClientConnAdapter(ClientConnectionManager mgr, OperatedClientConnection conn) {
        this.connManager = mgr;
        this.wrappedConnection = conn;
        this.markedReusable = false;
    }

    protected final void assertWrappedConn() {
        if (this.wrappedConnection == null) {
            throw new IllegalStateException("No wrapped connection.");
        }
    }

    public boolean isOpen() {
        if (this.wrappedConnection == null) {
            return false;
        }
        return this.wrappedConnection.isOpen();
    }

    public boolean isStale() {
        if (this.wrappedConnection == null) {
            return true;
        }
        return this.wrappedConnection.isStale();
    }

    public void setSocketTimeout(int timeout) {
        assertWrappedConn();
        this.wrappedConnection.setSocketTimeout(timeout);
    }

    public int getSocketTimeout() {
        assertWrappedConn();
        return this.wrappedConnection.getSocketTimeout();
    }

    public HttpConnectionMetrics getMetrics() {
        assertWrappedConn();
        return this.wrappedConnection.getMetrics();
    }

    public void flush() throws IOException {
        assertWrappedConn();
        this.wrappedConnection.flush();
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        assertWrappedConn();
        return this.wrappedConnection.isResponseAvailable(timeout);
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        assertWrappedConn();
        this.markedReusable = false;
        this.wrappedConnection.receiveResponseEntity(response);
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        assertWrappedConn();
        this.markedReusable = false;
        return this.wrappedConnection.receiveResponseHeader();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        assertWrappedConn();
        this.markedReusable = false;
        this.wrappedConnection.sendRequestEntity(request);
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        assertWrappedConn();
        this.markedReusable = false;
        this.wrappedConnection.sendRequestHeader(request);
    }

    public InetAddress getLocalAddress() {
        assertWrappedConn();
        return this.wrappedConnection.getLocalAddress();
    }

    public int getLocalPort() {
        assertWrappedConn();
        return this.wrappedConnection.getLocalPort();
    }

    public InetAddress getRemoteAddress() {
        assertWrappedConn();
        return this.wrappedConnection.getRemoteAddress();
    }

    public int getRemotePort() {
        assertWrappedConn();
        return this.wrappedConnection.getRemotePort();
    }

    public boolean isSecure() {
        assertWrappedConn();
        return this.wrappedConnection.isSecure();
    }

    public void markReusable() {
        this.markedReusable = true;
    }

    public void unmarkReusable() {
        this.markedReusable = false;
    }

    public boolean isMarkedReusable() {
        return this.markedReusable;
    }

    public void releaseConnection() {
        if (this.connManager != null) {
            this.connManager.releaseConnection(this);
        }
    }

    public void abortConnection() {
        unmarkReusable();
        if (this.connManager != null) {
            this.connManager.releaseConnection(this);
        }
    }
}

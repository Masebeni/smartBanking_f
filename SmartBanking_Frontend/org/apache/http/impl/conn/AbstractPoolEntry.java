package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.HttpRoute;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.RouteTracker;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public abstract class AbstractPoolEntry {
    protected OperatedClientConnection connection;
    protected RouteTracker tracker;

    protected abstract ClientConnectionOperator getOperator();

    protected AbstractPoolEntry(OperatedClientConnection occ) {
        this.connection = occ;
        this.tracker = null;
    }

    public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
        if (route == null) {
            throw new IllegalArgumentException("Route must not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else if (this.tracker == null || !this.tracker.isConnected()) {
            this.tracker = new RouteTracker(route);
            HttpHost proxy = route.getProxyHost();
            getOperator().openConnection(this.connection, proxy != null ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
            if (proxy == null) {
                this.tracker.connectTarget(this.connection.isSecure());
            } else {
                this.tracker.connectProxy(proxy, this.connection.isSecure());
            }
        } else {
            throw new IllegalStateException("Connection already open.");
        }
    }

    public void tunnelCreated(boolean secure, HttpParams params) throws IOException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else if (this.tracker == null || !this.tracker.isConnected()) {
            throw new IllegalStateException("Connection not open.");
        } else if (this.tracker.isTunnelled()) {
            throw new IllegalStateException("Connection is already tunnelled.");
        } else {
            this.connection.update(null, this.tracker.getTargetHost(), secure, params);
            this.tracker.tunnelTarget(secure);
        }
    }

    public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else if (this.tracker == null || !this.tracker.isConnected()) {
            throw new IllegalStateException("Connection not open.");
        } else if (!this.tracker.isTunnelled()) {
            throw new IllegalStateException("Protocol layering without a tunnel not supported.");
        } else if (this.tracker.isLayered()) {
            throw new IllegalStateException("Multiple protocol layering not supported.");
        } else {
            getOperator().updateSecureConnection(this.connection, this.tracker.getTargetHost(), context, params);
            this.tracker.layerProtocol(this.connection.isSecure());
        }
    }

    public void closing() {
        this.tracker = null;
    }
}

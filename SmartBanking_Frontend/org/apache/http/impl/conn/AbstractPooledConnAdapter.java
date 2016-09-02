package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpRoute;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public abstract class AbstractPooledConnAdapter extends AbstractClientConnAdapter implements ManagedClientConnection {
    protected AbstractPoolEntry poolEntry;

    protected AbstractPooledConnAdapter(ClientConnectionManager manager, AbstractPoolEntry entry) {
        super(manager, entry.connection);
        this.poolEntry = entry;
    }

    protected final void assertAttached() {
        if (this.poolEntry == null) {
            throw new IllegalStateException("Adapter is detached.");
        }
    }

    protected void detach() {
        this.wrappedConnection = null;
        this.poolEntry = null;
        this.connManager = null;
    }

    public HttpRoute getRoute() {
        assertAttached();
        return this.poolEntry.tracker == null ? null : this.poolEntry.tracker.toRoute();
    }

    public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
        assertAttached();
        this.poolEntry.open(route, context, params);
    }

    public void tunnelCreated(boolean secure, HttpParams params) throws IOException {
        assertAttached();
        this.poolEntry.tunnelCreated(secure, params);
    }

    public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
        assertAttached();
        this.poolEntry.layerProtocol(context, params);
    }

    public void close() throws IOException {
        if (this.poolEntry != null) {
            this.poolEntry.closing();
        }
        if (this.wrappedConnection != null) {
            this.wrappedConnection.close();
        }
    }

    public void shutdown() throws IOException {
        if (this.poolEntry != null) {
            this.poolEntry.closing();
        }
        if (this.wrappedConnection != null) {
            this.wrappedConnection.shutdown();
        }
    }
}

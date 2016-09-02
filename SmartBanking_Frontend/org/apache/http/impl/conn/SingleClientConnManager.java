package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.HttpRoute;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.SchemeRegistry;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class SingleClientConnManager implements ClientConnectionManager {
    private static final Log LOG;
    public static final String MISUSE_MESSAGE = "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
    static Class class$org$apache$http$impl$conn$SingleClientConnManager;
    protected boolean alwaysShutDown;
    protected ClientConnectionOperator connOperator;
    protected volatile boolean isShutDown;
    protected long lastReleaseTime;
    protected ConnAdapter managedConn;
    protected HttpParams params;
    protected SchemeRegistry schemeRegistry;
    protected PoolEntry uniquePoolEntry;

    protected class PoolEntry extends AbstractPoolEntry {
        protected HttpRoute plannedRoute;
        private final SingleClientConnManager this$0;

        protected PoolEntry(SingleClientConnManager singleClientConnManager, OperatedClientConnection occ) {
            this.this$0 = singleClientConnManager;
            super(occ);
        }

        protected ClientConnectionOperator getOperator() {
            return this.this$0.connOperator;
        }

        protected void close() throws IOException {
            closing();
            if (this.connection.isOpen()) {
                this.connection.close();
            }
        }

        protected void shutdown() throws IOException {
            closing();
            if (this.connection.isOpen()) {
                this.connection.shutdown();
            }
        }
    }

    protected class ConnAdapter extends AbstractPooledConnAdapter {
        private final SingleClientConnManager this$0;

        protected ConnAdapter(SingleClientConnManager singleClientConnManager, PoolEntry entry, HttpRoute plan) {
            this.this$0 = singleClientConnManager;
            super(singleClientConnManager, entry);
            this.markedReusable = true;
            entry.plannedRoute = plan;
        }
    }

    static {
        Class class$;
        if (class$org$apache$http$impl$conn$SingleClientConnManager == null) {
            class$ = class$("org.apache.http.impl.conn.SingleClientConnManager");
            class$org$apache$http$impl$conn$SingleClientConnManager = class$;
        } else {
            class$ = class$org$apache$http$impl$conn$SingleClientConnManager;
        }
        LOG = LogFactory.getLog(class$);
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public SingleClientConnManager(HttpParams params, SchemeRegistry schreg) {
        this.params = new BasicHttpParams();
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else if (schreg == null) {
            throw new IllegalArgumentException("Scheme registry must not be null.");
        } else {
            this.params = params;
            this.schemeRegistry = schreg;
            this.connOperator = createConnectionOperator(schreg);
            this.uniquePoolEntry = new PoolEntry(this, this.connOperator.createConnection());
            this.managedConn = null;
            this.lastReleaseTime = -1;
            this.alwaysShutDown = false;
            this.isShutDown = false;
        }
    }

    public SchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }

    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg);
    }

    protected final void assertStillUp() throws IllegalStateException {
        if (this.isShutDown) {
            throw new IllegalStateException("Manager is shut down.");
        }
    }

    public final ManagedClientConnection getConnection(HttpRoute route, long timeout) {
        return getConnection(route);
    }

    public ManagedClientConnection getConnection(HttpRoute route) {
        if (route == null) {
            throw new IllegalArgumentException("Route may not be null.");
        }
        assertStillUp();
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("SingleClientConnManager.getConnection: ").append(route).toString());
        }
        if (this.managedConn != null) {
            revokeConnection();
        }
        if (this.uniquePoolEntry.connection.isOpen()) {
            boolean shutdown = this.uniquePoolEntry.tracker == null || !this.uniquePoolEntry.tracker.toRoute().equals(route);
            if (shutdown) {
                try {
                    this.uniquePoolEntry.shutdown();
                } catch (IOException iox) {
                    LOG.debug("Problem shutting down connection.", iox);
                    this.uniquePoolEntry = new PoolEntry(this, this.connOperator.createConnection());
                }
            }
        }
        this.managedConn = new ConnAdapter(this, this.uniquePoolEntry, route);
        return this.managedConn;
    }

    public void releaseConnection(ManagedClientConnection conn) {
        assertStillUp();
        if (conn instanceof ConnAdapter) {
            ConnAdapter sca = (ConnAdapter) conn;
            if (sca.connManager != this) {
                throw new IllegalArgumentException("Connection not obtained from this manager.");
            } else if (sca.poolEntry != null) {
                try {
                    if (sca.isOpen() && (this.alwaysShutDown || !sca.isMarkedReusable())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Released connection open but not reusable.");
                        }
                        sca.shutdown();
                    }
                    sca.detach();
                    this.managedConn = null;
                    this.lastReleaseTime = System.currentTimeMillis();
                    return;
                } catch (IOException iox) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Exception shutting down released connection.", iox);
                    }
                    sca.detach();
                    this.managedConn = null;
                    this.lastReleaseTime = System.currentTimeMillis();
                    return;
                } catch (Throwable th) {
                    sca.detach();
                    this.managedConn = null;
                    this.lastReleaseTime = System.currentTimeMillis();
                }
            } else {
                return;
            }
        }
        throw new IllegalArgumentException("Connection class mismatch, connection not obtained from this manager.");
    }

    public void closeIdleConnections(long idletime) {
        assertStillUp();
        if (this.managedConn == null && this.uniquePoolEntry.connection.isOpen()) {
            if (this.lastReleaseTime <= System.currentTimeMillis() - idletime) {
                try {
                    this.uniquePoolEntry.close();
                } catch (IOException iox) {
                    LOG.debug("Problem closing idle connection.", iox);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void shutdown() {
        /*
        r4 = this;
        r3 = 0;
        r1 = 1;
        r4.isShutDown = r1;
        r1 = r4.managedConn;
        if (r1 == 0) goto L_0x000d;
    L_0x0008:
        r1 = r4.managedConn;
        r1.detach();
    L_0x000d:
        r1 = r4.uniquePoolEntry;	 Catch:{ IOException -> 0x0019 }
        if (r1 == 0) goto L_0x0016;
    L_0x0011:
        r1 = r4.uniquePoolEntry;	 Catch:{ IOException -> 0x0019 }
        r1.shutdown();	 Catch:{ IOException -> 0x0019 }
    L_0x0016:
        r4.uniquePoolEntry = r3;
    L_0x0018:
        return;
    L_0x0019:
        r0 = move-exception;
        r1 = LOG;	 Catch:{ all -> 0x0024 }
        r2 = "Problem while shutting down manager.";
        r1.debug(r2, r0);	 Catch:{ all -> 0x0024 }
        r4.uniquePoolEntry = r3;
        goto L_0x0018;
    L_0x0024:
        r1 = move-exception;
        r4.uniquePoolEntry = r3;
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.SingleClientConnManager.shutdown():void");
    }

    protected void revokeConnection() {
        if (this.managedConn != null) {
            LOG.warn(MISUSE_MESSAGE, new IllegalStateException(new StringBuffer().append("Revoking connection to ").append(this.managedConn.getRoute()).toString()));
            if (this.managedConn != null) {
                this.managedConn.detach();
            }
            try {
                this.uniquePoolEntry.shutdown();
            } catch (IOException iox) {
                LOG.debug("Problem while shutting down connection.", iox);
            }
        }
    }
}

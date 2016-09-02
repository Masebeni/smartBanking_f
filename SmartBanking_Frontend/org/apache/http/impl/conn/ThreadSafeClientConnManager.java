package org.apache.http.impl.conn;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HostConfiguration;
import org.apache.http.conn.HttpRoute;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.SchemeRegistry;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class ThreadSafeClientConnManager implements ClientConnectionManager {
    private static WeakHashMap ALL_CONNECTION_MANAGERS;
    private static final Log LOG;
    private static final ReferenceQueue REFERENCE_QUEUE;
    private static ReferenceQueueThread REFERENCE_QUEUE_THREAD;
    private static final Map REFERENCE_TO_CONNECTION_SOURCE;
    static Class class$org$apache$http$impl$conn$ThreadSafeClientConnManager;
    private ClientConnectionOperator connOperator;
    private ConnectionPool connectionPool;
    private volatile boolean isShutDown;
    private HttpParams params;
    protected SchemeRegistry schemeRegistry;

    /* renamed from: org.apache.http.impl.conn.ThreadSafeClientConnManager.1 */
    static class C01651 {
    }

    private class ConnectionPool {
        private LinkedList freeConnections;
        private IdleConnectionHandler idleConnectionHandler;
        private final Map mapHosts;
        private int numConnections;
        private final ThreadSafeClientConnManager this$0;
        private LinkedList waitingThreads;

        private ConnectionPool(ThreadSafeClientConnManager threadSafeClientConnManager) {
            this.this$0 = threadSafeClientConnManager;
            this.freeConnections = new LinkedList();
            this.waitingThreads = new LinkedList();
            this.mapHosts = new HashMap();
            this.idleConnectionHandler = new IdleConnectionHandler();
            this.numConnections = 0;
        }

        ConnectionPool(ThreadSafeClientConnManager x0, C01651 x1) {
            this(x0);
        }

        static int access$100(ConnectionPool x0) {
            return x0.numConnections;
        }

        static LinkedList access$200(ConnectionPool x0) {
            return x0.freeConnections;
        }

        static LinkedList access$400(ConnectionPool x0) {
            return x0.waitingThreads;
        }

        static void access$500(ConnectionPool x0, TrackingPoolEntry x1) {
            x0.freeConnection(x1);
        }

        public synchronized void shutdown() {
            Iterator iter = this.freeConnections.iterator();
            while (iter.hasNext()) {
                TrackingPoolEntry entry = (TrackingPoolEntry) iter.next();
                iter.remove();
                ThreadSafeClientConnManager.access$900(entry.connection);
            }
            ThreadSafeClientConnManager.access$1000(this);
            iter = this.waitingThreads.iterator();
            while (iter.hasNext()) {
                WaitingThread waiter = (WaitingThread) iter.next();
                iter.remove();
                waiter.interruptedByConnectionPool = true;
                waiter.thread.interrupt();
            }
            this.mapHosts.clear();
            this.idleConnectionHandler.removeAll();
        }

        protected synchronized TrackingPoolEntry createEntry(HostConfiguration route, OperatedClientConnection conn) {
            TrackingPoolEntry entry;
            HostConnectionPool hostPool = getHostPool(route);
            if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                ThreadSafeClientConnManager.access$1100().debug(new StringBuffer().append("Allocating new connection, hostConfiguration=").append(route).toString());
            }
            entry = new TrackingPoolEntry(this.this$0, conn, null);
            TrackingPoolEntry.access$1302(entry, route);
            this.numConnections++;
            hostPool.numConnections++;
            ThreadSafeClientConnManager.access$1400(entry, route, this);
            return entry;
        }

        public synchronized void handleLostConnection(HostConfiguration config) {
            HostConnectionPool hostPool = getHostPool(config);
            hostPool.numConnections--;
            if (hostPool.numConnections < 1) {
                this.mapHosts.remove(config);
            }
            this.numConnections--;
            notifyWaitingThread(config);
        }

        public synchronized HostConnectionPool getHostPool(HostConfiguration route) {
            HostConnectionPool listConnections;
            listConnections = (HostConnectionPool) this.mapHosts.get(route);
            if (listConnections == null) {
                listConnections = new HostConnectionPool(null);
                listConnections.hostConfiguration = route;
                this.mapHosts.put(route, listConnections);
            }
            return listConnections;
        }

        public synchronized TrackingPoolEntry getFreeConnection(HostConfiguration hostConfiguration) {
            TrackingPoolEntry entry;
            entry = null;
            HostConnectionPool hostPool = getHostPool(hostConfiguration);
            if (hostPool.freeConnections.size() > 0) {
                entry = (TrackingPoolEntry) hostPool.freeConnections.removeLast();
                this.freeConnections.remove(entry);
                ThreadSafeClientConnManager.access$1400(entry, hostConfiguration, this);
                if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                    ThreadSafeClientConnManager.access$1100().debug(new StringBuffer().append("Getting free connection, hostConfig=").append(hostConfiguration).toString());
                }
                this.idleConnectionHandler.remove(entry.connection);
            } else if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                ThreadSafeClientConnManager.access$1100().debug(new StringBuffer().append("There were no free connections to get, hostConfig=").append(hostConfiguration).toString());
            }
            return entry;
        }

        public synchronized void deleteClosedConnections() {
            Iterator iter = this.freeConnections.iterator();
            while (iter.hasNext()) {
                TrackingPoolEntry entry = (TrackingPoolEntry) iter.next();
                if (!entry.connection.isOpen()) {
                    iter.remove();
                    deleteConnection(entry);
                }
            }
        }

        public synchronized void closeIdleConnections(long idleTimeout) {
            this.idleConnectionHandler.closeIdleConnections(idleTimeout);
        }

        private synchronized void deleteConnection(TrackingPoolEntry entry) {
            HostConfiguration route = TrackingPoolEntry.access$1300(entry);
            if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                ThreadSafeClientConnManager.access$1100().debug(new StringBuffer().append("Reclaiming connection, hostConfig=").append(route).toString());
            }
            ThreadSafeClientConnManager.access$900(entry.connection);
            HostConnectionPool hostPool = getHostPool(route);
            hostPool.freeConnections.remove(entry);
            hostPool.numConnections--;
            this.numConnections--;
            if (hostPool.numConnections < 1) {
                this.mapHosts.remove(route);
            }
            this.idleConnectionHandler.remove(entry.connection);
        }

        public synchronized void deleteLeastUsedConnection() {
            TrackingPoolEntry entry = (TrackingPoolEntry) this.freeConnections.removeFirst();
            if (entry != null) {
                deleteConnection(entry);
            } else if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                ThreadSafeClientConnManager.access$1100().debug("Attempted to reclaim an unused connection but there were none.");
            }
        }

        public synchronized void notifyWaitingThread(HostConfiguration configuration) {
            notifyWaitingThread(getHostPool(configuration));
        }

        public synchronized void notifyWaitingThread(HostConnectionPool hostPool) {
            WaitingThread waitingThread = null;
            if (hostPool.waitingThreads.size() > 0) {
                if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                    ThreadSafeClientConnManager.access$1100().debug(new StringBuffer().append("Notifying thread waiting on host pool, hostConfig=").append(hostPool.hostConfiguration).toString());
                }
                waitingThread = (WaitingThread) hostPool.waitingThreads.removeFirst();
                this.waitingThreads.remove(waitingThread);
            } else if (this.waitingThreads.size() > 0) {
                if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                    ThreadSafeClientConnManager.access$1100().debug("No-one waiting on host pool, notifying next waiting thread.");
                }
                waitingThread = (WaitingThread) this.waitingThreads.removeFirst();
                waitingThread.hostConnectionPool.waitingThreads.remove(waitingThread);
            } else if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                ThreadSafeClientConnManager.access$1100().debug("Notifying no-one, there are no waiting threads");
            }
            if (waitingThread != null) {
                waitingThread.interruptedByConnectionPool = true;
                waitingThread.thread.interrupt();
            }
        }

        private void freeConnection(TrackingPoolEntry entry) {
            HostConfiguration route = TrackingPoolEntry.access$1300(entry);
            if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                ThreadSafeClientConnManager.access$1100().debug(new StringBuffer().append("Freeing connection, hostConfig=").append(route).toString());
            }
            synchronized (this) {
                if (ThreadSafeClientConnManager.access$1600(this.this$0)) {
                    ThreadSafeClientConnManager.access$900(entry.connection);
                    return;
                }
                HostConnectionPool hostPool = getHostPool(route);
                hostPool.freeConnections.add(entry);
                if (hostPool.numConnections == 0) {
                    ThreadSafeClientConnManager.access$1100().error(new StringBuffer().append("Host connection pool not found, hostConfig=").append(route).toString());
                    hostPool.numConnections = 1;
                }
                this.freeConnections.add(entry);
                ThreadSafeClientConnManager.access$1700(entry);
                if (this.numConnections == 0) {
                    ThreadSafeClientConnManager.access$1100().error(new StringBuffer().append("Host connection pool not found, hostConfig=").append(route).toString());
                    this.numConnections = 1;
                }
                this.idleConnectionHandler.add(entry.connection);
                notifyWaitingThread(hostPool);
            }
        }
    }

    private static class ConnectionSource {
        public ConnectionPool connectionPool;
        public HostConfiguration hostConfiguration;

        private ConnectionSource() {
        }

        ConnectionSource(C01651 x0) {
            this();
        }
    }

    private static class HostConnectionPool {
        public LinkedList freeConnections;
        public HostConfiguration hostConfiguration;
        public int numConnections;
        public LinkedList waitingThreads;

        private HostConnectionPool() {
            this.freeConnections = new LinkedList();
            this.waitingThreads = new LinkedList();
            this.numConnections = 0;
        }

        HostConnectionPool(C01651 x0) {
            this();
        }
    }

    private static class ReferenceQueueThread extends Thread {
        private volatile boolean isShutDown;

        public ReferenceQueueThread() {
            this.isShutDown = false;
            setDaemon(true);
            setName("ThreadSafeClientConnManager cleanup");
        }

        public void shutdown() {
            this.isShutDown = true;
            interrupt();
        }

        private void handleReference(Reference ref) {
            synchronized (ThreadSafeClientConnManager.access$1800()) {
                ConnectionSource source = (ConnectionSource) ThreadSafeClientConnManager.access$1800().remove(ref);
            }
            if (source != null) {
                if (ThreadSafeClientConnManager.access$1100().isDebugEnabled()) {
                    ThreadSafeClientConnManager.access$1100().debug(new StringBuffer().append("Connection reclaimed by garbage collector, hostConfig=").append(source.hostConfiguration).toString());
                }
                source.connectionPool.handleLostConnection(source.hostConfiguration);
            }
        }

        public void run() {
            while (!this.isShutDown) {
                try {
                    Reference ref = ThreadSafeClientConnManager.access$1900().remove();
                    if (ref != null) {
                        handleReference(ref);
                    }
                } catch (InterruptedException e) {
                    ThreadSafeClientConnManager.access$1100().debug("ReferenceQueueThread interrupted", e);
                }
            }
        }
    }

    private static class WaitingThread {
        public HostConnectionPool hostConnectionPool;
        public boolean interruptedByConnectionPool;
        public Thread thread;

        private WaitingThread() {
            this.interruptedByConnectionPool = false;
        }

        WaitingThread(C01651 x0) {
            this();
        }
    }

    private class TrackingPoolEntry extends AbstractPoolEntry {
        private ThreadSafeClientConnManager manager;
        private HostConfiguration plannedRoute;
        private WeakReference reference;
        private final ThreadSafeClientConnManager this$0;

        TrackingPoolEntry(ThreadSafeClientConnManager x0, OperatedClientConnection x1, C01651 x2) {
            this(x0, x1);
        }

        static HostConfiguration access$1300(TrackingPoolEntry x0) {
            return x0.plannedRoute;
        }

        static HostConfiguration access$1302(TrackingPoolEntry x0, HostConfiguration x1) {
            x0.plannedRoute = x1;
            return x1;
        }

        static WeakReference access$700(TrackingPoolEntry x0) {
            return x0.reference;
        }

        static ThreadSafeClientConnManager access$800(TrackingPoolEntry x0) {
            return x0.manager;
        }

        private TrackingPoolEntry(ThreadSafeClientConnManager threadSafeClientConnManager, OperatedClientConnection occ) {
            this.this$0 = threadSafeClientConnManager;
            super(occ);
            this.manager = threadSafeClientConnManager;
            this.reference = new WeakReference(this, ThreadSafeClientConnManager.access$1900());
        }

        protected ClientConnectionOperator getOperator() {
            return ThreadSafeClientConnManager.access$2000(this.this$0);
        }
    }

    private class HttpConnectionAdapter extends AbstractPooledConnAdapter {
        private final ThreadSafeClientConnManager this$0;

        protected HttpConnectionAdapter(ThreadSafeClientConnManager threadSafeClientConnManager, TrackingPoolEntry entry) {
            this.this$0 = threadSafeClientConnManager;
            super(threadSafeClientConnManager, entry);
            this.markedReusable = true;
        }
    }

    static void access$1000(ConnectionPool x0) {
        shutdownCheckedOutConnections(x0);
    }

    static Log access$1100() {
        return LOG;
    }

    static void access$1400(TrackingPoolEntry x0, HostConfiguration x1, ConnectionPool x2) {
        storeReferenceToConnection(x0, x1, x2);
    }

    static boolean access$1600(ThreadSafeClientConnManager x0) {
        return x0.isShutDown;
    }

    static void access$1700(TrackingPoolEntry x0) {
        removeReferenceToConnection(x0);
    }

    static Map access$1800() {
        return REFERENCE_TO_CONNECTION_SOURCE;
    }

    static ReferenceQueue access$1900() {
        return REFERENCE_QUEUE;
    }

    static ClientConnectionOperator access$2000(ThreadSafeClientConnManager x0) {
        return x0.connOperator;
    }

    static void access$900(OperatedClientConnection x0) {
        closeConnection(x0);
    }

    static {
        Class class$;
        if (class$org$apache$http$impl$conn$ThreadSafeClientConnManager == null) {
            class$ = class$("org.apache.http.impl.conn.ThreadSafeClientConnManager");
            class$org$apache$http$impl$conn$ThreadSafeClientConnManager = class$;
        } else {
            class$ = class$org$apache$http$impl$conn$ThreadSafeClientConnManager;
        }
        LOG = LogFactory.getLog(class$);
        REFERENCE_TO_CONNECTION_SOURCE = new HashMap();
        REFERENCE_QUEUE = new ReferenceQueue();
        ALL_CONNECTION_MANAGERS = new WeakHashMap();
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public ThreadSafeClientConnManager(HttpParams params, SchemeRegistry schreg) {
        this.params = new BasicHttpParams();
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        this.params = params;
        this.schemeRegistry = schreg;
        this.connectionPool = new ConnectionPool(this, null);
        this.connOperator = createConnectionOperator(schreg);
        this.isShutDown = false;
        synchronized (ALL_CONNECTION_MANAGERS) {
            ALL_CONNECTION_MANAGERS.put(this, null);
        }
    }

    public SchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }

    public ManagedClientConnection getConnection(HttpRoute route) {
        while (true) {
            try {
                break;
            } catch (ConnectionPoolTimeoutException e) {
                LOG.debug("Unexpected exception while waiting for connection", e);
            }
        }
        return getConnection(route, 0);
    }

    public ManagedClientConnection getConnection(HttpRoute route, long timeout) throws ConnectionPoolTimeoutException {
        if (route == null) {
            throw new IllegalArgumentException("Route may not be null.");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("ThreadSafeClientConnManager.getConnection: ").append(route).append(", timeout = ").append(timeout).toString());
        }
        return new HttpConnectionAdapter(this, doGetConnection(route.toHostConfig(), timeout));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.http.impl.conn.ThreadSafeClientConnManager.TrackingPoolEntry doGetConnection(org.apache.http.conn.HostConfiguration r21, long r22) throws org.apache.http.conn.ConnectionPoolTimeoutException {
        /*
        r20 = this;
        r3 = 0;
        r0 = r20;
        r0 = r0.params;
        r16 = r0;
        r0 = r16;
        r1 = r21;
        r7 = org.apache.http.conn.params.HttpConnectionManagerParams.getMaxConnectionsPerHost(r0, r1);
        r0 = r20;
        r0 = r0.params;
        r16 = r0;
        r8 = org.apache.http.conn.params.HttpConnectionManagerParams.getMaxTotalConnections(r16);
        r0 = r20;
        r0 = r0.connectionPool;
        r17 = r0;
        monitor-enter(r17);
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r0 = r16;
        r1 = r21;
        r6 = r0.getHostPool(r1);	 Catch:{ all -> 0x0053 }
        r14 = 0;
        r18 = 0;
        r16 = (r22 > r18 ? 1 : (r22 == r18 ? 0 : -1));
        if (r16 <= 0) goto L_0x0056;
    L_0x0035:
        r9 = 1;
    L_0x0036:
        r12 = r22;
        r10 = 0;
        r4 = 0;
        r15 = r14;
    L_0x003d:
        if (r3 != 0) goto L_0x01d1;
    L_0x003f:
        r0 = r20;
        r0 = r0.isShutDown;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        if (r16 == 0) goto L_0x0058;
    L_0x0047:
        r16 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0053 }
        r18 = "Connection manager has been shut down.";
        r0 = r16;
        r1 = r18;
        r0.<init>(r1);	 Catch:{ all -> 0x0053 }
        throw r16;	 Catch:{ all -> 0x0053 }
    L_0x0053:
        r16 = move-exception;
        monitor-exit(r17);	 Catch:{ all -> 0x0053 }
        throw r16;
    L_0x0056:
        r9 = 0;
        goto L_0x0036;
    L_0x0058:
        r0 = r6.freeConnections;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r16 = r16.size();	 Catch:{ all -> 0x0053 }
        if (r16 <= 0) goto L_0x0071;
    L_0x0062:
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r0 = r16;
        r1 = r21;
        r3 = r0.getFreeConnection(r1);	 Catch:{ all -> 0x0053 }
        goto L_0x003d;
    L_0x0071:
        r0 = r6.numConnections;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r0 = r16;
        if (r0 >= r7) goto L_0x008c;
    L_0x0079:
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r16 = org.apache.http.impl.conn.ThreadSafeClientConnManager.ConnectionPool.access$100(r16);	 Catch:{ all -> 0x0053 }
        r0 = r16;
        if (r0 >= r8) goto L_0x008c;
    L_0x0087:
        r3 = r20.createPoolEntry(r21);	 Catch:{ all -> 0x0053 }
        goto L_0x003d;
    L_0x008c:
        r0 = r6.numConnections;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r0 = r16;
        if (r0 >= r7) goto L_0x00b2;
    L_0x0094:
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r16 = org.apache.http.impl.conn.ThreadSafeClientConnManager.ConnectionPool.access$200(r16);	 Catch:{ all -> 0x0053 }
        r16 = r16.size();	 Catch:{ all -> 0x0053 }
        if (r16 <= 0) goto L_0x00b2;
    L_0x00a4:
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r16.deleteLeastUsedConnection();	 Catch:{ all -> 0x0053 }
        r3 = r20.createPoolEntry(r21);	 Catch:{ all -> 0x0053 }
        goto L_0x003d;
    L_0x00b2:
        if (r9 == 0) goto L_0x010f;
    L_0x00b4:
        r18 = 0;
        r16 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1));
        if (r16 > 0) goto L_0x010f;
    L_0x00ba:
        r16 = new org.apache.http.conn.ConnectionPoolTimeoutException;	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r18 = "Timeout waiting for connection";
        r0 = r16;
        r1 = r18;
        r0.<init>(r1);	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        throw r16;	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
    L_0x00c6:
        r2 = move-exception;
        r14 = r15;
    L_0x00c8:
        r0 = r14.interruptedByConnectionPool;	 Catch:{ all -> 0x00e5 }
        r16 = r0;
        if (r16 != 0) goto L_0x01a8;
    L_0x00ce:
        r16 = LOG;	 Catch:{ all -> 0x00e5 }
        r18 = "Interrupted while waiting for connection";
        r0 = r16;
        r1 = r18;
        r0.debug(r1, r2);	 Catch:{ all -> 0x00e5 }
        r16 = new java.lang.IllegalThreadStateException;	 Catch:{ all -> 0x00e5 }
        r18 = "Interrupted while waiting in ThreadSafeClientConnManager";
        r0 = r16;
        r1 = r18;
        r0.<init>(r1);	 Catch:{ all -> 0x00e5 }
        throw r16;	 Catch:{ all -> 0x00e5 }
    L_0x00e5:
        r16 = move-exception;
    L_0x00e6:
        r0 = r14.interruptedByConnectionPool;	 Catch:{ all -> 0x0053 }
        r18 = r0;
        if (r18 != 0) goto L_0x0104;
    L_0x00ec:
        r0 = r6.waitingThreads;	 Catch:{ all -> 0x0053 }
        r18 = r0;
        r0 = r18;
        r0.remove(r14);	 Catch:{ all -> 0x0053 }
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r18 = r0;
        r18 = org.apache.http.impl.conn.ThreadSafeClientConnManager.ConnectionPool.access$400(r18);	 Catch:{ all -> 0x0053 }
        r0 = r18;
        r0.remove(r14);	 Catch:{ all -> 0x0053 }
    L_0x0104:
        if (r9 == 0) goto L_0x010e;
    L_0x0106:
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0053 }
        r18 = r4 - r10;
        r12 = r12 - r18;
    L_0x010e:
        throw r16;	 Catch:{ all -> 0x0053 }
    L_0x010f:
        r16 = LOG;	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r16 = r16.isDebugEnabled();	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        if (r16 == 0) goto L_0x0137;
    L_0x0117:
        r16 = LOG;	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r18 = new java.lang.StringBuffer;	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r18.<init>();	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r19 = "Unable to get a connection, waiting..., hostConfig=";
        r18 = r18.append(r19);	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r0 = r18;
        r1 = r21;
        r18 = r0.append(r1);	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r18 = r18.toString();	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r0 = r16;
        r1 = r18;
        r0.debug(r1);	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
    L_0x0137:
        if (r15 != 0) goto L_0x01a0;
    L_0x0139:
        r14 = new org.apache.http.impl.conn.ThreadSafeClientConnManager$WaitingThread;	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r16 = 0;
        r0 = r16;
        r14.<init>(r0);	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r14.hostConnectionPool = r6;	 Catch:{ InterruptedException -> 0x01d7 }
        r16 = java.lang.Thread.currentThread();	 Catch:{ InterruptedException -> 0x01d7 }
        r0 = r16;
        r14.thread = r0;	 Catch:{ InterruptedException -> 0x01d7 }
    L_0x014c:
        if (r9 == 0) goto L_0x0152;
    L_0x014e:
        r10 = java.lang.System.currentTimeMillis();	 Catch:{ InterruptedException -> 0x01d7 }
    L_0x0152:
        r0 = r6.waitingThreads;	 Catch:{ InterruptedException -> 0x01d7 }
        r16 = r0;
        r0 = r16;
        r0.addLast(r14);	 Catch:{ InterruptedException -> 0x01d7 }
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ InterruptedException -> 0x01d7 }
        r16 = r0;
        r16 = org.apache.http.impl.conn.ThreadSafeClientConnManager.ConnectionPool.access$400(r16);	 Catch:{ InterruptedException -> 0x01d7 }
        r0 = r16;
        r0.addLast(r14);	 Catch:{ InterruptedException -> 0x01d7 }
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ InterruptedException -> 0x01d7 }
        r16 = r0;
        r0 = r16;
        r0.wait(r12);	 Catch:{ InterruptedException -> 0x01d7 }
        r0 = r14.interruptedByConnectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        if (r16 != 0) goto L_0x0193;
    L_0x017b:
        r0 = r6.waitingThreads;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r0 = r16;
        r0.remove(r14);	 Catch:{ all -> 0x0053 }
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r16 = org.apache.http.impl.conn.ThreadSafeClientConnManager.ConnectionPool.access$400(r16);	 Catch:{ all -> 0x0053 }
        r0 = r16;
        r0.remove(r14);	 Catch:{ all -> 0x0053 }
    L_0x0193:
        if (r9 == 0) goto L_0x019d;
    L_0x0195:
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0053 }
        r18 = r4 - r10;
        r12 = r12 - r18;
    L_0x019d:
        r15 = r14;
        goto L_0x003d;
    L_0x01a0:
        r16 = 0;
        r0 = r16;
        r15.interruptedByConnectionPool = r0;	 Catch:{ InterruptedException -> 0x00c6, all -> 0x01d3 }
        r14 = r15;
        goto L_0x014c;
    L_0x01a8:
        r0 = r14.interruptedByConnectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        if (r16 != 0) goto L_0x01c6;
    L_0x01ae:
        r0 = r6.waitingThreads;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r0 = r16;
        r0.remove(r14);	 Catch:{ all -> 0x0053 }
        r0 = r20;
        r0 = r0.connectionPool;	 Catch:{ all -> 0x0053 }
        r16 = r0;
        r16 = org.apache.http.impl.conn.ThreadSafeClientConnManager.ConnectionPool.access$400(r16);	 Catch:{ all -> 0x0053 }
        r0 = r16;
        r0.remove(r14);	 Catch:{ all -> 0x0053 }
    L_0x01c6:
        if (r9 == 0) goto L_0x019d;
    L_0x01c8:
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0053 }
        r18 = r4 - r10;
        r12 = r12 - r18;
        goto L_0x019d;
    L_0x01d1:
        monitor-exit(r17);	 Catch:{ all -> 0x0053 }
        return r3;
    L_0x01d3:
        r16 = move-exception;
        r14 = r15;
        goto L_0x00e6;
    L_0x01d7:
        r2 = move-exception;
        goto L_0x00c8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.ThreadSafeClientConnManager.doGetConnection(org.apache.http.conn.HostConfiguration, long):org.apache.http.impl.conn.ThreadSafeClientConnManager$TrackingPoolEntry");
    }

    private TrackingPoolEntry createPoolEntry(HostConfiguration route) {
        return this.connectionPool.createEntry(route, this.connOperator.createConnection());
    }

    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg);
    }

    public void releaseConnection(ManagedClientConnection conn) {
        TrackingPoolEntry entry;
        if (conn instanceof HttpConnectionAdapter) {
            HttpConnectionAdapter hca = (HttpConnectionAdapter) conn;
            if (hca.poolEntry == null || hca.connManager == this) {
                try {
                    if (hca.isOpen() && !hca.isMarkedReusable()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Released connection open but not marked reusable.");
                        }
                        hca.shutdown();
                    }
                    entry = hca.poolEntry;
                    hca.detach();
                    releasePoolEntry(entry);
                    return;
                } catch (IOException iox) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Exception shutting down released connection.", iox);
                    }
                    entry = (TrackingPoolEntry) hca.poolEntry;
                    hca.detach();
                    releasePoolEntry(entry);
                    return;
                } catch (Throwable th) {
                    entry = (TrackingPoolEntry) hca.poolEntry;
                    hca.detach();
                    releasePoolEntry(entry);
                }
            } else {
                throw new IllegalArgumentException("Connection not obtained from this manager.");
            }
        }
        throw new IllegalArgumentException("Connection class mismatch, connection not obtained from this manager.");
    }

    private void releasePoolEntry(TrackingPoolEntry entry) {
        if (entry != null) {
            ConnectionPool.access$500(this.connectionPool, entry);
        }
    }

    public static void shutdownAll() {
        synchronized (REFERENCE_TO_CONNECTION_SOURCE) {
            synchronized (ALL_CONNECTION_MANAGERS) {
                ThreadSafeClientConnManager[] connManagers = (ThreadSafeClientConnManager[]) ALL_CONNECTION_MANAGERS.keySet().toArray(new ThreadSafeClientConnManager[ALL_CONNECTION_MANAGERS.size()]);
                for (int i = 0; i < connManagers.length; i++) {
                    if (connManagers[i] != null) {
                        connManagers[i].shutdown();
                    }
                }
            }
            if (REFERENCE_QUEUE_THREAD != null) {
                REFERENCE_QUEUE_THREAD.shutdown();
                REFERENCE_QUEUE_THREAD = null;
            }
            REFERENCE_TO_CONNECTION_SOURCE.clear();
        }
    }

    private static void storeReferenceToConnection(TrackingPoolEntry connection, HostConfiguration hostConfiguration, ConnectionPool connectionPool) {
        ConnectionSource source = new ConnectionSource(null);
        source.connectionPool = connectionPool;
        source.hostConfiguration = hostConfiguration;
        synchronized (REFERENCE_TO_CONNECTION_SOURCE) {
            if (REFERENCE_QUEUE_THREAD == null) {
                REFERENCE_QUEUE_THREAD = new ReferenceQueueThread();
                REFERENCE_QUEUE_THREAD.start();
            }
            REFERENCE_TO_CONNECTION_SOURCE.put(TrackingPoolEntry.access$700(connection), source);
        }
    }

    private static void removeReferenceToConnection(TrackingPoolEntry entry) {
        synchronized (REFERENCE_TO_CONNECTION_SOURCE) {
            REFERENCE_TO_CONNECTION_SOURCE.remove(TrackingPoolEntry.access$700(entry));
        }
    }

    private static void shutdownCheckedOutConnections(ConnectionPool connectionPool) {
        ArrayList connectionsToClose = new ArrayList();
        synchronized (REFERENCE_TO_CONNECTION_SOURCE) {
            Iterator referenceIter = REFERENCE_TO_CONNECTION_SOURCE.keySet().iterator();
            while (referenceIter.hasNext()) {
                Reference ref = (Reference) referenceIter.next();
                if (((ConnectionSource) REFERENCE_TO_CONNECTION_SOURCE.get(ref)).connectionPool == connectionPool) {
                    referenceIter.remove();
                    Object entry = ref.get();
                    if (entry != null) {
                        connectionsToClose.add(entry);
                    }
                }
            }
        }
        Iterator i = connectionsToClose.iterator();
        while (i.hasNext()) {
            TrackingPoolEntry entry2 = (TrackingPoolEntry) i.next();
            closeConnection(entry2.connection);
            TrackingPoolEntry.access$800(entry2).releasePoolEntry(entry2);
        }
    }

    public synchronized void shutdown() {
        synchronized (this.connectionPool) {
            if (!this.isShutDown) {
                this.isShutDown = true;
                this.connectionPool.shutdown();
            }
        }
    }

    public int getConnectionsInPool(HostConfiguration hostConfiguration) {
        int i;
        synchronized (this.connectionPool) {
            i = this.connectionPool.getHostPool(hostConfiguration).numConnections;
        }
        return i;
    }

    public int getConnectionsInPool() {
        int access$100;
        synchronized (this.connectionPool) {
            access$100 = ConnectionPool.access$100(this.connectionPool);
        }
        return access$100;
    }

    private void deleteClosedConnections() {
        this.connectionPool.deleteClosedConnections();
    }

    public void closeIdleConnections(long idleTimeout) {
        this.connectionPool.closeIdleConnections(idleTimeout);
        deleteClosedConnections();
    }

    public HttpParams getParams() {
        return this.params;
    }

    public void setParams(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    private static void closeConnection(OperatedClientConnection conn) {
        try {
            conn.close();
        } catch (IOException ex) {
            LOG.debug("I/O error closing connection", ex);
        }
    }
}

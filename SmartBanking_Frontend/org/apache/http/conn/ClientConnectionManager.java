package org.apache.http.conn;

public interface ClientConnectionManager {
    void closeIdleConnections(long j);

    ManagedClientConnection getConnection(HttpRoute httpRoute);

    ManagedClientConnection getConnection(HttpRoute httpRoute, long j) throws ConnectionPoolTimeoutException;

    SchemeRegistry getSchemeRegistry();

    void releaseConnection(ManagedClientConnection managedClientConnection);

    void shutdown();
}

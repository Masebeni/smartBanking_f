package org.apache.http.conn;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpInetConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public interface ManagedClientConnection extends HttpClientConnection, HttpInetConnection, ConnectionReleaseTrigger {
    HttpRoute getRoute();

    boolean isMarkedReusable();

    boolean isSecure();

    void layerProtocol(HttpContext httpContext, HttpParams httpParams) throws IOException;

    void markReusable();

    void open(HttpRoute httpRoute, HttpContext httpContext, HttpParams httpParams) throws IOException;

    void tunnelCreated(boolean z, HttpParams httpParams) throws IOException;

    void unmarkReusable();
}

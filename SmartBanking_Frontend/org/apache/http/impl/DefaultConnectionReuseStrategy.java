package org.apache.http.impl;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class DefaultConnectionReuseStrategy implements ConnectionReuseStrategy {
    public boolean keepAlive(HttpResponse response, HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        } else if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        } else {
            HttpConnection conn = (HttpConnection) context.getAttribute(HttpContext.HTTP_CONNECTION);
            if (conn != null && !conn.isOpen()) {
                return false;
            }
            HttpEntity entity = response.getEntity();
            HttpVersion ver = response.getStatusLine().getHttpVersion();
            if (entity != null && entity.getContentLength() < 0 && (!entity.isChunked() || ver.lessEquals(HttpVersion.HTTP_1_0))) {
                return false;
            }
            Header connheader = response.getFirstHeader(HTTP.CONN_DIRECTIVE);
            if (connheader == null) {
                connheader = response.getFirstHeader("Proxy-Connection");
            }
            if (connheader != null) {
                String conndirective = connheader.getValue();
                if (HTTP.CONN_CLOSE.equalsIgnoreCase(conndirective)) {
                    return false;
                }
                if (HTTP.CONN_KEEP_ALIVE.equalsIgnoreCase(conndirective)) {
                    return true;
                }
            }
            return ver.greaterEquals(HttpVersion.HTTP_1_1);
        }
    }
}

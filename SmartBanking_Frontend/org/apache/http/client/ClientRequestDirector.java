package org.apache.http.client;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.protocol.HttpContext;

public interface ClientRequestDirector {
    HttpResponse execute(RoutedRequest routedRequest, HttpContext httpContext) throws HttpException, IOException;

    ManagedClientConnection getConnection();
}

package org.apache.http.client;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public interface HttpClient {
    HttpResponse execute(RoutedRequest routedRequest) throws HttpException, IOException;

    HttpResponse execute(RoutedRequest routedRequest, HttpContext httpContext) throws HttpException, IOException;

    HttpResponse execute(HttpUriRequest httpUriRequest) throws HttpException, IOException;

    HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws HttpException, IOException;

    ClientConnectionManager getConnectionManager();

    HttpContext getDefaultContext();

    HttpParams getParams();
}

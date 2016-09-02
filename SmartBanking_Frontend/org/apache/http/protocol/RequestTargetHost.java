package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;

public class RequestTargetHost implements HttpRequestInterceptor {
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        } else if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        } else if (!request.containsHeader(HTTP.TARGET_HOST)) {
            HttpHost targethost = (HttpHost) context.getAttribute(HttpExecutionContext.HTTP_TARGET_HOST);
            if (targethost != null) {
                request.addHeader(HTTP.TARGET_HOST, targethost.toHostString());
            } else if (!request.getRequestLine().getHttpVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                throw new ProtocolException("Target host missing");
            }
        }
    }
}

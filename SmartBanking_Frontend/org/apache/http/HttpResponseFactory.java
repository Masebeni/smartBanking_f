package org.apache.http;

import org.apache.http.protocol.HttpContext;

public interface HttpResponseFactory {
    HttpResponse newHttpResponse(HttpVersion httpVersion, int i, HttpContext httpContext);

    HttpResponse newHttpResponse(StatusLine statusLine, HttpContext httpContext);
}

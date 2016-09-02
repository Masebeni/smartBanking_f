package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;

public interface HttpUriRequest extends HttpRequest {
    HttpVersion getHttpVersion();

    String getMethod();

    URI getURI();
}

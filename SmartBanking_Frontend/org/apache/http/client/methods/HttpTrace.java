package org.apache.http.client.methods;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpTrace extends HttpRequestBase {
    public static final String METHOD_NAME = "TRACE";

    public HttpTrace(URI uri) {
        setURI(uri);
    }

    public HttpTrace(String uri) throws URISyntaxException {
        setURI(new URI(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

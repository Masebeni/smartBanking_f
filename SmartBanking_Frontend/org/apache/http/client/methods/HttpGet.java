package org.apache.http.client.methods;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpGet extends HttpRequestBase {
    public static final String METHOD_NAME = "GET";

    public HttpGet(URI uri) {
        setURI(uri);
    }

    public HttpGet(String uri) throws URISyntaxException {
        setURI(new URI(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

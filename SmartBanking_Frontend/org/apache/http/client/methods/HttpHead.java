package org.apache.http.client.methods;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpHead extends HttpRequestBase {
    public static final String METHOD_NAME = "HEAD";

    public HttpHead(URI uri) {
        setURI(uri);
    }

    public HttpHead(String uri) throws URISyntaxException {
        setURI(new URI(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

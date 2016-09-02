package org.apache.http.client.methods;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpPut extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "PUT";

    public HttpPut(URI uri) {
        setURI(uri);
    }

    public HttpPut(String uri) throws URISyntaxException {
        setURI(new URI(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

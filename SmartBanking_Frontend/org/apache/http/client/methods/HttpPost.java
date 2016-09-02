package org.apache.http.client.methods;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpPost extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "POST";

    public HttpPost(URI uri) {
        setURI(uri);
    }

    public HttpPost(String uri) throws URISyntaxException {
        setURI(new URI(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

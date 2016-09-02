package org.apache.http.client.methods;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;

public class HttpOptions extends HttpRequestBase {
    public static final String METHOD_NAME = "OPTIONS";

    public HttpOptions(URI uri) {
        setURI(uri);
    }

    public HttpOptions(String uri) throws URISyntaxException {
        setURI(new URI(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }

    public Set getAllowedMethods(HttpResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        Header header = response.getFirstHeader("Allow");
        if (header == null) {
            return Collections.EMPTY_SET;
        }
        HeaderElement[] elements = header.getElements();
        Set methods = new HashSet(elements.length);
        for (HeaderElement name : elements) {
            methods.add(name.getName());
        }
        return methods;
    }
}

package org.apache.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpProtocolParams;

class RequestWrapper extends AbstractHttpMessage implements HttpUriRequest {
    private String method;
    private final HttpRequest original;
    private URI uri;
    private HttpVersion version;

    public RequestWrapper(HttpRequest request) throws URISyntaxException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        this.original = request;
        setHeaders(request.getAllHeaders());
        setParams(request.getParams());
        if (request instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest) request).getURI();
            this.method = ((HttpUriRequest) request).getMethod();
            this.version = null;
            return;
        }
        RequestLine requestLine = request.getRequestLine();
        this.uri = new URI(requestLine.getUri());
        this.method = requestLine.getMethod();
        this.version = request.getHttpVersion();
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        if (method == null) {
            throw new IllegalArgumentException("Method name may not be null");
        }
        this.method = method;
    }

    public HttpVersion getHttpVersion() {
        if (this.version != null) {
            return this.version;
        }
        return HttpProtocolParams.getVersion(getParams());
    }

    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    public URI getURI() {
        return this.uri;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }

    public RequestLine getRequestLine() {
        String uritext;
        String method = getMethod();
        HttpVersion ver = getHttpVersion();
        URI uri = getURI();
        if (uri != null) {
            uritext = uri.toASCIIString();
        } else {
            uritext = "/";
        }
        return new BasicRequestLine(method, uritext, ver);
    }

    public HttpRequest getOriginal() {
        return this.original;
    }
}

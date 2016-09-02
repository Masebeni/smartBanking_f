package org.apache.http.client.methods;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpProtocolParams;

abstract class HttpRequestBase extends AbstractHttpMessage implements HttpUriRequest, AbortableHttpRequest {
    private ConnectionReleaseTrigger releaseTrigger;
    private URI uri;

    public abstract String getMethod();

    public HttpVersion getHttpVersion() {
        return HttpProtocolParams.getVersion(getParams());
    }

    public URI getURI() {
        return this.uri;
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

    public void setURI(URI uri) {
        this.uri = uri;
    }

    public void setReleaseTrigger(ConnectionReleaseTrigger releaseTrigger) {
        this.releaseTrigger = releaseTrigger;
    }

    public void abort() {
        if (this.releaseTrigger != null) {
            try {
                this.releaseTrigger.abortConnection();
            } catch (IOException e) {
            }
        }
    }
}

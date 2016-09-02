package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HTTP;

public class BasicHttpEntityEnclosingRequest extends BasicHttpRequest implements HttpEntityEnclosingRequest {
    private HttpEntity entity;

    public BasicHttpEntityEnclosingRequest(String method, String uri) {
        super(method, uri);
        this.entity = null;
    }

    public BasicHttpEntityEnclosingRequest(String method, String uri, HttpVersion ver) {
        this(new BasicRequestLine(method, uri, ver));
    }

    public BasicHttpEntityEnclosingRequest(RequestLine requestline) {
        super(requestline);
        this.entity = null;
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    public boolean expectContinue() {
        Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }
}

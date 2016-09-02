package org.apache.http.impl.client;

import java.net.URISyntaxException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.protocol.HTTP;

class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest {
    private HttpEntity entity;

    public EntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request) throws URISyntaxException {
        super(request);
        this.entity = null;
        this.entity = request.getEntity();
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

package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExecutionContext;

public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {
    private boolean requestSentRetryEnabled;
    private int retryCount;

    public DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
        this.retryCount = retryCount;
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }

    public DefaultHttpRequestRetryHandler() {
        this(3, false);
    }

    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception parameter may not be null");
        } else if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        } else if (executionCount > this.retryCount) {
            return false;
        } else {
            if (exception instanceof NoHttpResponseException) {
                return true;
            }
            if ((exception instanceof InterruptedIOException) || (exception instanceof UnknownHostException) || (exception instanceof SSLHandshakeException)) {
                return false;
            }
            boolean sent;
            Boolean b = (Boolean) context.getAttribute(HttpExecutionContext.HTTP_REQ_SENT);
            if (b == null || !b.booleanValue()) {
                sent = false;
            } else {
                sent = true;
            }
            if (!sent || this.requestSentRetryEnabled) {
                return true;
            }
            return false;
        }
    }

    public boolean isRequestSentRetryEnabled() {
        return this.requestSentRetryEnabled;
    }

    public int getRetryCount() {
        return this.retryCount;
    }
}
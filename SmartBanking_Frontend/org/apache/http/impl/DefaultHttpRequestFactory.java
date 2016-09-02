package org.apache.http.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

public class DefaultHttpRequestFactory implements HttpRequestFactory {
    public HttpRequest newHttpRequest(RequestLine requestline) throws MethodNotSupportedException {
        if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        String method = requestline.getMethod();
        if (HttpGet.METHOD_NAME.equalsIgnoreCase(method)) {
            return new BasicHttpRequest(requestline);
        }
        if (HttpHead.METHOD_NAME.equalsIgnoreCase(method)) {
            return new BasicHttpRequest(requestline);
        }
        if (HttpPost.METHOD_NAME.equalsIgnoreCase(method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        }
        throw new MethodNotSupportedException(new StringBuffer().append(method).append(" method not supported").toString());
    }

    public HttpRequest newHttpRequest(String method, String uri) throws MethodNotSupportedException {
        if (HttpGet.METHOD_NAME.equalsIgnoreCase(method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (HttpPost.METHOD_NAME.equalsIgnoreCase(method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        throw new MethodNotSupportedException(new StringBuffer().append(method).append(" method not supported").toString());
    }
}

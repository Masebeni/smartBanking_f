package org.apache.http.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;

public class BasicHttpProcessor implements HttpProcessor, HttpRequestInterceptorList, HttpResponseInterceptorList {
    private List requestInterceptors;
    private List responseInterceptors;

    public BasicHttpProcessor() {
        this.requestInterceptors = null;
        this.responseInterceptors = null;
    }

    public void addRequestInterceptor(HttpRequestInterceptor itcp) {
        if (itcp != null) {
            if (this.requestInterceptors == null) {
                this.requestInterceptors = new ArrayList();
            }
            this.requestInterceptors.add(itcp);
        }
    }

    public final void addInterceptor(HttpRequestInterceptor interceptor) {
        addRequestInterceptor(interceptor);
    }

    public int getRequestInterceptorCount() {
        return this.requestInterceptors == null ? 0 : this.requestInterceptors.size();
    }

    public HttpRequestInterceptor getRequestInterceptor(int index) {
        if (this.requestInterceptors == null || index < 0 || index >= this.requestInterceptors.size()) {
            return null;
        }
        return (HttpRequestInterceptor) this.requestInterceptors.get(index);
    }

    public void clearRequestInterceptors() {
        this.requestInterceptors = null;
    }

    public void addResponseInterceptor(HttpResponseInterceptor itcp) {
        if (itcp != null) {
            if (this.responseInterceptors == null) {
                this.responseInterceptors = new ArrayList();
            }
            this.responseInterceptors.add(itcp);
        }
    }

    public final void addInterceptor(HttpResponseInterceptor interceptor) {
        addResponseInterceptor(interceptor);
    }

    public int getResponseInterceptorCount() {
        return this.responseInterceptors == null ? 0 : this.responseInterceptors.size();
    }

    public HttpResponseInterceptor getResponseInterceptor(int index) {
        if (this.responseInterceptors == null || index < 0 || index >= this.responseInterceptors.size()) {
            return null;
        }
        return (HttpResponseInterceptor) this.responseInterceptors.get(index);
    }

    public void clearResponseInterceptors() {
        this.responseInterceptors = null;
    }

    public void setInterceptors(List list) {
        if (list == null) {
            throw new IllegalArgumentException("List must not be null.");
        }
        if (this.requestInterceptors != null) {
            this.requestInterceptors.clear();
        }
        if (this.responseInterceptors != null) {
            this.responseInterceptors.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (obj instanceof HttpRequestInterceptor) {
                addInterceptor((HttpRequestInterceptor) obj);
            }
            if (obj instanceof HttpResponseInterceptor) {
                addInterceptor((HttpResponseInterceptor) obj);
            }
        }
    }

    public void clearInterceptors() {
        clearRequestInterceptors();
        clearResponseInterceptors();
    }

    public void process(HttpRequest request, HttpContext context) throws IOException, HttpException {
        if (this.requestInterceptors != null) {
            for (int i = 0; i < this.requestInterceptors.size(); i++) {
                ((HttpRequestInterceptor) this.requestInterceptors.get(i)).process(request, context);
            }
        }
    }

    public void process(HttpResponse response, HttpContext context) throws IOException, HttpException {
        if (this.responseInterceptors != null) {
            for (int i = 0; i < this.responseInterceptors.size(); i++) {
                ((HttpResponseInterceptor) this.responseInterceptors.get(i)).process(response, context);
            }
        }
    }

    public BasicHttpProcessor copy() {
        BasicHttpProcessor clone = new BasicHttpProcessor();
        if (this.requestInterceptors != null) {
            clone.requestInterceptors = new ArrayList(this.requestInterceptors);
        }
        if (this.responseInterceptors != null) {
            clone.responseInterceptors = new ArrayList(this.responseInterceptors);
        }
        return clone;
    }
}

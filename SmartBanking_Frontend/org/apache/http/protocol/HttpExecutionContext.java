package org.apache.http.protocol;

import java.util.HashMap;
import java.util.Map;

public class HttpExecutionContext implements HttpContext {
    public static final String HTTP_PROXY_HOST = "http.proxy_host";
    public static final String HTTP_REQ_SENT = "http.request_sent";
    public static final String HTTP_TARGET_HOST = "http.target_host";
    private Map map;
    private final HttpContext parentContext;

    public HttpExecutionContext(HttpContext parentContext) {
        this.map = null;
        this.parentContext = parentContext;
    }

    public Object getAttribute(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        Object obj = null;
        if (this.map != null) {
            obj = this.map.get(id);
        }
        if (obj != null || this.parentContext == null) {
            return obj;
        }
        return this.parentContext.getAttribute(id);
    }

    public void setAttribute(String id, Object obj) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(id, obj);
    }

    public Object removeAttribute(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        } else if (this.map != null) {
            return this.map.remove(id);
        } else {
            return null;
        }
    }
}

package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieSpec;

public abstract class AbstractCookieSpec implements CookieSpec {
    private final List attribHandlerList;
    private final Map attribHandlerMap;

    public AbstractCookieSpec() {
        this.attribHandlerMap = new HashMap(10);
        this.attribHandlerList = new ArrayList(10);
    }

    public void registerAttribHandler(String name, CookieAttributeHandler handler) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name may not be null");
        } else if (handler == null) {
            throw new IllegalArgumentException("Attribute handler may not be null");
        } else {
            if (!this.attribHandlerList.contains(handler)) {
                this.attribHandlerList.add(handler);
            }
            this.attribHandlerMap.put(name, handler);
        }
    }

    protected CookieAttributeHandler findAttribHandler(String name) {
        return (CookieAttributeHandler) this.attribHandlerMap.get(name);
    }

    protected CookieAttributeHandler getAttribHandler(String name) {
        CookieAttributeHandler handler = findAttribHandler(name);
        if (handler != null) {
            return handler;
        }
        throw new IllegalStateException(new StringBuffer().append("Handler not registered for ").append(name).append(" attribute.").toString());
    }

    protected Iterator getAttribHandlerIterator() {
        return this.attribHandlerList.iterator();
    }
}

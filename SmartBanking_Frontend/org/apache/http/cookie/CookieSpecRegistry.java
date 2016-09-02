package org.apache.http.cookie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.params.HttpParams;

public final class CookieSpecRegistry {
    private final Map registeredSpecs;

    public CookieSpecRegistry() {
        this.registeredSpecs = new LinkedHashMap();
    }

    public synchronized void register(String name, CookieSpecFactory factory) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        } else if (factory == null) {
            throw new IllegalArgumentException("Cookie spec factory may not be null");
        } else {
            this.registeredSpecs.put(name.toLowerCase(), factory);
        }
    }

    public synchronized void unregister(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        this.registeredSpecs.remove(id.toLowerCase());
    }

    public synchronized CookieSpec getCookieSpec(String name, HttpParams params) throws IllegalStateException {
        CookieSpecFactory factory;
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        factory = (CookieSpecFactory) this.registeredSpecs.get(name.toLowerCase());
        if (factory != null) {
        } else {
            throw new IllegalStateException(new StringBuffer().append("Unsupported cookie spec: ").append(name).toString());
        }
        return factory.newInstance(params);
    }

    public synchronized CookieSpec getCookieSpec(String name) throws IllegalStateException {
        return getCookieSpec(name, null);
    }

    public synchronized List getSpecNames() {
        return new ArrayList(this.registeredSpecs.keySet());
    }
}

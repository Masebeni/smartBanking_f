package org.apache.http.conn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;

public final class SchemeRegistry {
    private final Map registeredSchemes;

    public SchemeRegistry() {
        this.registeredSchemes = new LinkedHashMap();
    }

    public final synchronized Scheme getScheme(String name) {
        Scheme found;
        found = get(name);
        if (found == null) {
            throw new IllegalStateException(new StringBuffer().append("Scheme '").append(name).append("' not registered.").toString());
        }
        return found;
    }

    public final synchronized Scheme getScheme(HttpHost host) {
        if (host == null) {
            throw new IllegalArgumentException("Host must not be null.");
        }
        return getScheme(host.getSchemeName());
    }

    public final synchronized Scheme get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null.");
        }
        return (Scheme) this.registeredSchemes.get(name);
    }

    public final synchronized Scheme register(Scheme sch) {
        if (sch == null) {
            throw new IllegalArgumentException("Scheme must not be null.");
        }
        return (Scheme) this.registeredSchemes.put(sch.getName(), sch);
    }

    public final synchronized Scheme unregister(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null.");
        }
        return (Scheme) this.registeredSchemes.remove(name);
    }

    public final synchronized List getSchemeNames() {
        return new ArrayList(this.registeredSchemes.keySet());
    }
}

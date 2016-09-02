package org.apache.http.auth;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.params.HttpParams;

public final class AuthSchemeRegistry {
    private final Map registeredSchemes;

    public AuthSchemeRegistry() {
        this.registeredSchemes = new LinkedHashMap();
    }

    public synchronized void register(String name, AuthSchemeFactory factory) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        } else if (factory == null) {
            throw new IllegalArgumentException("Authentication scheme factory may not be null");
        } else {
            this.registeredSchemes.put(name.toLowerCase(), factory);
        }
    }

    public synchronized void unregister(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.registeredSchemes.remove(name.toLowerCase());
    }

    public synchronized AuthScheme getAuthScheme(String name, HttpParams params) throws IllegalStateException {
        AuthSchemeFactory factory;
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        factory = (AuthSchemeFactory) this.registeredSchemes.get(name.toLowerCase());
        if (factory != null) {
        } else {
            throw new IllegalStateException(new StringBuffer().append("Unsupported authentication scheme: ").append(name).toString());
        }
        return factory.newInstance(params);
    }

    public synchronized List getSchemeNames() {
        return new ArrayList(this.registeredSchemes.keySet());
    }
}

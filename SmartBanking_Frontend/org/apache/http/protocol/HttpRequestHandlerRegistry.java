package org.apache.http.protocol;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandlerRegistry implements HttpRequestHandlerResolver {
    private final Map handlerMap;

    public HttpRequestHandlerRegistry() {
        this.handlerMap = new HashMap();
    }

    public void register(String pattern, HttpRequestHandler handler) {
        if (pattern == null) {
            throw new IllegalArgumentException("URI request pattern may not be null");
        } else if (handler == null) {
            throw new IllegalArgumentException("HTTP request handelr may not be null");
        } else {
            this.handlerMap.put(pattern, handler);
        }
    }

    public void unregister(String pattern) {
        if (pattern != null) {
            this.handlerMap.remove(pattern);
        }
    }

    public void setHandlers(Map map) {
        if (map == null) {
            throw new IllegalArgumentException("Map of handlers may not be null");
        }
        this.handlerMap.clear();
        this.handlerMap.putAll(map);
    }

    public HttpRequestHandler lookup(String requestURI) {
        if (requestURI == null) {
            throw new IllegalArgumentException("Request URI may not be null");
        }
        int index = requestURI.indexOf("?");
        if (index != -1) {
            requestURI = requestURI.substring(0, index);
        }
        Object handler = this.handlerMap.get(requestURI);
        if (handler == null) {
            String bestMatch = null;
            for (String pattern : this.handlerMap.keySet()) {
                if (matchUriRequestPattern(pattern, requestURI) && (bestMatch == null || bestMatch.length() < pattern.length() || (bestMatch.length() == pattern.length() && pattern.endsWith("*")))) {
                    handler = this.handlerMap.get(pattern);
                    bestMatch = pattern;
                }
            }
        }
        return (HttpRequestHandler) handler;
    }

    protected boolean matchUriRequestPattern(String pattern, String requestUri) {
        boolean z = false;
        if (pattern.equals("*")) {
            return true;
        }
        if ((pattern.endsWith("*") && requestUri.startsWith(pattern.substring(0, pattern.length() - 1))) || (pattern.startsWith("*") && requestUri.endsWith(pattern.substring(1, pattern.length())))) {
            z = true;
        }
        return z;
    }
}
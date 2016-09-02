package org.apache.http.client.params;

import org.apache.http.params.HttpParams;

public class HttpClientParams {
    public static final String ALLOW_CIRCULAR_REDIRECTS = "http.protocol.allow-circular-redirects";
    public static final String AUTH_SCHEME_PRIORITY = "http.protocol-auth-scheme-priority";
    public static final String CONNECTION_MANAGER_FACTORY = "http.connection-manager.factory";
    public static final String CONNECTION_MANAGER_TIMEOUT = "http.connection-manager.timeout";
    public static final String COOKIE_POLICY = "http.protocol.cookie-policy";
    public static final String DEFAULT_HEADERS = "http.default-headers";
    public static final String DEFAULT_HOST = "http.default-host";
    public static final String DEFAULT_PROXY = "http.default-proxy";
    public static final String HANDLE_AUTHENTICATION = "http.protocol.handle-authentication";
    public static final String HANDLE_REDIRECTS = "http.protocol.handle-redirects";
    public static final String MAX_REDIRECTS = "http.protocol.max-redirects";
    public static final String PREEMPTIVE_AUTHENTICATION = "http.protocol.authentication-preemptive";
    public static final String REJECT_RELATIVE_REDIRECT = "http.protocol.reject-relative-redirect";
    public static final String VIRTUAL_HOST = "http.virtual-host";

    private HttpClientParams() {
    }

    public static long getConnectionManagerTimeout(HttpParams params) {
        if (params != null) {
            return params.getLongParameter(CONNECTION_MANAGER_TIMEOUT, 0);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setConnectionManagerTimeout(HttpParams params, long timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setLongParameter(CONNECTION_MANAGER_TIMEOUT, timeout);
    }

    public static boolean isRedirecting(HttpParams params) {
        if (params != null) {
            return params.getBooleanParameter(HANDLE_REDIRECTS, true);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setRedirecting(HttpParams params, boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter(HANDLE_REDIRECTS, value);
    }

    public static boolean isAuthenticating(HttpParams params) {
        if (params != null) {
            return params.getBooleanParameter(HANDLE_AUTHENTICATION, true);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setAuthenticating(HttpParams params, boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter(HANDLE_AUTHENTICATION, value);
    }

    public static boolean isAuthenticationPreemptive(HttpParams params) {
        if (params != null) {
            return params.getBooleanParameter(PREEMPTIVE_AUTHENTICATION, false);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setAuthenticationPreemptive(HttpParams params, boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter(PREEMPTIVE_AUTHENTICATION, value);
    }

    public static String getCookiePolicy(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        String cookiePolicy = (String) params.getParameter(COOKIE_POLICY);
        if (cookiePolicy == null) {
            return CookiePolicy.BROWSER_COMPATIBILITY;
        }
        return cookiePolicy;
    }

    public static void setCookiePolicy(HttpParams params, String cookiePolicy) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter(COOKIE_POLICY, cookiePolicy);
    }
}

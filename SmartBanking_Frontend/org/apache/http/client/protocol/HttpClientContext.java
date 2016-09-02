package org.apache.http.client.protocol;

import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExecutionContext;

public class HttpClientContext extends HttpExecutionContext {
    public static final String AUTHSCHEME_REGISTRY = "http.authscheme-registry";
    public static final String COOKIESPEC_REGISTRY = "http.cookiespec-registry";
    public static final String COOKIE_ORIGIN = "http.cookie-origin";
    public static final String COOKIE_SPEC = "http.cookie-spec";
    public static final String HTTP_STATE = "http.state";
    public static final String PROXY_AUTH_STATE = "http.auth.proxy-scope";
    public static final String TARGET_AUTH_STATE = "http.auth.target-scope";

    public HttpClientContext(HttpContext parentContext) {
        super(parentContext);
    }
}

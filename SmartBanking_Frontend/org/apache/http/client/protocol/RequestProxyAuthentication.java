package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthState;
import org.apache.http.protocol.HttpContext;

public class RequestProxyAuthentication implements HttpRequestInterceptor {
    private static final Log LOG;
    static Class class$org$apache$http$client$protocol$RequestProxyAuthentication;

    static {
        Class class$;
        if (class$org$apache$http$client$protocol$RequestProxyAuthentication == null) {
            class$ = class$("org.apache.http.client.protocol.RequestProxyAuthentication");
            class$org$apache$http$client$protocol$RequestProxyAuthentication = class$;
        } else {
            class$ = class$org$apache$http$client$protocol$RequestProxyAuthentication;
        }
        LOG = LogFactory.getLog(class$);
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        } else if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        } else if (!request.containsHeader(AUTH.PROXY_AUTH_RESP)) {
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.PROXY_AUTH_STATE);
            if (authState != null) {
                AuthScheme authScheme = authState.getAuthScheme();
                if (authScheme != null) {
                    Credentials creds = authState.getCredentials();
                    if (creds == null) {
                        LOG.debug("User credentials not available");
                    } else if (authState.getAuthScope() != null || !authScheme.isConnectionBased()) {
                        try {
                            request.addHeader(authScheme.authenticate(creds, request));
                        } catch (AuthenticationException ex) {
                            if (LOG.isErrorEnabled()) {
                                LOG.error(new StringBuffer().append("Proxy authentication error: ").append(ex.getMessage()).toString());
                            }
                        }
                    }
                }
            }
        }
    }
}

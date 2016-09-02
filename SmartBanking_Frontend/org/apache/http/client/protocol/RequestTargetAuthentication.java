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

public class RequestTargetAuthentication implements HttpRequestInterceptor {
    private static final Log LOG;
    static Class f8x7fb95833;

    static {
        Class class$;
        if (f8x7fb95833 == null) {
            class$ = class$("org.apache.http.client.protocol.RequestTargetAuthentication");
            f8x7fb95833 = class$;
        } else {
            class$ = f8x7fb95833;
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
        } else if (!request.containsHeader(AUTH.WWW_AUTH_RESP)) {
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
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
                                LOG.error(new StringBuffer().append("Authentication error: ").append(ex.getMessage()).toString());
                            }
                        }
                    }
                }
            }
        }
    }
}

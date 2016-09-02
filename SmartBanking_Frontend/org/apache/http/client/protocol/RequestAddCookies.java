package org.apache.http.client.protocol;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpState;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExecutionContext;

public class RequestAddCookies implements HttpRequestInterceptor {
    private static final Log LOG;
    static Class class$org$apache$http$client$protocol$RequestAddCookies;

    static {
        Class class$;
        if (class$org$apache$http$client$protocol$RequestAddCookies == null) {
            class$ = class$("org.apache.http.client.protocol.RequestAddCookies");
            class$org$apache$http$client$protocol$RequestAddCookies = class$;
        } else {
            class$ = class$org$apache$http$client$protocol$RequestAddCookies;
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
        } else {
            HttpState state = (HttpState) context.getAttribute(HttpClientContext.HTTP_STATE);
            if (state == null) {
                LOG.info("HTTP state not available in HTTP context");
                return;
            }
            CookieSpecRegistry registry = (CookieSpecRegistry) context.getAttribute(HttpClientContext.COOKIESPEC_REGISTRY);
            if (registry == null) {
                LOG.info("CookieSpec registry not available in HTTP context");
                return;
            }
            HttpHost targetHost = (HttpHost) context.getAttribute(HttpExecutionContext.HTTP_TARGET_HOST);
            if (targetHost == null) {
                throw new IllegalStateException("Target host not specified in HTTP context");
            }
            ManagedClientConnection conn = (ManagedClientConnection) context.getAttribute(HttpContext.HTTP_CONNECTION);
            if (conn == null) {
                throw new IllegalStateException("Client connection not specified in HTTP context");
            }
            URI requestURI;
            int length;
            String policy = HttpClientParams.getCookiePolicy(request.getParams());
            if (LOG.isDebugEnabled()) {
                LOG.debug(new StringBuffer().append("CookieSpec selected: ").append(policy).toString());
            }
            if (request instanceof HttpUriRequest) {
                requestURI = ((HttpUriRequest) request).getURI();
            } else {
                try {
                    requestURI = new URI(request.getRequestLine().getUri());
                } catch (URISyntaxException ex) {
                    throw new ProtocolException(new StringBuffer().append("Invalid request URI: ").append(request.getRequestLine().getUri()).toString(), ex);
                }
            }
            String hostName = targetHost.getHostName();
            int port = targetHost.getPort();
            if (port < 0) {
                port = conn.getRemotePort();
            }
            CookieOrigin cookieOrigin = new CookieOrigin(hostName, port, requestURI.getPath(), conn.isSecure());
            CookieSpec cookieSpec = registry.getCookieSpec(policy, request.getParams());
            Cookie[] cookies = state.getCookies();
            List matchedCookies = new ArrayList(cookies.length);
            int i = 0;
            while (true) {
                length = cookies.length;
                if (i >= r0) {
                    break;
                }
                Cookie cookie = cookies[i];
                if (cookieSpec.match(cookie, cookieOrigin)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(new StringBuffer().append("Cookie ").append(cookie).append(" match ").append(cookieOrigin).toString());
                    }
                    matchedCookies.add(cookie);
                }
                i++;
            }
            cookies = (Cookie[]) matchedCookies.toArray(new Cookie[matchedCookies.size()]);
            if (cookies.length > 0) {
                Header[] headers = cookieSpec.formatCookies(cookies);
                i = 0;
                while (true) {
                    length = headers.length;
                    if (i >= r0) {
                        break;
                    }
                    request.addHeader(headers[i]);
                    i++;
                }
            }
            context.setAttribute(HttpClientContext.COOKIE_SPEC, cookieSpec);
            context.setAttribute(HttpClientContext.COOKIE_ORIGIN, cookieOrigin);
        }
    }
}

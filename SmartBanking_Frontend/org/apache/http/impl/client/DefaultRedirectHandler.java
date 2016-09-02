package org.apache.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExecutionContext;

public class DefaultRedirectHandler implements RedirectHandler {
    private static final Log LOG;
    private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    static Class class$org$apache$http$impl$client$DefaultRedirectHandler;

    static {
        Class class$;
        if (class$org$apache$http$impl$client$DefaultRedirectHandler == null) {
            class$ = class$("org.apache.http.impl.client.DefaultRedirectHandler");
            class$org$apache$http$impl$client$DefaultRedirectHandler = class$;
        } else {
            class$ = class$org$apache$http$impl$client$DefaultRedirectHandler;
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

    public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        switch (response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_MOVED_PERMANENTLY /*301*/:
            case HttpStatus.SC_MOVED_TEMPORARILY /*302*/:
            case HttpStatus.SC_SEE_OTHER /*303*/:
            case HttpStatus.SC_TEMPORARY_REDIRECT /*307*/:
                return true;
            default:
                return false;
        }
    }

    public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            throw new ProtocolException(new StringBuffer().append("Received redirect response ").append(response.getStatusLine()).append(" but no location header").toString());
        }
        String location = locationHeader.getValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("Redirect requested to location '").append(location).append("'").toString());
        }
        try {
            URI uri;
            URI uri2 = new URI(location);
            HttpParams params = response.getParams();
            if (uri2.isAbsolute()) {
                uri = uri2;
            } else if (params.isParameterTrue(HttpClientParams.REJECT_RELATIVE_REDIRECT)) {
                throw new ProtocolException(new StringBuffer().append("Relative redirect location '").append(uri2).append("' not allowed").toString());
            } else {
                HttpHost target = (HttpHost) context.getAttribute(HttpExecutionContext.HTTP_TARGET_HOST);
                if (target == null) {
                    throw new IllegalStateException("Target host not available in the HTTP context");
                }
                try {
                    uri = new URI(target.getSchemeName(), null, target.getHostName(), target.getPort(), uri2.getPath(), uri2.getQuery(), uri2.getFragment());
                } catch (URISyntaxException ex) {
                    throw new ProtocolException(ex.getMessage(), ex);
                }
            }
            if (params.isParameterFalse(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS)) {
                URI redirectURI;
                Set redirectLocations = (Set) context.getAttribute(REDIRECT_LOCATIONS);
                if (redirectLocations == null) {
                    redirectLocations = new HashSet();
                    context.setAttribute(REDIRECT_LOCATIONS, redirectLocations);
                }
                if (uri.getQuery() == null && uri.getFragment() == null) {
                    redirectURI = uri;
                } else {
                    try {
                        redirectURI = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null);
                    } catch (URISyntaxException ex2) {
                        throw new ProtocolException(ex2.getMessage(), ex2);
                    }
                }
                if (redirectLocations.contains(redirectURI)) {
                    throw new CircularRedirectException(new StringBuffer().append("Circular redirect to '").append(redirectURI).append("'").toString());
                }
                redirectLocations.add(redirectURI);
            }
            return uri;
        } catch (URISyntaxException ex22) {
            throw new ProtocolException(new StringBuffer().append("Invalid redirect URI: ").append(location).toString(), ex22);
        }
    }
}

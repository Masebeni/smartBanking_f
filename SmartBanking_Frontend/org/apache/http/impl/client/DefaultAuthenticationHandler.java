package org.apache.http.impl.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BufferedHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;

public class DefaultAuthenticationHandler implements AuthenticationHandler {
    private static List DEFAULT_SCHEME_PRIORITY;
    private static final Log LOG;
    static Class class$org$apache$http$impl$client$DefaultAuthenticationHandler;

    static {
        Class class$;
        if (class$org$apache$http$impl$client$DefaultAuthenticationHandler == null) {
            class$ = class$("org.apache.http.impl.client.DefaultAuthenticationHandler");
            class$org$apache$http$impl$client$DefaultAuthenticationHandler = class$;
        } else {
            class$ = class$org$apache$http$impl$client$DefaultAuthenticationHandler;
        }
        LOG = LogFactory.getLog(class$);
        DEFAULT_SCHEME_PRIORITY = Arrays.asList(new String[]{"digest", "basic"});
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public boolean isTargetAuthenticationRequested(HttpResponse response, HttpContext context) {
        if (response != null) {
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED;
        } else {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
    }

    public boolean isProxyAuthenticationRequested(HttpResponse response, HttpContext context) {
        if (response != null) {
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED;
        } else {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
    }

    protected Map parseChallenges(Header[] headers) throws MalformedChallengeException {
        Map map = new HashMap(headers.length);
        for (Header header : headers) {
            CharArrayBuffer buffer;
            int pos;
            if (header instanceof BufferedHeader) {
                buffer = ((BufferedHeader) header).getBuffer();
                pos = ((BufferedHeader) header).getValuePos();
            } else {
                String s = header.getValue();
                if (s == null) {
                    throw new MalformedChallengeException("Header value is null");
                }
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                pos = 0;
            }
            while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
                pos++;
            }
            int beginIndex = pos;
            while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
                pos++;
            }
            map.put(buffer.substring(beginIndex, pos).toLowerCase(), header);
        }
        return map;
    }

    public Map getTargetChallenges(HttpResponse response, HttpContext context) throws MalformedChallengeException {
        if (response != null) {
            return parseChallenges(response.getHeaders(AUTH.WWW_AUTH));
        }
        throw new IllegalArgumentException("HTTP response may not be null");
    }

    public Map getProxyChallenges(HttpResponse response, HttpContext context) throws MalformedChallengeException {
        if (response != null) {
            return parseChallenges(response.getHeaders(AUTH.PROXY_AUTH));
        }
        throw new IllegalArgumentException("HTTP response may not be null");
    }

    public AuthScheme selectScheme(Map challenges, HttpResponse response, HttpContext context) throws AuthenticationException {
        AuthSchemeRegistry registry = (AuthSchemeRegistry) context.getAttribute(HttpClientContext.AUTHSCHEME_REGISTRY);
        if (registry == null) {
            throw new IllegalStateException("AuthScheme registry not set in HTTP context");
        }
        HttpParams params = response.getParams();
        Collection authPrefs = (Collection) params.getParameter(HttpClientParams.AUTH_SCHEME_PRIORITY);
        if (authPrefs == null || authPrefs.isEmpty()) {
            authPrefs = DEFAULT_SCHEME_PRIORITY;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("Supported authentication schemes in the order of preference: ").append(authPrefs).toString());
        }
        AuthScheme authScheme = null;
        for (String id : authPrefs) {
            if (((Header) challenges.get(id.toLowerCase())) != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(new StringBuffer().append(id).append(" authentication scheme selected").toString());
                }
                try {
                    authScheme = registry.getAuthScheme(id, params);
                    if (authScheme == null) {
                        return authScheme;
                    }
                    throw new AuthenticationException(new StringBuffer().append("Unable to respond to any of these challenges: ").append(challenges).toString());
                } catch (IllegalStateException e) {
                    throw new AuthenticationException(e.getMessage());
                }
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(new StringBuffer().append("Challenge for ").append(id).append(" authentication scheme not available").toString());
            }
        }
        if (authScheme == null) {
            return authScheme;
        }
        throw new AuthenticationException(new StringBuffer().append("Unable to respond to any of these challenges: ").append(challenges).toString());
    }
}

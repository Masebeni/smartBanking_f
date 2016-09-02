package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpState;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.HttpContext;

public class ResponseProcessCookies implements HttpResponseInterceptor {
    private static final Log LOG;
    static Class class$org$apache$http$client$protocol$ResponseProcessCookies;

    static {
        Class class$;
        if (class$org$apache$http$client$protocol$ResponseProcessCookies == null) {
            class$ = class$("org.apache.http.client.protocol.ResponseProcessCookies");
            class$org$apache$http$client$protocol$ResponseProcessCookies = class$;
        } else {
            class$ = class$org$apache$http$client$protocol$ResponseProcessCookies;
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

    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        } else if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        } else {
            HttpState state = (HttpState) context.getAttribute(HttpClientContext.HTTP_STATE);
            if (state == null) {
                LOG.info("HTTP state not available in HTTP context");
                return;
            }
            CookieSpec cookieSpec = (CookieSpec) context.getAttribute(HttpClientContext.COOKIE_SPEC);
            if (cookieSpec == null) {
                LOG.info("CookieSpec not available in HTTP context");
                return;
            }
            CookieOrigin cookieOrigin = (CookieOrigin) context.getAttribute(HttpClientContext.COOKIE_ORIGIN);
            if (cookieOrigin == null) {
                LOG.info("CookieOrigin not available in HTTP context");
            } else {
                processCookies(response.getHeaders(SM.SET_COOKIE), cookieSpec, cookieOrigin, state);
            }
        }
    }

    private static void processCookies(Header[] headers, CookieSpec cookieSpec, CookieOrigin cookieOrigin, HttpState state) {
        for (Header header : headers) {
            try {
                Cookie[] cookies = cookieSpec.parse(header, cookieOrigin);
                for (Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        state.addCookie(cookie);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(new StringBuffer().append("Cookie accepted: \"").append(cookie).append("\". ").toString());
                        }
                    } catch (MalformedCookieException ex) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn(new StringBuffer().append("Cookie rejected: \"").append(cookie).append("\". ").append(ex.getMessage()).toString());
                        }
                    }
                }
            } catch (MalformedCookieException ex2) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(new StringBuffer().append("Invalid cookie header: \"").append(header).append("\". ").append(ex2.getMessage()).toString());
                }
            }
        }
    }
}

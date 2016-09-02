package org.apache.http.impl.cookie;

import java.util.StringTokenizer;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

public class NetscapeDomainHandler extends BasicDomainHandler {
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        super.validate(cookie, origin);
        String host = origin.getHost();
        String domain = cookie.getDomain();
        if (host.indexOf(".") >= 0) {
            int domainParts = new StringTokenizer(domain, ".").countTokens();
            if (isSpecialDomain(domain)) {
                if (domainParts < 2) {
                    throw new MalformedCookieException(new StringBuffer().append("Domain attribute \"").append(domain).append("\" violates the Netscape cookie specification for ").append("special domains").toString());
                }
            } else if (domainParts < 3) {
                throw new MalformedCookieException(new StringBuffer().append("Domain attribute \"").append(domain).append("\" violates the Netscape cookie specification").toString());
            }
        }
    }

    private static boolean isSpecialDomain(String domain) {
        String ucDomain = domain.toUpperCase();
        if (ucDomain.endsWith(".COM") || ucDomain.endsWith(".EDU") || ucDomain.endsWith(".NET") || ucDomain.endsWith(".GOV") || ucDomain.endsWith(".MIL") || ucDomain.endsWith(".ORG") || ucDomain.endsWith(".INT")) {
            return true;
        }
        return false;
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        } else {
            String host = origin.getHost();
            String domain = cookie.getDomain();
            if (domain == null) {
                return false;
            }
            return host.endsWith(domain);
        }
    }
}

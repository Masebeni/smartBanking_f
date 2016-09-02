package org.apache.http.impl.cookie;

import com.shane.smartbanking.BuildConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

public class RFC2109DomainHandler implements CookieAttributeHandler {
    public void parse(Cookie cookie, String value) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (value == null) {
            throw new MalformedCookieException("Missing value for domain attribute");
        } else if (value.trim().equals(BuildConfig.FLAVOR)) {
            throw new MalformedCookieException("Blank value for domain attribute");
        } else {
            cookie.setDomain(value);
            cookie.setDomainAttributeSpecified(true);
        }
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        } else {
            String host = origin.getHost();
            String domain = cookie.getDomain();
            if (domain == null) {
                throw new MalformedCookieException("Cookie domain may not be null");
            } else if (!domain.equals(host)) {
                if (domain.indexOf(46) == -1) {
                    throw new MalformedCookieException(new StringBuffer().append("Domain attribute \"").append(domain).append("\" does not match the host \"").append(host).append("\"").toString());
                } else if (domain.startsWith(".")) {
                    int dotIndex = domain.indexOf(46, 1);
                    if (dotIndex < 0 || dotIndex == domain.length() - 1) {
                        throw new MalformedCookieException(new StringBuffer().append("Domain attribute \"").append(domain).append("\" violates RFC 2109: domain must contain an embedded dot").toString());
                    }
                    host = host.toLowerCase();
                    if (!host.endsWith(domain)) {
                        throw new MalformedCookieException(new StringBuffer().append("Illegal domain attribute \"").append(domain).append("\". Domain of origin: \"").append(host).append("\"").toString());
                    } else if (host.substring(0, host.length() - domain.length()).indexOf(46) != -1) {
                        throw new MalformedCookieException(new StringBuffer().append("Domain attribute \"").append(domain).append("\" violates RFC 2109: host minus domain may not contain any dots").toString());
                    }
                } else {
                    throw new MalformedCookieException(new StringBuffer().append("Domain attribute \"").append(domain).append("\" violates RFC 2109: domain must start with a dot").toString());
                }
            }
        }
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
            if (host.equals(domain) || (domain.startsWith(".") && host.endsWith(domain))) {
                return true;
            }
            return false;
        }
    }
}

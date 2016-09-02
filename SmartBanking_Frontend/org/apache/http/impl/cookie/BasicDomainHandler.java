package org.apache.http.impl.cookie;

import com.shane.smartbanking.BuildConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

public class BasicDomainHandler implements CookieAttributeHandler {
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
            } else if (host.indexOf(".") >= 0) {
                if (!host.endsWith(domain)) {
                    if (domain.startsWith(".")) {
                        domain = domain.substring(1, domain.length());
                    }
                    if (!host.equals(domain)) {
                        throw new MalformedCookieException(new StringBuffer().append("Illegal domain attribute \"").append(domain).append("\". Domain of origin: \"").append(host).append("\"").toString());
                    }
                }
            } else if (!host.equals(domain)) {
                throw new MalformedCookieException(new StringBuffer().append("Illegal domain attribute \"").append(domain).append("\". Domain of origin: \"").append(host).append("\"").toString());
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
            if (host.equals(domain)) {
                return true;
            }
            if (!domain.startsWith(".")) {
                domain = new StringBuffer().append(".").append(domain).toString();
            }
            if (host.endsWith(domain) || host.equals(domain.substring(1))) {
                return true;
            }
            return false;
        }
    }
}

package org.apache.http.impl.cookie;

import com.shane.smartbanking.BuildConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

public class BasicPathHandler implements CookieAttributeHandler {
    public void parse(Cookie cookie, String value) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (value == null || value.trim().equals(BuildConfig.FLAVOR)) {
            value = "/";
        }
        cookie.setPath(value);
        cookie.setPathAttributeSpecified(true);
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        if (!match(cookie, origin)) {
            throw new MalformedCookieException(new StringBuffer().append("Illegal path attribute \"").append(cookie.getPath()).append("\". Path of origin: \"").append(origin.getPath()).append("\"").toString());
        }
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        } else {
            String targetpath = origin.getPath();
            String topmostPath = cookie.getPath();
            if (topmostPath == null) {
                topmostPath = "/";
            }
            if (topmostPath.length() > 1 && topmostPath.endsWith("/")) {
                topmostPath = topmostPath.substring(0, topmostPath.length() - 1);
            }
            boolean match = targetpath.startsWith(topmostPath);
            if (!match || targetpath.length() == topmostPath.length() || topmostPath.endsWith("/")) {
                return match;
            }
            if (targetpath.charAt(topmostPath.length()) == '/') {
                return true;
            }
            return false;
        }
    }
}

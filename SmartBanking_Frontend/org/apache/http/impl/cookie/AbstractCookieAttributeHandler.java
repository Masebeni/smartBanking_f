package org.apache.http.impl.cookie;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

public abstract class AbstractCookieAttributeHandler implements CookieAttributeHandler {
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        return true;
    }
}

package org.apache.http.cookie;

import org.apache.http.Header;

public interface CookieSpec {
    Header[] formatCookies(Cookie[] cookieArr);

    boolean match(Cookie cookie, CookieOrigin cookieOrigin);

    Cookie[] parse(Header header, CookieOrigin cookieOrigin) throws MalformedCookieException;

    void validate(Cookie cookie, CookieOrigin cookieOrigin) throws MalformedCookieException;
}

package org.apache.http.impl.cookie;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;

public class BasicCommentHandler extends AbstractCookieAttributeHandler {
    public void parse(Cookie cookie, String value) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        cookie.setComment(value);
    }
}

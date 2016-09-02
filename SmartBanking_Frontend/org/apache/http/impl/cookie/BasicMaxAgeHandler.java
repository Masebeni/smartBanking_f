package org.apache.http.impl.cookie;

import java.util.Date;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;

public class BasicMaxAgeHandler extends AbstractCookieAttributeHandler {
    public void parse(Cookie cookie, String value) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (value == null) {
            throw new MalformedCookieException("Missing value for max-age attribute");
        } else {
            try {
                cookie.setExpiryDate(new Date(System.currentTimeMillis() + (((long) Integer.parseInt(value)) * 1000)));
            } catch (NumberFormatException e) {
                throw new MalformedCookieException(new StringBuffer().append("Invalid max-age attribute: ").append(value).toString());
            }
        }
    }
}

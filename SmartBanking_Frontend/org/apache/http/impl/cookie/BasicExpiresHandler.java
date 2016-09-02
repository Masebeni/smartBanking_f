package org.apache.http.impl.cookie;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;

public class BasicExpiresHandler extends AbstractCookieAttributeHandler {
    private final String[] datepatterns;

    public BasicExpiresHandler(String[] datepatterns) {
        if (datepatterns == null) {
            throw new IllegalArgumentException("Array of date patterns may not be null");
        }
        this.datepatterns = datepatterns;
    }

    public void parse(Cookie cookie, String value) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (value == null) {
            throw new MalformedCookieException("Missing value for expires attribute");
        } else {
            try {
                cookie.setExpiryDate(DateUtils.parseDate(value, this.datepatterns));
            } catch (DateParseException e) {
                throw new MalformedCookieException(new StringBuffer().append("Unable to parse expires attribute: ").append(value).toString());
            }
        }
    }
}

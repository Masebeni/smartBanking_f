package org.apache.http.impl.cookie;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;

public class NetscapeDraftSpec extends CookieSpecBase {
    public NetscapeDraftSpec() {
        registerAttribHandler("path", new BasicPathHandler());
        registerAttribHandler("domain", new NetscapeDomainHandler());
        registerAttribHandler("max-age", new BasicMaxAgeHandler());
        registerAttribHandler("secure", new BasicSecureHandler());
        registerAttribHandler("comment", new BasicCommentHandler());
        registerAttribHandler("expires", new BasicExpiresHandler(new String[]{"EEE, dd-MMM-yyyy HH:mm:ss z"}));
    }

    public Cookie[] parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null");
        } else if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        } else {
            return parse(new HeaderElement[]{BasicHeaderElement.parse(header.getValue())}, origin);
        }
    }

    public Header[] formatCookies(Cookie[] cookies) {
        if (cookies == null) {
            throw new IllegalArgumentException("Cookie array may not be null");
        } else if (cookies.length == 0) {
            throw new IllegalArgumentException("Cookie array may not be empty");
        } else {
            CharArrayBuffer buffer = new CharArrayBuffer(cookies.length * 20);
            buffer.append(SM.COOKIE);
            buffer.append(": ");
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (i > 0) {
                    buffer.append("; ");
                }
                buffer.append(cookie.getName());
                String s = cookie.getValue();
                if (s != null) {
                    buffer.append("=");
                    buffer.append(s);
                }
            }
            return new Header[]{new BufferedHeader(buffer)};
        }
    }
}

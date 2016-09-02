package org.apache.http.impl.cookie;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HttpDateGenerator;
import org.apache.http.util.CharArrayBuffer;

public class BrowserCompatSpec extends CookieSpecBase {
    private static final String[] DATE_PATTERNS;
    private final String[] datepatterns;

    static {
        DATE_PATTERNS = new String[]{HttpDateGenerator.PATTERN_RFC1123, DateUtils.PATTERN_RFC1036, DateUtils.PATTERN_ASCTIME, "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z"};
    }

    public BrowserCompatSpec(String[] datepatterns) {
        if (datepatterns != null) {
            this.datepatterns = (String[]) datepatterns.clone();
        } else {
            this.datepatterns = DATE_PATTERNS;
        }
        registerAttribHandler("path", new BasicPathHandler());
        registerAttribHandler("domain", new BasicDomainHandler());
        registerAttribHandler("max-age", new BasicMaxAgeHandler());
        registerAttribHandler("secure", new BasicSecureHandler());
        registerAttribHandler("comment", new BasicCommentHandler());
        registerAttribHandler("expires", new BasicExpiresHandler(this.datepatterns));
    }

    public BrowserCompatSpec() {
        this(null);
    }

    public Cookie[] parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null");
        } else if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        } else {
            String headervalue = header.getValue();
            boolean isNetscapeCookie = false;
            int i1 = headervalue.toLowerCase().indexOf("expires=");
            if (i1 != -1) {
                i1 += "expires=".length();
                int i2 = headervalue.indexOf(";", i1);
                if (i2 == -1) {
                    i2 = headervalue.length();
                }
                try {
                    DateUtils.parseDate(headervalue.substring(i1, i2), this.datepatterns);
                    isNetscapeCookie = true;
                } catch (DateParseException e) {
                }
            }
            return parse(isNetscapeCookie ? new HeaderElement[]{BasicHeaderElement.parse(headervalue)} : header.getElements(), origin);
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
                buffer.append("=");
                String s = cookie.getValue();
                if (s != null) {
                    buffer.append(s);
                }
            }
            return new Header[]{new BufferedHeader(buffer)};
        }
    }
}

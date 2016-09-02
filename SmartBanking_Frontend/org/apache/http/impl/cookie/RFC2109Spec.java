package org.apache.http.impl.cookie;

import java.util.Arrays;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookiePathComparator;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HttpDateGenerator;
import org.apache.http.util.CharArrayBuffer;

public class RFC2109Spec extends CookieSpecBase {
    private static final String[] DATE_PATTERNS;
    private static final CookiePathComparator PATH_COMPARATOR;
    private final String[] datepatterns;
    private final boolean oneHeader;

    static {
        PATH_COMPARATOR = new CookiePathComparator();
        DATE_PATTERNS = new String[]{HttpDateGenerator.PATTERN_RFC1123, DateUtils.PATTERN_RFC1036, DateUtils.PATTERN_ASCTIME};
    }

    public RFC2109Spec(String[] datepatterns, boolean oneHeader) {
        if (datepatterns != null) {
            this.datepatterns = (String[]) datepatterns.clone();
        } else {
            this.datepatterns = DATE_PATTERNS;
        }
        this.oneHeader = oneHeader;
        registerAttribHandler("version", new RFC2109VersionHandler());
        registerAttribHandler("path", new BasicPathHandler());
        registerAttribHandler("domain", new RFC2109DomainHandler());
        registerAttribHandler("max-age", new BasicMaxAgeHandler());
        registerAttribHandler("secure", new BasicSecureHandler());
        registerAttribHandler("comment", new BasicCommentHandler());
        registerAttribHandler("expires", new BasicExpiresHandler(this.datepatterns));
    }

    public RFC2109Spec() {
        this(null, false);
    }

    public Cookie[] parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null");
        } else if (origin != null) {
            return parse(header.getElements(), origin);
        } else {
            throw new IllegalArgumentException("Cookie origin may not be null");
        }
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        String name = cookie.getName();
        if (name.indexOf(32) != -1) {
            throw new MalformedCookieException("Cookie name may not contain blanks");
        } else if (name.startsWith("$")) {
            throw new MalformedCookieException("Cookie name may not start with $");
        } else {
            super.validate(cookie, origin);
        }
    }

    public Header[] formatCookies(Cookie[] cookies) {
        if (cookies == null) {
            throw new IllegalArgumentException("Cookie array may not be null");
        } else if (cookies.length == 0) {
            throw new IllegalArgumentException("Cookie array may not be empty");
        } else {
            Arrays.sort(cookies, PATH_COMPARATOR);
            if (this.oneHeader) {
                return doFormatOneHeader(cookies);
            }
            return doFormatManyHeaders(cookies);
        }
    }

    private Header[] doFormatOneHeader(Cookie[] cookies) {
        int version = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (Cookie cookie : cookies) {
            if (cookie.getVersion() < version) {
                version = cookie.getVersion();
            }
        }
        CharArrayBuffer buffer = new CharArrayBuffer(cookies.length * 40);
        buffer.append(SM.COOKIE);
        buffer.append(": ");
        formatParamAsVer(buffer, "$Version", Integer.toString(version), version);
        for (Cookie cookie2 : cookies) {
            buffer.append("; ");
            formatCookieAsVer(buffer, cookie2, version);
        }
        return new Header[]{new BufferedHeader(buffer)};
    }

    private Header[] doFormatManyHeaders(Cookie[] cookies) {
        Header[] headers = new Header[cookies.length];
        for (int i = 0; i < cookies.length; i++) {
            int version = cookies[i].getVersion();
            CharArrayBuffer buffer = new CharArrayBuffer(40);
            buffer.append("Cookie: ");
            formatParamAsVer(buffer, "$Version", Integer.toString(version), version);
            buffer.append("; ");
            formatCookieAsVer(buffer, cookies[i], version);
            headers[i] = new BufferedHeader(buffer);
        }
        return headers;
    }

    private void formatParamAsVer(CharArrayBuffer buffer, String name, String value, int version) {
        buffer.append(name);
        buffer.append("=");
        if (value == null) {
            return;
        }
        if (version > 0) {
            buffer.append('\"');
            buffer.append(value);
            buffer.append('\"');
            return;
        }
        buffer.append(value);
    }

    private void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version) {
        formatParamAsVer(buffer, cookie.getName(), cookie.getValue(), version);
        if (cookie.getPath() != null && cookie.isPathAttributeSpecified()) {
            buffer.append("; ");
            formatParamAsVer(buffer, "$Path", cookie.getPath(), version);
        }
        if (cookie.getDomain() != null && cookie.isDomainAttributeSpecified()) {
            buffer.append("; ");
            formatParamAsVer(buffer, "$Domain", cookie.getDomain(), version);
        }
    }
}

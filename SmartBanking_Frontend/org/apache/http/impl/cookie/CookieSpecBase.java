package org.apache.http.impl.cookie;

import com.shane.smartbanking.BuildConfig;
import java.util.Iterator;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

public abstract class CookieSpecBase extends AbstractCookieSpec {
    protected static String getDefaultPath(CookieOrigin origin) {
        String defaultPath = origin.getPath();
        int lastSlashIndex = defaultPath.lastIndexOf(47);
        if (lastSlashIndex < 0) {
            return defaultPath;
        }
        if (lastSlashIndex == 0) {
            lastSlashIndex = 1;
        }
        return defaultPath.substring(0, lastSlashIndex);
    }

    protected static String getDefaultDomain(CookieOrigin origin) {
        return origin.getHost();
    }

    protected Cookie[] parse(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
        Cookie[] cookies = new Cookie[elems.length];
        for (int i = 0; i < elems.length; i++) {
            HeaderElement headerelement = elems[i];
            String name = headerelement.getName();
            String value = headerelement.getValue();
            if (name == null || name.equals(BuildConfig.FLAVOR)) {
                throw new MalformedCookieException("Cookie name may not be empty");
            }
            Cookie cookie = new Cookie(name, value);
            cookie.setPath(getDefaultPath(origin));
            cookie.setDomain(getDefaultDomain(origin));
            NameValuePair[] attribs = headerelement.getParameters();
            for (int j = attribs.length - 1; j >= 0; j--) {
                NameValuePair attrib = attribs[j];
                CookieAttributeHandler handler = findAttribHandler(attrib.getName().toLowerCase());
                if (handler != null) {
                    handler.parse(cookie, attrib.getValue());
                }
            }
            cookies[i] = cookie;
        }
        return cookies;
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        } else {
            Iterator i = getAttribHandlerIterator();
            while (i.hasNext()) {
                ((CookieAttributeHandler) i.next()).validate(cookie, origin);
            }
        }
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        } else if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        } else {
            Iterator i = getAttribHandlerIterator();
            while (i.hasNext()) {
                if (!((CookieAttributeHandler) i.next()).match(cookie, origin)) {
                    return false;
                }
            }
            return true;
        }
    }
}

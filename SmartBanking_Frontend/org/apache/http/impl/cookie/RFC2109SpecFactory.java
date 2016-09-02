package org.apache.http.impl.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.params.CookieSpecParams;
import org.apache.http.params.HttpParams;

public class RFC2109SpecFactory implements CookieSpecFactory {
    public CookieSpec newInstance(HttpParams params) {
        if (params != null) {
            return new RFC2109Spec((String[]) params.getParameter(CookieSpecParams.DATE_PATTERNS), params.getBooleanParameter(CookieSpecParams.SINGLE_COOKIE_HEADER, false));
        }
        return new RFC2109Spec();
    }
}

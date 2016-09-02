package org.apache.http.impl.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.params.CookieSpecParams;
import org.apache.http.params.HttpParams;

public class BrowserCompatSpecFactory implements CookieSpecFactory {
    public CookieSpec newInstance(HttpParams params) {
        if (params != null) {
            return new BrowserCompatSpec((String[]) params.getParameter(CookieSpecParams.DATE_PATTERNS));
        }
        return new BrowserCompatSpec();
    }
}

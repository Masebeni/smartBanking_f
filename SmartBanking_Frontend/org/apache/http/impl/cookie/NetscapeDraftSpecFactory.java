package org.apache.http.impl.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.params.HttpParams;

public class NetscapeDraftSpecFactory implements CookieSpecFactory {
    public CookieSpec newInstance(HttpParams params) {
        return new NetscapeDraftSpec();
    }
}

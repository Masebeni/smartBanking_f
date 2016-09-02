package org.apache.http.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

public class HttpState {
    private final Comparator cookieComparator;
    private final ArrayList cookies;
    private final HashMap credMap;

    public HttpState() {
        this.credMap = new HashMap();
        this.cookies = new ArrayList();
        this.cookieComparator = new CookieIdentityComparator();
    }

    public synchronized void addCookie(Cookie cookie) {
        if (cookie != null) {
            Iterator it = this.cookies.iterator();
            while (it.hasNext()) {
                if (this.cookieComparator.compare(cookie, (Cookie) it.next()) == 0) {
                    it.remove();
                    break;
                }
            }
            if (!cookie.isExpired(new Date())) {
                this.cookies.add(cookie);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void addCookies(org.apache.http.cookie.Cookie[] r3) {
        /*
        r2 = this;
        monitor-enter(r2);
        if (r3 == 0) goto L_0x000f;
    L_0x0003:
        r0 = 0;
    L_0x0004:
        r1 = r3.length;	 Catch:{ all -> 0x0011 }
        if (r0 >= r1) goto L_0x000f;
    L_0x0007:
        r1 = r3[r0];	 Catch:{ all -> 0x0011 }
        r2.addCookie(r1);	 Catch:{ all -> 0x0011 }
        r0 = r0 + 1;
        goto L_0x0004;
    L_0x000f:
        monitor-exit(r2);
        return;
    L_0x0011:
        r1 = move-exception;
        monitor-exit(r2);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.client.HttpState.addCookies(org.apache.http.cookie.Cookie[]):void");
    }

    public synchronized Cookie[] getCookies() {
        return (Cookie[]) this.cookies.toArray(new Cookie[this.cookies.size()]);
    }

    public synchronized boolean purgeExpiredCookies() {
        boolean removed;
        removed = false;
        Date now = new Date();
        Iterator it = this.cookies.iterator();
        while (it.hasNext()) {
            if (((Cookie) it.next()).isExpired(now)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    public synchronized void setCredentials(AuthScope authscope, Credentials credentials) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        this.credMap.put(authscope, credentials);
    }

    private static Credentials matchCredentials(HashMap map, AuthScope authscope) {
        Credentials creds = (Credentials) map.get(authscope);
        if (creds != null) {
            return creds;
        }
        int bestMatchFactor = -1;
        AuthScope bestMatch = null;
        for (AuthScope current : map.keySet()) {
            int factor = authscope.match(current);
            if (factor > bestMatchFactor) {
                bestMatchFactor = factor;
                bestMatch = current;
            }
        }
        if (bestMatch != null) {
            return (Credentials) map.get(bestMatch);
        }
        return creds;
    }

    public synchronized Credentials getCredentials(AuthScope authscope) {
        if (authscope == null) {
            throw new IllegalArgumentException("Authentication scope may not be null");
        }
        return matchCredentials(this.credMap, authscope);
    }

    public synchronized String toString() {
        StringBuffer buffer;
        buffer = new StringBuffer();
        buffer.append(this.credMap);
        buffer.append(this.cookies);
        return buffer.toString();
    }

    public synchronized void clearCredentials() {
        this.credMap.clear();
    }

    public synchronized void clearCookies() {
        this.cookies.clear();
    }

    public synchronized void clear() {
        clearCookies();
        clearCredentials();
    }
}

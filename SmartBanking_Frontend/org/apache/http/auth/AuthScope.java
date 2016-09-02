package org.apache.http.auth;

import org.apache.http.util.LangUtils;

public class AuthScope {
    public static final AuthScope ANY;
    public static final String ANY_HOST;
    public static final int ANY_PORT = -1;
    public static final String ANY_REALM;
    public static final String ANY_SCHEME;
    private String host;
    private int port;
    private String realm;
    private String scheme;

    static {
        ANY_HOST = null;
        ANY_REALM = null;
        ANY_SCHEME = null;
        ANY = new AuthScope(ANY_HOST, ANY_PORT, ANY_REALM, ANY_SCHEME);
    }

    public AuthScope(String host, int port, String realm, String scheme) {
        this.scheme = null;
        this.realm = null;
        this.host = null;
        this.port = ANY_PORT;
        this.host = host == null ? ANY_HOST : host.toLowerCase();
        if (port < 0) {
            port = ANY_PORT;
        }
        this.port = port;
        if (realm == null) {
            realm = ANY_REALM;
        }
        this.realm = realm;
        this.scheme = scheme == null ? ANY_SCHEME : scheme.toUpperCase();
    }

    public AuthScope(String host, int port, String realm) {
        this(host, port, realm, ANY_SCHEME);
    }

    public AuthScope(String host, int port) {
        this(host, port, ANY_REALM, ANY_SCHEME);
    }

    public AuthScope(AuthScope authscope) {
        this.scheme = null;
        this.realm = null;
        this.host = null;
        this.port = ANY_PORT;
        if (authscope == null) {
            throw new IllegalArgumentException("Scope may not be null");
        }
        this.host = authscope.getHost();
        this.port = authscope.getPort();
        this.realm = authscope.getRealm();
        this.scheme = authscope.getScheme();
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getRealm() {
        return this.realm;
    }

    public String getScheme() {
        return this.scheme;
    }

    public int match(AuthScope that) {
        int factor = 0;
        if (LangUtils.equals(this.scheme, that.scheme)) {
            factor = 0 + 1;
        } else if (!(this.scheme == ANY_SCHEME || that.scheme == ANY_SCHEME)) {
            return ANY_PORT;
        }
        if (LangUtils.equals(this.realm, that.realm)) {
            factor += 2;
        } else if (!(this.realm == ANY_REALM || that.realm == ANY_REALM)) {
            return ANY_PORT;
        }
        if (this.port == that.port) {
            factor += 4;
        } else if (!(this.port == ANY_PORT || that.port == ANY_PORT)) {
            return ANY_PORT;
        }
        if (LangUtils.equals(this.host, that.host)) {
            factor += 8;
        } else if (!(this.host == ANY_HOST || that.host == ANY_HOST)) {
            return ANY_PORT;
        }
        return factor;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthScope)) {
            return super.equals(o);
        }
        AuthScope that = (AuthScope) o;
        if (LangUtils.equals(this.host, that.host) && this.port == that.port && LangUtils.equals(this.realm, that.realm) && LangUtils.equals(this.scheme, that.scheme)) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (this.scheme != null) {
            buffer.append(this.scheme.toUpperCase());
            buffer.append(' ');
        }
        if (this.realm != null) {
            buffer.append('\'');
            buffer.append(this.realm);
            buffer.append('\'');
        } else {
            buffer.append("<any realm>");
        }
        if (this.host != null) {
            buffer.append('@');
            buffer.append(this.host);
            if (this.port >= 0) {
                buffer.append(':');
                buffer.append(this.port);
            }
        }
        return buffer.toString();
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(17, this.host), this.port), this.realm), this.scheme);
    }
}

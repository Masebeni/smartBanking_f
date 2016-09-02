package org.apache.http;

import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.LangUtils;

public class HttpHost {
    public static final String DEFAULT_SCHEME_NAME = "http";
    private String hostname;
    private int port;
    private String schemeName;

    public HttpHost(String hostname, int port, String schemeName) {
        this.hostname = null;
        this.port = -1;
        this.schemeName = null;
        if (hostname == null) {
            throw new IllegalArgumentException("Host name may not be null");
        }
        this.hostname = hostname;
        if (schemeName != null) {
            this.schemeName = schemeName.toLowerCase();
        } else {
            this.schemeName = DEFAULT_SCHEME_NAME;
        }
        this.port = port;
    }

    public HttpHost(String hostname, int port) {
        this(hostname, port, null);
    }

    public HttpHost(String hostname) {
        this(hostname, -1, null);
    }

    public HttpHost(HttpHost httphost) {
        this.hostname = null;
        this.port = -1;
        this.schemeName = null;
        this.hostname = httphost.hostname;
        this.port = httphost.port;
        this.schemeName = httphost.schemeName;
    }

    public String getHostName() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public String getSchemeName() {
        return this.schemeName;
    }

    public String toURI() {
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        buffer.append(this.schemeName);
        buffer.append("://");
        buffer.append(this.hostname);
        if (this.port != -1) {
            buffer.append(':');
            buffer.append(Integer.toString(this.port));
        }
        return buffer.toString();
    }

    public String toHostString() {
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        buffer.append(this.hostname);
        if (this.port != -1) {
            buffer.append(':');
            buffer.append(Integer.toString(this.port));
        }
        return buffer.toString();
    }

    public String toString() {
        return toURI();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HttpHost)) {
            return false;
        }
        HttpHost that = (HttpHost) obj;
        if (!(this.hostname.equalsIgnoreCase(that.hostname) && this.port == that.port && this.schemeName.equals(that.schemeName))) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(17, this.hostname.toUpperCase()), this.port), this.schemeName);
    }
}

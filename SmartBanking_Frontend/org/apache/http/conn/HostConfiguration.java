package org.apache.http.conn;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.util.LangUtils;

public class HostConfiguration {
    public static final HostConfiguration ANY_HOST_CONFIGURATION;
    private final InetAddress localAddress;
    private final HttpHost proxyHost;
    private final HttpHost targetHost;

    static {
        ANY_HOST_CONFIGURATION = new HostConfiguration();
    }

    public HostConfiguration(HttpHost host, HttpHost proxy, InetAddress laddr) {
        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        this.targetHost = host;
        this.proxyHost = proxy;
        this.localAddress = laddr;
    }

    private HostConfiguration() {
        this.targetHost = null;
        this.proxyHost = null;
        this.localAddress = null;
    }

    public String toString() {
        StringBuffer b = new StringBuffer(50);
        b.append("HostConfiguration[");
        if (this.targetHost != null) {
            b.append("host=").append(this.targetHost);
        } else {
            b.append("host=*any*");
        }
        if (this.proxyHost != null) {
            b.append(", ").append("proxyHost=").append(this.proxyHost);
        }
        if (this.localAddress != null) {
            b.append(", ").append("localAddress=").append(this.localAddress);
        }
        b.append("]");
        return b.toString();
    }

    public HttpHost getHost() {
        return this.targetHost;
    }

    public HttpHost getProxyHost() {
        return this.proxyHost;
    }

    public InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public boolean equals(Object o) {
        if (!(o instanceof HostConfiguration)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        HostConfiguration that = (HostConfiguration) o;
        if (LangUtils.equals(this.targetHost, that.targetHost) && LangUtils.equals(this.proxyHost, that.proxyHost) && LangUtils.equals(this.localAddress, that.localAddress)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(17, this.targetHost), this.proxyHost), this.localAddress);
    }
}

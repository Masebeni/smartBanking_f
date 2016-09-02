package org.apache.http.conn;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.util.CharArrayBuffer;

public final class HttpRoute implements Cloneable {
    private final boolean layered;
    private final InetAddress localAddress;
    private final HttpHost[] proxyChain;
    private final boolean secure;
    private final HttpHost targetHost;
    private final boolean tunnelled;

    private HttpRoute(InetAddress local, HttpHost target, HttpHost[] proxies, boolean secure, boolean tunnelled, boolean layered) {
        if (target == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        } else if (tunnelled && proxies == null) {
            throw new IllegalArgumentException("Proxy required if tunnelled.");
        } else {
            this.targetHost = target;
            this.localAddress = local;
            this.proxyChain = proxies;
            this.secure = secure;
            this.tunnelled = tunnelled;
            this.layered = layered;
        }
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost[] proxies, boolean secure, boolean tunnelled, boolean layered) {
        this(local, target, toChain(proxies), secure, tunnelled, layered);
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure, boolean tunnelled, boolean layered) {
        this(local, target, toChain(proxy), secure, tunnelled, layered);
    }

    public HttpRoute(HttpHost target, InetAddress local, boolean secure) {
        this(local, target, null, secure, false, false);
    }

    public HttpRoute(HttpHost target) {
        this(null, target, null, false, false, false);
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure) {
        this(local, target, toChain(proxy), secure, secure, secure);
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy host may not be null.");
        }
    }

    private static HttpHost[] toChain(HttpHost proxy) {
        if (proxy == null) {
            return null;
        }
        return new HttpHost[]{proxy};
    }

    private static HttpHost[] toChain(HttpHost[] proxies) {
        if (proxies == null || proxies.length < 1) {
            return null;
        }
        for (HttpHost httpHost : proxies) {
            if (httpHost == null) {
                throw new IllegalArgumentException("Proxy chain may not contain null elements.");
            }
        }
        HttpHost[] result = new HttpHost[proxies.length];
        System.arraycopy(proxies, 0, result, 0, proxies.length);
        return result;
    }

    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    public final InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public final int getHopCount() {
        return this.proxyChain == null ? 1 : this.proxyChain.length + 1;
    }

    public final HttpHost getHopTarget(int hop) {
        if (hop < 0) {
            throw new IllegalArgumentException(new StringBuffer().append("Hop index must not be negative: ").append(hop).toString());
        }
        int hopcount = getHopCount();
        if (hop >= hopcount) {
            throw new IllegalArgumentException(new StringBuffer().append("Hop index ").append(hop).append(" exceeds route length ").append(hopcount).append(".").toString());
        } else if (hop < hopcount - 1) {
            return this.proxyChain[hop];
        } else {
            return this.targetHost;
        }
    }

    public final HttpHost getProxyHost() {
        return this.proxyChain == null ? null : this.proxyChain[0];
    }

    public final boolean isTunnelled() {
        return this.tunnelled;
    }

    public final boolean isLayered() {
        return this.layered;
    }

    public final boolean isSecure() {
        return this.secure;
    }

    public final HostConfiguration toHostConfig() {
        if (this.proxyChain == null || this.proxyChain.length <= 1) {
            return new HostConfiguration(this.targetHost, getProxyHost(), this.localAddress);
        }
        throw new IllegalStateException("Cannot convert proxy chain.");
    }

    public final boolean equals(Object o) {
        int i = 1;
        if (o == this) {
            return true;
        }
        if (!(o instanceof HttpRoute)) {
            return false;
        }
        int i2;
        HttpRoute that = (HttpRoute) o;
        boolean equal = this.targetHost.equals(that.targetHost);
        if (this.localAddress == that.localAddress || (this.localAddress != null && this.localAddress.equals(that.localAddress))) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        equal &= i2;
        if (this.proxyChain == that.proxyChain || !(this.proxyChain == null || that.proxyChain == null || this.proxyChain.length != that.proxyChain.length)) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        equal &= i2;
        if (!(this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered)) {
            i = 0;
        }
        equal &= i;
        if (equal && this.proxyChain != null) {
            int i3 = 0;
            while (equal && i3 < this.proxyChain.length) {
                equal = this.proxyChain[i3].equals(that.proxyChain[i3]);
                i3++;
            }
        }
        return equal;
    }

    public final int hashCode() {
        int hc = this.targetHost.hashCode();
        if (this.localAddress != null) {
            hc ^= this.localAddress.hashCode();
        }
        if (this.proxyChain != null) {
            hc ^= this.proxyChain.length;
            for (HttpHost hashCode : this.proxyChain) {
                hc ^= hashCode.hashCode();
            }
        }
        if (this.secure) {
            hc ^= 286331153;
        }
        if (this.tunnelled) {
            hc ^= 572662306;
        }
        if (this.layered) {
            return hc ^ 1145324612;
        }
        return hc;
    }

    public final String toString() {
        CharArrayBuffer cab = new CharArrayBuffer((getHopCount() * 30) + 50);
        cab.append("HttpRoute[");
        if (this.localAddress != null) {
            cab.append(this.localAddress);
            cab.append("->");
        }
        cab.append('{');
        if (this.tunnelled) {
            cab.append('t');
        }
        if (this.layered) {
            cab.append('l');
        }
        if (this.secure) {
            cab.append('s');
        }
        cab.append("}->");
        if (this.proxyChain != null) {
            for (Object append : this.proxyChain) {
                cab.append(append);
                cab.append("->");
            }
        }
        cab.append(this.targetHost);
        cab.append(']');
        return cab.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

package org.apache.http.conn;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.util.CharArrayBuffer;

public final class RouteTracker implements Cloneable {
    private boolean connected;
    private boolean layered;
    private final InetAddress localAddress;
    private HttpHost[] proxyChain;
    private boolean secure;
    private final HttpHost targetHost;
    private boolean tunnelled;

    public RouteTracker(HttpHost target, InetAddress local) {
        if (target == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        this.targetHost = target;
        this.localAddress = local;
    }

    public RouteTracker(HttpRoute route) {
        this(route.getTargetHost(), route.getLocalAddress());
    }

    public final void connectTarget(boolean secure) {
        if (this.connected) {
            throw new IllegalStateException("Already connected.");
        }
        this.connected = true;
        this.secure = secure;
    }

    public final void connectProxy(HttpHost proxy, boolean secure) {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy host may not be null.");
        } else if (this.connected) {
            throw new IllegalStateException("Already connected.");
        } else {
            this.connected = true;
            this.proxyChain = new HttpHost[]{proxy};
            this.secure = secure;
        }
    }

    public final void tunnelTarget(boolean secure) {
        if (!this.connected) {
            throw new IllegalStateException("No tunnel unless connected.");
        } else if (this.proxyChain == null) {
            throw new IllegalStateException("No tunnel without proxy.");
        } else {
            this.tunnelled = true;
            this.secure = secure;
        }
    }

    public final void tunnelProxy(HttpHost proxy, boolean secure) {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy host may not be null.");
        } else if (!this.connected) {
            throw new IllegalStateException("No tunnel unless connected.");
        } else if (this.proxyChain == null) {
            throw new IllegalStateException("No proxy tunnel without proxy.");
        } else {
            HttpHost[] proxies = new HttpHost[(this.proxyChain.length + 1)];
            System.arraycopy(this.proxyChain, 0, proxies, 0, this.proxyChain.length);
            proxies[proxies.length - 1] = proxy;
            this.proxyChain = proxies;
            this.secure = secure;
        }
    }

    public final void layerProtocol(boolean secure) {
        if (this.connected) {
            this.layered = true;
            this.secure = secure;
            return;
        }
        throw new IllegalStateException("No layered protocol unless connected.");
    }

    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    public final InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public final int getHopCount() {
        if (!this.connected) {
            return 0;
        }
        if (this.proxyChain == null) {
            return 1;
        }
        return this.proxyChain.length + 1;
    }

    public final HttpHost getHopTarget(int hop) {
        if (hop < 0) {
            throw new IllegalArgumentException(new StringBuffer().append("Hop index must not be negative: ").append(hop).toString());
        }
        int hopcount = getHopCount();
        if (hop >= hopcount) {
            throw new IllegalArgumentException(new StringBuffer().append("Hop index ").append(hop).append(" exceeds tracked route length ").append(hopcount).append(".").toString());
        } else if (hop < hopcount - 1) {
            return this.proxyChain[hop];
        } else {
            return this.targetHost;
        }
    }

    public final HttpHost getProxyHost() {
        return this.proxyChain == null ? null : this.proxyChain[0];
    }

    public final boolean isConnected() {
        return this.connected;
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

    public final HttpRoute toRoute() {
        return !this.connected ? null : new HttpRoute(this.targetHost, this.localAddress, this.proxyChain, this.secure, this.tunnelled, this.layered);
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
        if (!(o instanceof RouteTracker)) {
            return false;
        }
        int i2;
        RouteTracker that = (RouteTracker) o;
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
        if (!(this.connected == that.connected && this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered)) {
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
        if (this.connected) {
            hc ^= 286331153;
        }
        if (this.secure) {
            hc ^= 572662306;
        }
        if (this.tunnelled) {
            hc ^= 1145324612;
        }
        if (this.layered) {
            return hc ^ -2004318072;
        }
        return hc;
    }

    public final String toString() {
        CharArrayBuffer cab = new CharArrayBuffer((getHopCount() * 30) + 50);
        cab.append("RouteTracker[");
        if (this.localAddress != null) {
            cab.append(this.localAddress);
            cab.append("->");
        }
        cab.append('{');
        if (this.connected) {
            cab.append('c');
        }
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

package org.apache.http.conn;

import android.support.v4.internal.view.SupportMenu;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.LangUtils;

public final class Scheme {
    private final int defaultPort;
    private final boolean layered;
    private final String name;
    private final SocketFactory socketFactory;
    private String stringRep;

    public Scheme(String name, SocketFactory factory, int port) {
        if (name == null) {
            throw new IllegalArgumentException("Scheme name may not be null");
        } else if (factory == null) {
            throw new IllegalArgumentException("Socket factory may not be null");
        } else if (port <= 0 || port > SupportMenu.USER_MASK) {
            throw new IllegalArgumentException(new StringBuffer().append("Port is invalid: ").append(port).toString());
        } else {
            this.name = name.toLowerCase();
            this.socketFactory = factory;
            this.defaultPort = port;
            this.layered = factory instanceof SecureSocketFactory;
        }
    }

    public final int getDefaultPort() {
        return this.defaultPort;
    }

    public final SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public final String getName() {
        return this.name;
    }

    public final boolean isLayered() {
        return this.layered;
    }

    public final int resolvePort(int port) {
        return (port <= 0 || port > SupportMenu.USER_MASK) ? this.defaultPort : port;
    }

    public final String toString() {
        if (this.stringRep == null) {
            CharArrayBuffer buffer = new CharArrayBuffer(32);
            buffer.append(this.name);
            buffer.append(':');
            buffer.append(Integer.toString(this.defaultPort));
            this.stringRep = buffer.toString();
        }
        return this.stringRep;
    }

    public final boolean equals(Object obj) {
        boolean z = true;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Scheme)) {
            return false;
        }
        Scheme s = (Scheme) obj;
        if (!(this.name.equals(s.name) && this.defaultPort == s.defaultPort && this.layered == s.layered && this.socketFactory.equals(s.socketFactory))) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(LangUtils.hashCode(17, this.defaultPort), this.name), this.layered), this.socketFactory);
    }
}

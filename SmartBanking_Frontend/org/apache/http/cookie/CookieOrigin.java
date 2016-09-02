package org.apache.http.cookie;

import com.shane.smartbanking.BuildConfig;
import org.apache.http.util.CharArrayBuffer;

public final class CookieOrigin {
    private final String host;
    private final String path;
    private final int port;
    private final boolean secure;

    public CookieOrigin(String host, int port, String path, boolean secure) {
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        } else if (host.trim().equals(BuildConfig.FLAVOR)) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        } else if (port < 0) {
            throw new IllegalArgumentException(new StringBuffer().append("Invalid port: ").append(port).toString());
        } else if (path == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        } else {
            this.host = host.toLowerCase();
            this.port = port;
            if (path.trim().equals(BuildConfig.FLAVOR)) {
                this.path = "/";
            } else {
                this.path = path;
            }
            this.secure = secure;
        }
    }

    public String getHost() {
        return this.host;
    }

    public String getPath() {
        return this.path;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        buffer.append("[");
        if (this.secure) {
            buffer.append("(secure)");
        }
        buffer.append(this.host);
        buffer.append(":");
        buffer.append(Integer.toString(this.port));
        buffer.append(this.path);
        buffer.append("]");
        return buffer.toString();
    }
}

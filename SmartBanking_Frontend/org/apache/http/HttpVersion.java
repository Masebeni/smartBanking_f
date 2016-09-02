package org.apache.http;

import java.io.Serializable;
import org.apache.http.util.CharArrayBuffer;

public final class HttpVersion implements Comparable, Serializable {
    public static final HttpVersion HTTP_0_9;
    public static final HttpVersion HTTP_1_0;
    public static final HttpVersion HTTP_1_1;
    static final long serialVersionUID = -3164547215216382904L;
    private int major;
    private int minor;

    static {
        HTTP_0_9 = new HttpVersion(0, 9);
        HTTP_1_0 = new HttpVersion(1, 0);
        HTTP_1_1 = new HttpVersion(1, 1);
    }

    public HttpVersion(int major, int minor) {
        this.major = 0;
        this.minor = 0;
        if (major < 0) {
            throw new IllegalArgumentException("HTTP major version number may not be negative");
        }
        this.major = major;
        if (minor < 0) {
            throw new IllegalArgumentException("HTTP minor version number may not be negative");
        }
        this.minor = minor;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int hashCode() {
        return (this.major * 100000) + this.minor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HttpVersion) {
            return equals((HttpVersion) obj);
        }
        return false;
    }

    public int compareTo(HttpVersion anotherVer) {
        if (anotherVer == null) {
            throw new IllegalArgumentException("Version parameter may not be null");
        }
        int delta = getMajor() - anotherVer.getMajor();
        if (delta == 0) {
            return getMinor() - anotherVer.getMinor();
        }
        return delta;
    }

    public int compareTo(Object o) {
        return compareTo((HttpVersion) o);
    }

    public boolean equals(HttpVersion version) {
        return compareTo(version) == 0;
    }

    public boolean greaterEquals(HttpVersion version) {
        return compareTo(version) >= 0;
    }

    public boolean lessEquals(HttpVersion version) {
        return compareTo(version) <= 0;
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(16);
        buffer.append("HTTP/");
        buffer.append(Integer.toString(this.major));
        buffer.append('.');
        buffer.append(Integer.toString(this.minor));
        return buffer.toString();
    }
}

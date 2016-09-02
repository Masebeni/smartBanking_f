package org.apache.http.cookie;

import java.util.Date;
import org.apache.http.util.CharArrayBuffer;

public class Cookie {
    private String cookieComment;
    private String cookieDomain;
    private Date cookieExpiryDate;
    private String cookiePath;
    private int cookieVersion;
    private boolean hasDomainAttribute;
    private boolean hasPathAttribute;
    private boolean isSecure;
    private final String name;
    private final String value;

    public Cookie(String name, String value) {
        this.hasPathAttribute = false;
        this.hasDomainAttribute = false;
        this.cookieVersion = 0;
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getComment() {
        return this.cookieComment;
    }

    public void setComment(String comment) {
        this.cookieComment = comment;
    }

    public Date getExpiryDate() {
        return this.cookieExpiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.cookieExpiryDate = expiryDate;
    }

    public boolean isPersistent() {
        return this.cookieExpiryDate != null;
    }

    public String getDomain() {
        return this.cookieDomain;
    }

    public void setDomain(String domain) {
        if (domain != null) {
            this.cookieDomain = domain.toLowerCase();
        } else {
            this.cookieDomain = null;
        }
    }

    public String getPath() {
        return this.cookiePath;
    }

    public void setPath(String path) {
        this.cookiePath = path;
    }

    public boolean isSecure() {
        return this.isSecure;
    }

    public void setSecure(boolean secure) {
        this.isSecure = secure;
    }

    public int getVersion() {
        return this.cookieVersion;
    }

    public void setVersion(int version) {
        this.cookieVersion = version;
    }

    public boolean isExpired(Date date) {
        if (date != null) {
            return this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= date.getTime();
        } else {
            throw new IllegalArgumentException("Date may not be null");
        }
    }

    public void setPathAttributeSpecified(boolean value) {
        this.hasPathAttribute = value;
    }

    public boolean isPathAttributeSpecified() {
        return this.hasPathAttribute;
    }

    public void setDomainAttributeSpecified(boolean value) {
        this.hasDomainAttribute = value;
    }

    public boolean isDomainAttributeSpecified() {
        return this.hasDomainAttribute;
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        buffer.append("[version: ");
        buffer.append(Integer.toString(this.cookieVersion));
        buffer.append("]");
        buffer.append("[name: ");
        buffer.append(this.name);
        buffer.append("]");
        buffer.append("[name: ");
        buffer.append(this.value);
        buffer.append("]");
        buffer.append("[domain: ");
        buffer.append(this.cookieDomain);
        buffer.append("]");
        buffer.append("[path: ");
        buffer.append(this.cookiePath);
        buffer.append("]");
        buffer.append("[expiry: ");
        buffer.append(this.cookieExpiryDate);
        buffer.append("]");
        return buffer.toString();
    }
}

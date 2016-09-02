package org.apache.http.impl.auth;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

public abstract class RFC2617Scheme implements AuthScheme {
    private Map params;
    private boolean proxy;

    public RFC2617Scheme() {
        this.params = null;
    }

    public void processChallenge(Header header) throws MalformedChallengeException {
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null");
        }
        CharArrayBuffer buffer;
        int pos;
        String s;
        String authheader = header.getName();
        if (authheader.equalsIgnoreCase(AUTH.WWW_AUTH)) {
            this.proxy = false;
        } else if (authheader.equalsIgnoreCase(AUTH.PROXY_AUTH)) {
            this.proxy = true;
        } else {
            throw new MalformedChallengeException(new StringBuffer().append("Unexpected header name: ").append(authheader).toString());
        }
        if (header instanceof BufferedHeader) {
            buffer = ((BufferedHeader) header).getBuffer();
            pos = ((BufferedHeader) header).getValuePos();
        } else {
            s = header.getValue();
            if (s == null) {
                throw new MalformedChallengeException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            pos = 0;
        }
        while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
            pos++;
        }
        int beginIndex = pos;
        while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
            pos++;
        }
        s = buffer.substring(beginIndex, pos);
        if (s.equalsIgnoreCase(getSchemeName())) {
            HeaderElement[] elements = BasicHeaderElement.parseAll(buffer, pos, buffer.length());
            if (elements.length == 0) {
                throw new MalformedChallengeException("Authentication challenge is empty");
            }
            this.params = new HashMap(elements.length);
            for (HeaderElement element : elements) {
                this.params.put(element.getName(), element.getValue());
            }
            return;
        }
        throw new MalformedChallengeException(new StringBuffer().append("Invalid scheme identifier: ").append(s).toString());
    }

    protected Map getParameters() {
        return this.params;
    }

    public String getParameter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        } else if (this.params == null) {
            return null;
        } else {
            return (String) this.params.get(name.toLowerCase());
        }
    }

    public String getRealm() {
        return getParameter("realm");
    }

    public boolean isProxy() {
        return this.proxy;
    }
}

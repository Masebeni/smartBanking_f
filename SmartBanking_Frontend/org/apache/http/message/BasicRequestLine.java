package org.apache.http.message;

import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

public class BasicRequestLine implements RequestLine {
    private final HttpVersion httpversion;
    private final String method;
    private final String uri;

    public BasicRequestLine(String method, String uri, HttpVersion httpversion) {
        if (method == null) {
            throw new IllegalArgumentException("Method may not be null");
        } else if (uri == null) {
            throw new IllegalArgumentException("URI may not be null");
        } else if (httpversion == null) {
            throw new IllegalArgumentException("HTTP version may not be null");
        } else {
            this.method = method;
            this.uri = uri;
            this.httpversion = httpversion;
        }
    }

    public String getMethod() {
        return this.method;
    }

    public HttpVersion getHttpVersion() {
        return this.httpversion;
    }

    public String getUri() {
        return this.uri;
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        buffer.append(this.method);
        buffer.append(' ');
        buffer.append(this.uri);
        buffer.append(' ');
        buffer.append(this.httpversion);
        return buffer.toString();
    }

    public static RequestLine parse(CharArrayBuffer buffer, int indexFrom, int indexTo) throws ProtocolException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (indexFrom < 0) {
            throw new IndexOutOfBoundsException();
        } else if (indexTo > buffer.length()) {
            throw new IndexOutOfBoundsException();
        } else if (indexFrom > indexTo) {
            throw new IndexOutOfBoundsException();
        } else {
            int i = indexFrom;
            while (HTTP.isWhitespace(buffer.charAt(i))) {
                try {
                    i++;
                } catch (IndexOutOfBoundsException e) {
                    throw new ProtocolException(new StringBuffer().append("Invalid request line: ").append(buffer.substring(indexFrom, indexTo)).toString());
                }
            }
            int blank = buffer.indexOf(32, i, indexTo);
            if (blank < 0) {
                throw new ProtocolException(new StringBuffer().append("Invalid request line: ").append(buffer.substring(indexFrom, indexTo)).toString());
            }
            String method = buffer.substringTrimmed(i, blank);
            i = blank;
            while (HTTP.isWhitespace(buffer.charAt(i))) {
                i++;
            }
            blank = buffer.indexOf(32, i, indexTo);
            if (blank >= 0) {
                return new BasicRequestLine(method, buffer.substringTrimmed(i, blank), BasicHttpVersionFormat.parse(buffer, blank, indexTo));
            }
            throw new ProtocolException(new StringBuffer().append("Invalid request line: ").append(buffer.substring(indexFrom, indexTo)).toString());
        }
    }

    public static final RequestLine parse(String s) throws ProtocolException {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, 0, buffer.length());
    }

    public static void format(CharArrayBuffer buffer, RequestLine requestline) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        } else {
            buffer.append(requestline.getMethod());
            buffer.append(' ');
            buffer.append(requestline.getUri());
            buffer.append(' ');
            BasicHttpVersionFormat.format(buffer, requestline.getHttpVersion());
        }
    }

    public static String format(RequestLine requestline) {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        format(buffer, requestline);
        return buffer.toString();
    }
}

package org.apache.http.message;

import com.shane.smartbanking.BuildConfig;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

public class BasicStatusLine implements StatusLine {
    private final HttpVersion httpVersion;
    private final String reasonPhrase;
    private final int statusCode;

    public BasicStatusLine(HttpVersion httpVersion, int statusCode, String reasonPhrase) {
        if (httpVersion == null) {
            throw new IllegalArgumentException("HTTP version may not be null.");
        } else if (statusCode < 0) {
            throw new IllegalArgumentException("Status code may not be negative.");
        } else {
            this.httpVersion = httpVersion;
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
        }
    }

    public static StatusLine parse(CharArrayBuffer buffer, int indexFrom, int indexTo) throws ProtocolException {
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
                } catch (NumberFormatException e) {
                    throw new ProtocolException(new StringBuffer().append("Unable to parse status code from status line: ").append(buffer.substring(indexFrom, indexTo)).toString());
                } catch (IndexOutOfBoundsException e2) {
                    throw new ProtocolException(new StringBuffer().append("Invalid status line: ").append(buffer.substring(indexFrom, indexTo)).toString());
                }
            }
            int blank = buffer.indexOf(32, i, indexTo);
            if (blank <= 0) {
                throw new ProtocolException(new StringBuffer().append("Unable to parse HTTP-Version from the status line: ").append(buffer.substring(indexFrom, indexTo)).toString());
            }
            String reasonPhrase;
            HttpVersion ver = BasicHttpVersionFormat.parse(buffer, i, blank);
            i = blank;
            while (HTTP.isWhitespace(buffer.charAt(i))) {
                i++;
            }
            blank = buffer.indexOf(32, i, indexTo);
            if (blank < 0) {
                blank = indexTo;
            }
            int statusCode = Integer.parseInt(buffer.substringTrimmed(i, blank));
            i = blank;
            if (i < indexTo) {
                reasonPhrase = buffer.substringTrimmed(i, indexTo);
            } else {
                reasonPhrase = BuildConfig.FLAVOR;
            }
            return new BasicStatusLine(ver, statusCode, reasonPhrase);
        }
    }

    public static final StatusLine parse(String s) throws ProtocolException {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, 0, buffer.length());
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public HttpVersion getHttpVersion() {
        return this.httpVersion;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        buffer.append(this.httpVersion);
        buffer.append(' ');
        buffer.append(Integer.toString(this.statusCode));
        if (this.reasonPhrase != null && this.reasonPhrase.length() > 0) {
            buffer.append(' ');
            buffer.append(this.reasonPhrase);
        }
        return buffer.toString();
    }

    public static void format(CharArrayBuffer buffer, StatusLine statusline) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (statusline == null) {
            throw new IllegalArgumentException("Status line may not be null");
        } else {
            BasicHttpVersionFormat.format(buffer, statusline.getHttpVersion());
            buffer.append(' ');
            buffer.append(Integer.toString(statusline.getStatusCode()));
            buffer.append(' ');
            if (statusline.getReasonPhrase() != null) {
                buffer.append(statusline.getReasonPhrase());
            }
        }
    }

    public static String format(StatusLine statusline) {
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        format(buffer, statusline);
        return buffer.toString();
    }
}

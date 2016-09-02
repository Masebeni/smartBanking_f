package org.apache.http.message;

import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

public class BasicHttpVersionFormat {
    public static HttpVersion parse(CharArrayBuffer buffer, int indexFrom, int indexTo) throws ProtocolException {
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
                    throw new ProtocolException(new StringBuffer().append("Invalid HTTP minor version number: ").append(buffer.substring(indexFrom, indexTo)).toString());
                } catch (NumberFormatException e2) {
                    throw new ProtocolException(new StringBuffer().append("Invalid HTTP major version number: ").append(buffer.substring(indexFrom, indexTo)).toString());
                } catch (IndexOutOfBoundsException e3) {
                    throw new ProtocolException(new StringBuffer().append("Invalid HTTP version string: ").append(buffer.substring(indexFrom, indexTo)).toString());
                }
            }
            if (buffer.charAt(i) == 'H' && buffer.charAt(i + 1) == 'T' && buffer.charAt(i + 2) == 'T' && buffer.charAt(i + 3) == 'P' && buffer.charAt(i + 4) == '/') {
                i += 5;
                int period = buffer.indexOf(46, i, indexTo);
                if (period != -1) {
                    return new HttpVersion(Integer.parseInt(buffer.substringTrimmed(i, period)), Integer.parseInt(buffer.substringTrimmed(period + 1, indexTo)));
                }
                throw new ProtocolException(new StringBuffer().append("Invalid HTTP version number: ").append(buffer.substring(indexFrom, indexTo)).toString());
            }
            throw new ProtocolException(new StringBuffer().append("Not a valid HTTP version string: ").append(buffer.substring(indexFrom, indexTo)).toString());
        }
    }

    public static final HttpVersion parse(String s) throws ProtocolException {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, 0, buffer.length());
    }

    public static void format(CharArrayBuffer buffer, HttpVersion ver) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (ver == null) {
            throw new IllegalArgumentException("Version may not be null");
        } else {
            buffer.append("HTTP/");
            buffer.append(Integer.toString(ver.getMajor()));
            buffer.append('.');
            buffer.append(Integer.toString(ver.getMinor()));
        }
    }

    public static String format(HttpVersion ver) {
        CharArrayBuffer buffer = new CharArrayBuffer(16);
        format(buffer, ver);
        return buffer.toString();
    }
}

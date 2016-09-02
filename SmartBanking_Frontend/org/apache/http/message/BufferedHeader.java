package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.util.CharArrayBuffer;

public class BufferedHeader implements Header {
    private final CharArrayBuffer buffer;
    private final String name;
    private final int valuePos;

    public BufferedHeader(CharArrayBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        int colon = buffer.indexOf(58);
        if (colon == -1) {
            throw new IllegalArgumentException(new StringBuffer().append("Invalid header: ").append(buffer.toString()).toString());
        }
        String s = buffer.substringTrimmed(0, colon);
        if (s.length() == 0) {
            throw new IllegalArgumentException(new StringBuffer().append("Invalid header: ").append(buffer.toString()).toString());
        }
        this.buffer = buffer;
        this.name = s;
        this.valuePos = colon + 1;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.buffer.substringTrimmed(this.valuePos, this.buffer.length());
    }

    public HeaderElement[] getElements() {
        return BasicHeaderElement.parseAll(this.buffer, this.valuePos, this.buffer.length());
    }

    public int getValuePos() {
        return this.valuePos;
    }

    public CharArrayBuffer getBuffer() {
        return this.buffer;
    }

    public String toString() {
        return this.buffer.toString();
    }
}

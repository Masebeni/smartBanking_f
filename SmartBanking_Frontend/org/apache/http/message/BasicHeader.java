package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.util.CharArrayBuffer;

public class BasicHeader implements Header {
    private final String name;
    private final String value;

    public BasicHeader(String name, String value) {
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

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        buffer.append(this.name);
        buffer.append(": ");
        if (this.value != null) {
            buffer.append(this.value);
        }
        return buffer.toString();
    }

    public HeaderElement[] getElements() {
        if (this.value != null) {
            return BasicHeaderElement.parseAll(this.value);
        }
        return new HeaderElement[0];
    }

    public static void format(CharArrayBuffer buffer, Header header) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (header == null) {
            throw new IllegalArgumentException("Header may not be null");
        } else {
            buffer.append(header.getName());
            buffer.append(": ");
            if (header.getValue() != null) {
                buffer.append(header.getValue());
            }
        }
    }

    public static String format(Header header) {
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        format(buffer, header);
        return buffer.toString();
    }
}

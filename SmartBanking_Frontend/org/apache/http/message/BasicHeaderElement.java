package org.apache.http.message;

import com.shane.smartbanking.BuildConfig;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.LangUtils;

public class BasicHeaderElement implements HeaderElement {
    private final String name;
    private final NameValuePair[] parameters;
    private final String value;

    private BasicHeaderElement(NameValuePair[] nvps) {
        if (nvps.length > 0) {
            NameValuePair nvp = nvps[0];
            this.name = nvp.getName();
            this.value = nvp.getValue();
            int len = nvps.length - 1;
            if (len > 0) {
                this.parameters = new NameValuePair[len];
                System.arraycopy(nvps, 1, this.parameters, 0, len);
                return;
            }
            this.parameters = new NameValuePair[0];
            return;
        }
        this.name = BuildConfig.FLAVOR;
        this.value = null;
        this.parameters = new NameValuePair[0];
    }

    public BasicHeaderElement(String name, String value, NameValuePair[] parameters) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        this.value = value;
        if (parameters != null) {
            this.parameters = (NameValuePair[]) parameters.clone();
        } else {
            this.parameters = new NameValuePair[0];
        }
    }

    public BasicHeaderElement(String name, String value) {
        this(name, value, null);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public NameValuePair[] getParameters() {
        return (NameValuePair[]) this.parameters.clone();
    }

    public static final HeaderElement[] parseAll(CharArrayBuffer buffer, int indexFrom, int indexTo) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (indexFrom < 0) {
            throw new IndexOutOfBoundsException();
        } else if (indexTo > buffer.length()) {
            throw new IndexOutOfBoundsException();
        } else if (indexFrom > indexTo) {
            throw new IndexOutOfBoundsException();
        } else {
            List elements = new ArrayList();
            int from = indexFrom;
            boolean qouted = false;
            boolean escaped = false;
            for (int cur = indexFrom; cur < indexTo; cur++) {
                char ch = buffer.charAt(cur);
                if (ch == '\"' && !escaped) {
                    if (qouted) {
                        qouted = false;
                    } else {
                        qouted = true;
                    }
                }
                HeaderElement element = null;
                if (!qouted && ch == ',') {
                    element = parse(buffer, from, cur);
                    from = cur + 1;
                } else if (cur == indexTo - 1) {
                    element = parse(buffer, from, indexTo);
                }
                if (!(element == null || (element.getName().length() == 0 && element.getValue() == null))) {
                    elements.add(element);
                }
                if (escaped) {
                    escaped = false;
                } else {
                    escaped = qouted && ch == '\\';
                }
            }
            return (HeaderElement[]) elements.toArray(new HeaderElement[elements.size()]);
        }
    }

    public static final HeaderElement[] parseAll(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parseAll(buffer, 0, buffer.length());
    }

    public static HeaderElement parse(CharArrayBuffer buffer, int indexFrom, int indexTo) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (indexFrom < 0) {
            throw new IndexOutOfBoundsException();
        } else if (indexTo > buffer.length()) {
            throw new IndexOutOfBoundsException();
        } else if (indexFrom <= indexTo) {
            return new BasicHeaderElement(BasicNameValuePair.parseAll(buffer, indexFrom, indexTo));
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public static final HeaderElement parse(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, 0, buffer.length());
    }

    public static void format(CharArrayBuffer buffer, HeaderElement element) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (element == null) {
            throw new IllegalArgumentException("Header element may not be null");
        } else {
            buffer.append(element.getName());
            if (element.getValue() != null) {
                buffer.append("=");
                buffer.append(element.getValue());
            }
            NameValuePair[] params = element.getParameters();
            for (NameValuePair format : params) {
                buffer.append("; ");
                BasicNameValuePair.format(buffer, format, false);
            }
        }
    }

    public static String format(HeaderElement element) {
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        format(buffer, element);
        return buffer.toString();
    }

    public static void formatAll(CharArrayBuffer buffer, HeaderElement[] elements) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (elements == null) {
            throw new IllegalArgumentException("Array of header element may not be null");
        } else {
            for (int i = 0; i < elements.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                format(buffer, elements[i]);
            }
        }
    }

    public static String formatAll(HeaderElement[] elements) {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        formatAll(buffer, elements);
        return buffer.toString();
    }

    public NameValuePair getParameterByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        for (NameValuePair current : this.parameters) {
            if (current.getName().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }

    public boolean equals(Object object) {
        boolean z = true;
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof HeaderElement)) {
            return false;
        }
        BasicHeaderElement that = (BasicHeaderElement) object;
        if (!(this.name.equals(that.name) && LangUtils.equals(this.value, that.value) && LangUtils.equals(this.parameters, that.parameters))) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int hash = LangUtils.hashCode(LangUtils.hashCode(17, this.name), this.value);
        for (Object hashCode : this.parameters) {
            hash = LangUtils.hashCode(hash, hashCode);
        }
        return hash;
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        buffer.append(this.name);
        if (this.value != null) {
            buffer.append("=");
            buffer.append(this.value);
        }
        for (Object append : this.parameters) {
            buffer.append("; ");
            buffer.append(append);
        }
        return buffer.toString();
    }
}

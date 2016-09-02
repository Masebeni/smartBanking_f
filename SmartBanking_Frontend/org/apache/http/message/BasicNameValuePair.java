package org.apache.http.message;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.LangUtils;

public class BasicNameValuePair implements NameValuePair {
    private static final char[] SEPARATORS;
    private static final char[] UNSAFE_CHARS;
    private final String name;
    private final String value;

    public BasicNameValuePair(String name, String value) {
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

    public static final NameValuePair[] parseAll(CharArrayBuffer buffer, int indexFrom, int indexTo) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (indexFrom < 0) {
            throw new IndexOutOfBoundsException();
        } else if (indexTo > buffer.length()) {
            throw new IndexOutOfBoundsException();
        } else if (indexFrom > indexTo) {
            throw new IndexOutOfBoundsException();
        } else {
            List params = new ArrayList();
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
                NameValuePair param = null;
                if (!qouted && ch == ';') {
                    param = parse(buffer, from, cur);
                    from = cur + 1;
                } else if (cur == indexTo - 1) {
                    param = parse(buffer, from, indexTo);
                }
                if (!(param == null || (param.getName().length() == 0 && param.getValue() == null))) {
                    params.add(param);
                }
                if (escaped) {
                    escaped = false;
                } else {
                    escaped = qouted && ch == '\\';
                }
            }
            return (NameValuePair[]) params.toArray(new NameValuePair[params.size()]);
        }
    }

    public static final NameValuePair[] parseAll(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parseAll(buffer, 0, buffer.length());
    }

    public static NameValuePair parse(CharArrayBuffer buffer, int indexFrom, int indexTo) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (indexFrom < 0) {
            throw new IndexOutOfBoundsException();
        } else if (indexTo > buffer.length()) {
            throw new IndexOutOfBoundsException();
        } else if (indexFrom > indexTo) {
            throw new IndexOutOfBoundsException();
        } else {
            int eq = buffer.indexOf(61, indexFrom, indexTo);
            if (eq < 0) {
                return new BasicNameValuePair(buffer.substringTrimmed(indexFrom, indexTo), null);
            }
            String name = buffer.substringTrimmed(indexFrom, eq);
            int i1 = eq + 1;
            int i2 = indexTo;
            while (i1 < i2 && HTTP.isWhitespace(buffer.charAt(i1))) {
                i1++;
            }
            while (i2 > i1 && HTTP.isWhitespace(buffer.charAt(i2 - 1))) {
                i2--;
            }
            if (i2 - i1 >= 2 && buffer.charAt(i1) == '\"' && buffer.charAt(i2 - 1) == '\"') {
                i1++;
                i2--;
            }
            return new BasicNameValuePair(name, buffer.substring(i1, i2));
        }
    }

    public static final NameValuePair parse(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String may not be null");
        }
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        return parse(buffer, 0, buffer.length());
    }

    static {
        SEPARATORS = new char[]{'(', ')', '<', '>', '@', ',', ';', ':', '\\', '\"', '/', '[', ']', '?', '=', '{', '}', ' ', '\t'};
        UNSAFE_CHARS = new char[]{'\"', '\\'};
    }

    private static boolean isOneOf(char[] chars, char ch) {
        for (char c : chars) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }

    private static boolean isUnsafeChar(char ch) {
        return isOneOf(UNSAFE_CHARS, ch);
    }

    private static boolean isSeparator(char ch) {
        return isOneOf(SEPARATORS, ch);
    }

    private static void format(CharArrayBuffer buffer, String value, boolean alwaysUseQuotes) {
        int i;
        boolean unsafe = false;
        if (alwaysUseQuotes) {
            unsafe = true;
        } else {
            for (i = 0; i < value.length(); i++) {
                if (isSeparator(value.charAt(i))) {
                    unsafe = true;
                    break;
                }
            }
        }
        if (unsafe) {
            buffer.append('\"');
        }
        for (i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (isUnsafeChar(ch)) {
                buffer.append('\\');
            }
            buffer.append(ch);
        }
        if (unsafe) {
            buffer.append('\"');
        }
    }

    public static void format(CharArrayBuffer buffer, NameValuePair param, boolean alwaysUseQuotes) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (param == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        } else {
            buffer.append(param.getName());
            String value = param.getValue();
            if (value != null) {
                buffer.append("=");
                format(buffer, value, alwaysUseQuotes);
            }
        }
    }

    public static void formatAll(CharArrayBuffer buffer, NameValuePair[] params, boolean alwaysUseQuotes) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        } else if (params == null) {
            throw new IllegalArgumentException("Array of parameter may not be null");
        } else {
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    buffer.append("; ");
                }
                format(buffer, params[i], alwaysUseQuotes);
            }
        }
    }

    public static String format(NameValuePair param, boolean alwaysUseQuotes) {
        CharArrayBuffer buffer = new CharArrayBuffer(16);
        format(buffer, param, alwaysUseQuotes);
        return buffer.toString();
    }

    public static String formatAll(NameValuePair[] params, boolean alwaysUseQuotes) {
        CharArrayBuffer buffer = new CharArrayBuffer(16);
        formatAll(buffer, params, alwaysUseQuotes);
        return buffer.toString();
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(16);
        buffer.append(this.name);
        if (this.value != null) {
            buffer.append("=");
            format(buffer, this.value, false);
        }
        return buffer.toString();
    }

    public boolean equals(Object object) {
        boolean z = true;
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof NameValuePair)) {
            return false;
        }
        BasicNameValuePair that = (BasicNameValuePair) object;
        if (!(this.name.equals(that.name) && LangUtils.equals(this.value, that.value))) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(17, this.name), this.value);
    }
}

package org.apache.http.client.utils;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.NameValuePair;
import org.apache.http.util.CharArrayBuffer;

public class URLUtils {
    private static final String DEFAULT_CHARSET = "ISO-8859-1";

    public static String simpleFormUrlEncode(NameValuePair[] pairs, String charset) {
        String formUrlEncode;
        try {
            formUrlEncode = formUrlEncode(pairs, charset);
        } catch (UnsupportedEncodingException e) {
            try {
                formUrlEncode = formUrlEncode(pairs, DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e2) {
                throw new Error("HttpClient requires ISO-8859-1 support");
            }
        }
        return formUrlEncode;
    }

    public static String formUrlEncode(NameValuePair[] pairs, String charset) throws UnsupportedEncodingException {
        CharArrayBuffer buf = new CharArrayBuffer(32);
        for (int i = 0; i < pairs.length; i++) {
            URLCodec codec = new URLCodec();
            NameValuePair pair = pairs[i];
            if (pair.getName() != null) {
                if (i > 0) {
                    buf.append("&");
                }
                buf.append(codec.encode(pair.getName(), charset));
                buf.append("=");
                if (pair.getValue() != null) {
                    buf.append(codec.encode(pair.getValue(), charset));
                }
            }
        }
        return buf.toString();
    }

    private URLUtils() {
    }
}

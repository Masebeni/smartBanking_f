package org.apache.http.params;

import org.apache.http.HttpVersion;
import org.apache.http.protocol.HTTP;

public final class HttpProtocolParams {
    public static final String HTTP_CONTENT_CHARSET = "http.protocol.content-charset";
    public static final String HTTP_ELEMENT_CHARSET = "http.protocol.element-charset";
    public static final String ORIGIN_SERVER = "http.origin-server";
    public static final String PROTOCOL_VERSION = "http.protocol.version";
    public static final String STRICT_TRANSFER_ENCODING = "http.protocol.strict-transfer-encoding";
    public static final String USER_AGENT = "http.useragent";
    public static final String USE_EXPECT_CONTINUE = "http.protocol.expect-continue";
    public static final String WAIT_FOR_CONTINUE = "http.protocol.wait-for-continue";

    private HttpProtocolParams() {
    }

    public static String getHttpElementCharset(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        String charset = (String) params.getParameter(HTTP_ELEMENT_CHARSET);
        if (charset == null) {
            return HTTP.US_ASCII;
        }
        return charset;
    }

    public static void setHttpElementCharset(HttpParams params, String charset) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter(HTTP_ELEMENT_CHARSET, charset);
    }

    public static String getContentCharset(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        String charset = (String) params.getParameter(HTTP_CONTENT_CHARSET);
        if (charset == null) {
            return HTTP.ISO_8859_1;
        }
        return charset;
    }

    public static void setContentCharset(HttpParams params, String charset) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter(HTTP_CONTENT_CHARSET, charset);
    }

    public static HttpVersion getVersion(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        Object param = params.getParameter(PROTOCOL_VERSION);
        if (param == null) {
            return HttpVersion.HTTP_1_1;
        }
        return (HttpVersion) param;
    }

    public static void setVersion(HttpParams params, HttpVersion version) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter(PROTOCOL_VERSION, version);
    }

    public static String getUserAgent(HttpParams params) {
        if (params != null) {
            return (String) params.getParameter(USER_AGENT);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setUserAgent(HttpParams params, String useragent) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter(USER_AGENT, useragent);
    }

    public static boolean useExpectContinue(HttpParams params) {
        if (params != null) {
            return params.getBooleanParameter(USE_EXPECT_CONTINUE, false);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setUseExpectContinue(HttpParams params, boolean b) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter(USE_EXPECT_CONTINUE, b);
    }
}

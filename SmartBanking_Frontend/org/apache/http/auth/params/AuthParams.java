package org.apache.http.auth.params;

import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public final class AuthParams {
    public static final String CREDENTIAL_CHARSET = "http.protocol.credential-charset";

    private AuthParams() {
    }

    public static String getCredentialCharset(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        String charset = (String) params.getParameter(CREDENTIAL_CHARSET);
        if (charset == null) {
            return HTTP.US_ASCII;
        }
        return charset;
    }

    public static void setCredentialCharset(HttpParams params, String charset) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter(CREDENTIAL_CHARSET, charset);
    }
}

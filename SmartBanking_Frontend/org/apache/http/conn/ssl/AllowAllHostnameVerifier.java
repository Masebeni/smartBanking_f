package org.apache.http.conn.ssl;

public class AllowAllHostnameVerifier extends AbstractVerifier {
    public final void verify(String host, String[] cns, String[] subjectAlts) {
    }

    public final String toString() {
        return "ALLOW_ALL";
    }
}

package org.apache.http.conn.ssl;

import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public interface HostnameVerifier extends javax.net.ssl.HostnameVerifier {
    void verify(String str, X509Certificate x509Certificate) throws SSLException;

    void verify(String str, SSLSocket sSLSocket) throws IOException;

    void verify(String str, String[] strArr, String[] strArr2) throws SSLException;

    boolean verify(String str, SSLSession sSLSession);
}

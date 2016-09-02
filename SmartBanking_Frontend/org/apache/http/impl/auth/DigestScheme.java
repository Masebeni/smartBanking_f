package org.apache.http.impl.auth;

import android.support.v4.media.TransportMediator;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.params.AuthParams;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;

public class DigestScheme extends RFC2617Scheme {
    private static final char[] HEXADECIMAL;
    private static final String NC = "00000001";
    private static final int QOP_AUTH = 2;
    private static final int QOP_AUTH_INT = 1;
    private static final int QOP_MISSING = 0;
    private String cnonce;
    private boolean complete;
    private int qopVariant;

    static {
        HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }

    public DigestScheme() {
        this.qopVariant = 0;
        this.complete = false;
    }

    public void processChallenge(Header header) throws MalformedChallengeException {
        super.processChallenge(header);
        if (getParameter("realm") == null) {
            throw new MalformedChallengeException("missing realm in challange");
        } else if (getParameter("nonce") == null) {
            throw new MalformedChallengeException("missing nonce in challange");
        } else {
            boolean unsupportedQop = false;
            String qop = getParameter("qop");
            if (qop != null) {
                StringTokenizer tok = new StringTokenizer(qop, ",");
                while (tok.hasMoreTokens()) {
                    String variant = tok.nextToken().trim();
                    if (variant.equals("auth")) {
                        this.qopVariant = QOP_AUTH;
                        break;
                    } else if (variant.equals("auth-int")) {
                        this.qopVariant = QOP_AUTH_INT;
                    } else {
                        unsupportedQop = true;
                    }
                }
            }
            if (unsupportedQop && this.qopVariant == 0) {
                throw new MalformedChallengeException("None of the qop methods is supported");
            }
            this.cnonce = createCnonce();
            this.complete = true;
        }
    }

    public boolean isComplete() {
        if ("true".equalsIgnoreCase(getParameter("stale"))) {
            return false;
        }
        return this.complete;
    }

    public String getSchemeName() {
        return "digest";
    }

    public boolean isConnectionBased() {
        return false;
    }

    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        } else if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        } else {
            getParameters().put("methodname", request.getRequestLine().getMethod());
            getParameters().put("uri", request.getRequestLine().getUri());
            if (getParameter("charset") == null) {
                getParameters().put("charset", AuthParams.getCredentialCharset(request.getParams()));
            }
            return createDigestHeader(credentials, createDigest(credentials));
        }
    }

    private static MessageDigest createMessageDigest(String digAlg) throws UnsupportedDigestAlgorithmException {
        try {
            return MessageDigest.getInstance(digAlg);
        } catch (Exception e) {
            throw new UnsupportedDigestAlgorithmException(new StringBuffer().append("Unsupported algorithm in HTTP Digest authentication: ").append(digAlg).toString());
        }
    }

    private String createDigest(Credentials credentials) throws AuthenticationException {
        String uri = getParameter("uri");
        String realm = getParameter("realm");
        String nonce = getParameter("nonce");
        String method = getParameter("methodname");
        String algorithm = getParameter("algorithm");
        if (algorithm == null) {
            algorithm = "MD5";
        }
        String charset = getParameter("charset");
        if (charset == null) {
            charset = HTTP.ISO_8859_1;
        }
        int i = this.qopVariant;
        if (r0 == QOP_AUTH_INT) {
            throw new AuthenticationException("Unsupported qop in HTTP Digest authentication");
        }
        String serverDigestValue;
        MessageDigest md5Helper = createMessageDigest("MD5");
        String uname = credentials.getPrincipalName();
        String pwd = credentials.getPassword();
        CharArrayBuffer charArrayBuffer = new CharArrayBuffer(((uname.length() + realm.length()) + pwd.length()) + QOP_AUTH);
        charArrayBuffer.append(uname);
        charArrayBuffer.append(':');
        charArrayBuffer.append(realm);
        charArrayBuffer.append(':');
        charArrayBuffer.append(pwd);
        String a1 = charArrayBuffer.toString();
        if (algorithm.equals("MD5-sess")) {
            String tmp2 = encode(md5Helper.digest(EncodingUtils.getBytes(a1, charset)));
            charArrayBuffer = new CharArrayBuffer(((tmp2.length() + nonce.length()) + this.cnonce.length()) + QOP_AUTH);
            charArrayBuffer.append(tmp2);
            charArrayBuffer.append(':');
            charArrayBuffer.append(nonce);
            charArrayBuffer.append(':');
            charArrayBuffer.append(this.cnonce);
            a1 = charArrayBuffer.toString();
        } else {
            if (!algorithm.equals("MD5")) {
                throw new AuthenticationException(new StringBuffer().append("Unhandled algorithm ").append(algorithm).append(" requested").toString());
            }
        }
        String md5a1 = encode(md5Helper.digest(EncodingUtils.getBytes(a1, charset)));
        String a2 = null;
        i = this.qopVariant;
        if (r0 != QOP_AUTH_INT) {
            a2 = new StringBuffer().append(method).append(":").append(uri).toString();
        }
        String md5a2 = encode(md5Helper.digest(EncodingUtils.getAsciiBytes(a2)));
        if (this.qopVariant == 0) {
            charArrayBuffer = new CharArrayBuffer((md5a1.length() + nonce.length()) + md5a2.length());
            charArrayBuffer.append(md5a1);
            charArrayBuffer.append(':');
            charArrayBuffer.append(nonce);
            charArrayBuffer.append(':');
            charArrayBuffer.append(md5a2);
            serverDigestValue = charArrayBuffer.toString();
        } else {
            String qopOption = getQopVariantString();
            charArrayBuffer = new CharArrayBuffer((((((md5a1.length() + nonce.length()) + NC.length()) + this.cnonce.length()) + qopOption.length()) + md5a2.length()) + 5);
            charArrayBuffer.append(md5a1);
            charArrayBuffer.append(':');
            charArrayBuffer.append(nonce);
            charArrayBuffer.append(':');
            charArrayBuffer.append(NC);
            charArrayBuffer.append(':');
            charArrayBuffer.append(this.cnonce);
            charArrayBuffer.append(':');
            charArrayBuffer.append(qopOption);
            charArrayBuffer.append(':');
            charArrayBuffer.append(md5a2);
            serverDigestValue = charArrayBuffer.toString();
        }
        return encode(md5Helper.digest(EncodingUtils.getAsciiBytes(serverDigestValue)));
    }

    private Header createDigestHeader(Credentials credentials, String digest) throws AuthenticationException {
        CharArrayBuffer buffer = new CharArrayBuffer(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        if (isProxy()) {
            buffer.append(AUTH.PROXY_AUTH_RESP);
        } else {
            buffer.append(AUTH.WWW_AUTH_RESP);
        }
        buffer.append(": Digest ");
        String uri = getParameter("uri");
        String realm = getParameter("realm");
        String nonce = getParameter("nonce");
        String opaque = getParameter("opaque");
        String response = digest;
        String algorithm = getParameter("algorithm");
        List params = new ArrayList(20);
        params.add(new BasicNameValuePair("username", credentials.getPrincipalName()));
        params.add(new BasicNameValuePair("realm", realm));
        params.add(new BasicNameValuePair("nonce", nonce));
        params.add(new BasicNameValuePair("uri", uri));
        params.add(new BasicNameValuePair("response", response));
        if (this.qopVariant != 0) {
            params.add(new BasicNameValuePair("qop", getQopVariantString()));
            params.add(new BasicNameValuePair("nc", NC));
            params.add(new BasicNameValuePair("cnonce", this.cnonce));
        }
        if (algorithm != null) {
            params.add(new BasicNameValuePair("algorithm", algorithm));
        }
        if (opaque != null) {
            params.add(new BasicNameValuePair("opaque", opaque));
        }
        for (int i = 0; i < params.size(); i += QOP_AUTH_INT) {
            boolean z;
            NameValuePair param = (BasicNameValuePair) params.get(i);
            if (i > 0) {
                buffer.append(", ");
            }
            boolean noQuotes = "nc".equals(param.getName()) || "qop".equals(param.getName());
            if (noQuotes) {
                z = false;
            } else {
                z = true;
            }
            BasicNameValuePair.format(buffer, param, z);
        }
        return new BufferedHeader(buffer);
    }

    private String getQopVariantString() {
        if (this.qopVariant == QOP_AUTH_INT) {
            return "auth-int";
        }
        return "auth";
    }

    private static String encode(byte[] binaryData) {
        if (binaryData.length != 16) {
            return null;
        }
        char[] buffer = new char[32];
        for (int i = 0; i < 16; i += QOP_AUTH_INT) {
            int low = binaryData[i] & 15;
            buffer[i * QOP_AUTH] = HEXADECIMAL[(binaryData[i] & 240) >> 4];
            buffer[(i * QOP_AUTH) + QOP_AUTH_INT] = HEXADECIMAL[low];
        }
        return new String(buffer);
    }

    public static String createCnonce() {
        return encode(createMessageDigest("MD5").digest(EncodingUtils.getAsciiBytes(Long.toString(System.currentTimeMillis()))));
    }
}

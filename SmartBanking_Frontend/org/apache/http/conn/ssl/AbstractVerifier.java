package org.apache.http.conn.ssl;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public abstract class AbstractVerifier implements HostnameVerifier {
    private static final String[] BAD_COUNTRY_2LDS;

    static {
        BAD_COUNTRY_2LDS = new String[]{"ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org"};
        Arrays.sort(BAD_COUNTRY_2LDS);
    }

    AbstractVerifier() {
    }

    public final void verify(String host, SSLSocket ssl) throws IOException {
        if (host == null) {
            throw new NullPointerException("host to verify is null");
        }
        SSLSession session = ssl.getSession();
        if (session == null) {
            ssl.getInputStream().available();
            session = ssl.getSession();
            if (session == null) {
                ssl.startHandshake();
                session = ssl.getSession();
            }
        }
        verify(host, session.getPeerCertificates()[0]);
    }

    public final boolean verify(String host, SSLSession session) {
        try {
            verify(host, session.getPeerCertificates()[0]);
            return true;
        } catch (SSLException e) {
            return false;
        }
    }

    public final void verify(String host, X509Certificate cert) throws SSLException {
        verify(host, getCNs(cert), getDNSSubjectAlts(cert));
    }

    public final void verify(String host, String[] cns, String[] subjectAlts, boolean strictWithSubDomains) throws SSLException {
        LinkedList names = new LinkedList();
        if (!(cns == null || cns.length <= 0 || cns[0] == null)) {
            names.add(cns[0]);
        }
        if (subjectAlts != null) {
            for (int i = 0; i < subjectAlts.length; i++) {
                if (subjectAlts[i] != null) {
                    names.add(subjectAlts[i]);
                }
            }
        }
        if (names.isEmpty()) {
            throw new SSLException(new StringBuffer().append("Certificate for <").append(host).append("> doesn't contain CN or DNS subjectAlt").toString());
        }
        StringBuffer buf = new StringBuffer();
        String hostName = host.trim().toLowerCase();
        boolean match = false;
        Iterator it = names.iterator();
        while (it.hasNext()) {
            String cn = ((String) it.next()).toLowerCase();
            buf.append(" <");
            buf.append(cn);
            buf.append('>');
            if (it.hasNext()) {
                buf.append(" OR");
            }
            boolean doWildcard = cn.startsWith("*.") && cn.lastIndexOf(46) >= 0 && acceptableCountryWildcard(cn);
            if (doWildcard) {
                match = hostName.endsWith(cn.substring(1));
                if (match && strictWithSubDomains) {
                    if (countDots(hostName) == countDots(cn)) {
                        match = true;
                        continue;
                    } else {
                        match = false;
                        continue;
                    }
                }
            } else {
                match = hostName.equals(cn);
                continue;
            }
            if (match) {
                break;
            }
        }
        if (!match) {
            throw new SSLException(new StringBuffer().append("hostname in certificate didn't match: <").append(host).append("> !=").append(buf).toString());
        }
    }

    public static boolean acceptableCountryWildcard(String cn) {
        int cnLen = cn.length();
        if (cnLen < 7 || cnLen > 9 || cn.charAt(cnLen - 3) != '.') {
            return true;
        }
        if (Arrays.binarySearch(BAD_COUNTRY_2LDS, cn.substring(2, cnLen - 3)) < 0) {
            return true;
        }
        return false;
    }

    public static String[] getCNs(X509Certificate cert) {
        LinkedList cnList = new LinkedList();
        StringTokenizer st = new StringTokenizer(cert.getSubjectX500Principal().toString(), ",");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            int x = tok.indexOf("CN=");
            if (x >= 0) {
                cnList.add(tok.substring(x + 3));
            }
        }
        if (cnList.isEmpty()) {
            return null;
        }
        String[] cns = new String[cnList.size()];
        cnList.toArray(cns);
        return cns;
    }

    public static String[] getDNSSubjectAlts(X509Certificate cert) {
        LinkedList subjectAltList = new LinkedList();
        Collection c = null;
        try {
            c = cert.getSubjectAlternativeNames();
        } catch (CertificateParsingException cpe) {
            cpe.printStackTrace();
        }
        if (c != null) {
            for (List list : c) {
                if (((Integer) list.get(0)).intValue() == 2) {
                    subjectAltList.add((String) list.get(1));
                }
            }
        }
        if (subjectAltList.isEmpty()) {
            return null;
        }
        String[] subjectAlts = new String[subjectAltList.size()];
        subjectAltList.toArray(subjectAlts);
        return subjectAlts;
    }

    public static int countDots(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                count++;
            }
        }
        return count;
    }
}

package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.Scheme;
import org.apache.http.conn.SchemeRegistry;
import org.apache.http.conn.SecureSocketFactory;
import org.apache.http.conn.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class DefaultClientConnectionOperator implements ClientConnectionOperator {
    protected SchemeRegistry schemeRegistry;

    public DefaultClientConnectionOperator(SchemeRegistry schemes) {
        if (schemes == null) {
            throw new IllegalArgumentException("Scheme registry must not be null.");
        }
        this.schemeRegistry = schemes;
    }

    public OperatedClientConnection createConnection() {
        return new DefaultClientConnection();
    }

    public void openConnection(OperatedClientConnection conn, HttpHost target, InetAddress local, HttpContext context, HttpParams params) throws IOException {
        if (conn == null) {
            throw new IllegalArgumentException("Connection must not be null.");
        } else if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else if (conn.isOpen()) {
            throw new IllegalArgumentException("Connection must not be open.");
        } else {
            Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
            if (schm == null) {
                throw new IllegalArgumentException(new StringBuffer().append("Unknown scheme '").append(target.getSchemeName()).append("' in target host.").toString());
            }
            SocketFactory sf = schm.getSocketFactory();
            Socket sock = sf.createSocket();
            conn.announce(sock);
            sock = sf.connectSocket(sock, target.getHostName(), schm.resolvePort(target.getPort()), local, 0, params);
            prepareSocket(sock, context, params);
            conn.open(sock, target, sf.isSecure(sock), params);
        }
    }

    public void updateSecureConnection(OperatedClientConnection conn, HttpHost target, HttpContext context, HttpParams params) throws IOException {
        if (conn == null) {
            throw new IllegalArgumentException("Connection must not be null.");
        } else if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else if (conn.isOpen()) {
            Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
            if (schm == null) {
                throw new IllegalArgumentException(new StringBuffer().append("Unknown scheme '").append(target.getSchemeName()).append("' in target host.").toString());
            } else if (schm.getSocketFactory() instanceof SecureSocketFactory) {
                SecureSocketFactory ssf = (SecureSocketFactory) schm.getSocketFactory();
                Socket sock = ssf.createSocket(conn.getSocket(), target.getHostName(), target.getPort(), true);
                prepareSocket(sock, context, params);
                conn.update(sock, target, ssf.isSecure(sock), params);
            } else {
                throw new IllegalArgumentException(new StringBuffer().append("Target scheme (").append(schm.getName()).append(") must have secure socket factory.").toString());
            }
        } else {
            throw new IllegalArgumentException("Connection must be open.");
        }
    }

    protected void prepareSocket(Socket sock, HttpContext context, HttpParams params) throws IOException {
        sock.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
        sock.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
        int linger = HttpConnectionParams.getLinger(params);
        if (linger >= 0) {
            sock.setSoLinger(linger > 0, linger);
        }
    }
}

package org.apache.http.conn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public final class PlainSocketFactory implements SocketFactory {
    private static final PlainSocketFactory DEFAULT_FACTORY;
    static Class class$java$net$Socket;
    static Class class$org$apache$http$conn$PlainSocketFactory;

    static {
        DEFAULT_FACTORY = new PlainSocketFactory();
    }

    public static final PlainSocketFactory getSocketFactory() {
        return DEFAULT_FACTORY;
    }

    private PlainSocketFactory() {
    }

    public Socket createSocket() {
        return new Socket();
    }

    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        } else {
            InetSocketAddress target = new InetSocketAddress(host, port);
            if (sock == null) {
                sock = createSocket();
            }
            if (localAddress != null || localPort > 0) {
                if (localPort < 0) {
                    localPort = 0;
                }
                sock.bind(new InetSocketAddress(localAddress, localPort));
            }
            sock.connect(target, HttpConnectionParams.getConnectionTimeout(params));
            return sock;
        }
    }

    public final boolean isSecure(Socket sock) throws IllegalArgumentException {
        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }
        Class class$;
        Class cls = sock.getClass();
        if (class$java$net$Socket == null) {
            class$ = class$("java.net.Socket");
            class$java$net$Socket = class$;
        } else {
            class$ = class$java$net$Socket;
        }
        if (cls != class$) {
            throw new IllegalArgumentException("Socket not created by this factory.");
        } else if (!sock.isClosed()) {
            return false;
        } else {
            throw new IllegalArgumentException("Socket is closed.");
        }
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public int hashCode() {
        Object class$;
        if (class$org$apache$http$conn$PlainSocketFactory == null) {
            class$ = class$("org.apache.http.conn.PlainSocketFactory");
            class$org$apache$http$conn$PlainSocketFactory = class$;
        } else {
            class$ = class$org$apache$http$conn$PlainSocketFactory;
        }
        return class$.hashCode();
    }
}

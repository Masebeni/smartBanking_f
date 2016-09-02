package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.Socket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.impl.SocketHttpClientConnection;
import org.apache.http.io.HttpDataReceiver;
import org.apache.http.io.HttpDataTransmitter;
import org.apache.http.params.HttpParams;

public class DefaultClientConnection extends SocketHttpClientConnection implements OperatedClientConnection {
    private static final Log HEADERS_LOG;
    private static final Log LOG;
    private static final Log WIRE_LOG;
    static Class class$org$apache$http$impl$conn$DefaultClientConnection;
    private volatile Socket announcedSocket;
    private boolean connSecure;
    private HttpHost targetHost;

    static {
        Class class$;
        HEADERS_LOG = LogFactory.getLog("org.apache.http.headers");
        WIRE_LOG = LogFactory.getLog("org.apache.http.wire");
        if (class$org$apache$http$impl$conn$DefaultClientConnection == null) {
            class$ = class$("org.apache.http.impl.conn.DefaultClientConnection");
            class$org$apache$http$impl$conn$DefaultClientConnection = class$;
        } else {
            class$ = class$org$apache$http$impl$conn$DefaultClientConnection;
        }
        LOG = LogFactory.getLog(class$);
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    public final boolean isSecure() {
        return this.connSecure;
    }

    public final Socket getSocket() {
        return super.getSocket();
    }

    public void announce(Socket sock) {
        assertNotOpen();
        this.announcedSocket = sock;
    }

    public void shutdown() throws IOException {
        LOG.debug("Connection shut down");
        Socket sock = this.announcedSocket;
        if (sock != null) {
            sock.close();
        }
        super.shutdown();
    }

    public void close() throws IOException {
        LOG.debug("Connection closed");
        super.close();
    }

    protected HttpDataReceiver createHttpDataReceiver(Socket socket, int buffersize, HttpParams params) throws IOException {
        HttpDataReceiver receiver = super.createHttpDataReceiver(socket, buffersize, params);
        if (WIRE_LOG.isDebugEnabled()) {
            return new LoggingHttpDataReceiverDecorator(receiver, new Wire(WIRE_LOG));
        }
        return receiver;
    }

    protected HttpDataTransmitter createHttpDataTransmitter(Socket socket, int buffersize, HttpParams params) throws IOException {
        HttpDataTransmitter transmitter = super.createHttpDataTransmitter(socket, buffersize, params);
        if (WIRE_LOG.isDebugEnabled()) {
            return new LoggingHttpDataTransmitterDecorator(transmitter, new Wire(WIRE_LOG));
        }
        return transmitter;
    }

    public void open(Socket sock, HttpHost target, boolean secure, HttpParams params) throws IOException {
        assertNotOpen();
        if (sock == null) {
            throw new IllegalArgumentException("Socket must not be null.");
        } else if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else {
            bind(sock, params);
            this.targetHost = target;
            this.connSecure = secure;
            this.announcedSocket = null;
        }
    }

    public void update(Socket sock, HttpHost target, boolean secure, HttpParams params) throws IOException {
        assertOpen();
        if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else {
            if (sock != null) {
                bind(sock, params);
            }
            this.targetHost = target;
            this.connSecure = secure;
        }
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        HttpResponse response = super.receiveResponseHeader();
        if (HEADERS_LOG.isDebugEnabled()) {
            HEADERS_LOG.debug(new StringBuffer().append("<< ").append(response.getStatusLine().toString()).toString());
            Header[] headers = response.getAllHeaders();
            for (Object obj : headers) {
                HEADERS_LOG.debug(new StringBuffer().append("<< ").append(obj.toString()).toString());
            }
        }
        return response;
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        super.sendRequestHeader(request);
        if (HEADERS_LOG.isDebugEnabled()) {
            HEADERS_LOG.debug(new StringBuffer().append(">> ").append(request.getRequestLine().toString()).toString());
            Header[] headers = request.getAllHeaders();
            for (Object obj : headers) {
                HEADERS_LOG.debug(new StringBuffer().append(">> ").append(obj.toString()).toString());
            }
        }
    }
}

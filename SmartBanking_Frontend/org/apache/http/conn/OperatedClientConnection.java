package org.apache.http.conn;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpInetConnection;
import org.apache.http.params.HttpParams;

public interface OperatedClientConnection extends HttpClientConnection, HttpInetConnection {
    void announce(Socket socket);

    Socket getSocket();

    HttpHost getTargetHost();

    boolean isSecure();

    void open(Socket socket, HttpHost httpHost, boolean z, HttpParams httpParams) throws IOException;

    void update(Socket socket, HttpHost httpHost, boolean z, HttpParams httpParams) throws IOException;
}

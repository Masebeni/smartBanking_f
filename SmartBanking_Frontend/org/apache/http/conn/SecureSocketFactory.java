package org.apache.http.conn;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public interface SecureSocketFactory extends SocketFactory {
    Socket createSocket(Socket socket, String str, int i, boolean z) throws IOException, UnknownHostException;
}

package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.params.HttpParams;

public class DefaultHttpClientConnection extends SocketHttpClientConnection {
    public void bind(Socket socket, HttpParams params) throws IOException {
        assertNotOpen();
        super.bind(socket, params);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        if (isOpen()) {
            buffer.append(getRemotePort());
        } else {
            buffer.append("closed");
        }
        buffer.append("]");
        return buffer.toString();
    }
}

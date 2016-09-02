package org.apache.http.impl.io;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.IOException;
import java.net.Socket;
import org.apache.http.params.HttpParams;

public class SocketHttpDataTransmitter extends AbstractHttpDataTransmitter {
    public SocketHttpDataTransmitter(Socket socket, int buffersize, HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        if (buffersize < 0) {
            buffersize = socket.getReceiveBufferSize();
        }
        if (buffersize < AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
            buffersize = AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
        }
        init(socket.getOutputStream(), buffersize, params);
    }
}

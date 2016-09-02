package org.apache.http.conn;

import java.io.InterruptedIOException;

public class ConnectTimeoutException extends InterruptedIOException {
    static final long serialVersionUID = -4816682903149535989L;

    public ConnectTimeoutException(String message) {
        super(message);
    }
}

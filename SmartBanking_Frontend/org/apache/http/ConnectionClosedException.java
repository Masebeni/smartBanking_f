package org.apache.http;

import java.io.IOException;

public class ConnectionClosedException extends IOException {
    static final long serialVersionUID = 617550366255636674L;

    public ConnectionClosedException(String message) {
        super(message);
    }
}

package org.apache.http.params;

public final class HttpConnectionParams {
    public static final String CONNECTION_TIMEOUT = "http.connection.timeout";
    public static final String MAX_HEADER_COUNT = "http.connection.max-header-count";
    public static final String MAX_LINE_LENGTH = "http.connection.max-line-length";
    public static final String MAX_STATUS_LINE_GARBAGE = "http.connection.max-status-line-garbage";
    public static final String SOCKET_BUFFER_SIZE = "http.socket.buffer-size";
    public static final String SO_LINGER = "http.socket.linger";
    public static final String SO_TIMEOUT = "http.socket.timeout";
    public static final String STALE_CONNECTION_CHECK = "http.connection.stalecheck";
    public static final String TCP_NODELAY = "http.tcp.nodelay";

    private HttpConnectionParams() {
    }

    public static int getSoTimeout(HttpParams params) {
        if (params != null) {
            return params.getIntParameter(SO_TIMEOUT, 0);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setSoTimeout(HttpParams params, int timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter(SO_TIMEOUT, timeout);
    }

    public static boolean getTcpNoDelay(HttpParams params) {
        if (params != null) {
            return params.getBooleanParameter(TCP_NODELAY, true);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setTcpNoDelay(HttpParams params, boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter(TCP_NODELAY, value);
    }

    public static int getSocketBufferSize(HttpParams params) {
        if (params != null) {
            return params.getIntParameter(SOCKET_BUFFER_SIZE, -1);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setSocketBufferSize(HttpParams params, int size) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter(SOCKET_BUFFER_SIZE, size);
    }

    public static int getLinger(HttpParams params) {
        if (params != null) {
            return params.getIntParameter(SO_LINGER, -1);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setLinger(HttpParams params, int value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter(SO_LINGER, value);
    }

    public static int getConnectionTimeout(HttpParams params) {
        if (params != null) {
            return params.getIntParameter(CONNECTION_TIMEOUT, 0);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setConnectionTimeout(HttpParams params, int timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter(CONNECTION_TIMEOUT, timeout);
    }

    public static boolean isStaleCheckingEnabled(HttpParams params) {
        if (params != null) {
            return params.getBooleanParameter(STALE_CONNECTION_CHECK, true);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }

    public static void setStaleCheckingEnabled(HttpParams params, boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setBooleanParameter(STALE_CONNECTION_CHECK, value);
    }
}

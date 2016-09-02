package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpConnection;

public class IdleConnectionHandler {
    private static final Log LOG;
    static Class class$org$apache$http$impl$conn$IdleConnectionHandler;
    private Map connectionToAdded;

    static {
        Class class$;
        if (class$org$apache$http$impl$conn$IdleConnectionHandler == null) {
            class$ = class$("org.apache.http.impl.conn.IdleConnectionHandler");
            class$org$apache$http$impl$conn$IdleConnectionHandler = class$;
        } else {
            class$ = class$org$apache$http$impl$conn$IdleConnectionHandler;
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

    public IdleConnectionHandler() {
        this.connectionToAdded = new HashMap();
    }

    public void add(HttpConnection connection) {
        Long timeAdded = new Long(System.currentTimeMillis());
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("Adding connection at: ").append(timeAdded).toString());
        }
        this.connectionToAdded.put(connection, timeAdded);
    }

    public void remove(HttpConnection connection) {
        this.connectionToAdded.remove(connection);
    }

    public void removeAll() {
        this.connectionToAdded.clear();
    }

    public void closeIdleConnections(long idleTime) {
        long idleTimeout = System.currentTimeMillis() - idleTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("Checking for connections, idleTimeout: ").append(idleTimeout).toString());
        }
        Iterator connectionIter = this.connectionToAdded.keySet().iterator();
        while (connectionIter.hasNext()) {
            HttpConnection conn = (HttpConnection) connectionIter.next();
            Long connectionTime = (Long) this.connectionToAdded.get(conn);
            if (connectionTime.longValue() <= idleTimeout) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(new StringBuffer().append("Closing connection, connection time: ").append(connectionTime).toString());
                }
                connectionIter.remove();
                try {
                    conn.close();
                } catch (IOException ex) {
                    LOG.debug("I/O error closing connection", ex);
                }
            }
        }
    }
}

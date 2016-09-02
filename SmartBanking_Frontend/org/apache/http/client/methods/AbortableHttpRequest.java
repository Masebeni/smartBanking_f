package org.apache.http.client.methods;

import org.apache.http.conn.ConnectionReleaseTrigger;

public interface AbortableHttpRequest {
    void abort();

    void setReleaseTrigger(ConnectionReleaseTrigger connectionReleaseTrigger);
}

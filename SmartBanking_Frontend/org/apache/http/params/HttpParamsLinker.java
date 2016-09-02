package org.apache.http.params;

import org.apache.http.HttpMessage;

public class HttpParamsLinker {
    private HttpParamsLinker() {
    }

    public static void link(HttpMessage message, HttpParams defaults) {
        if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        } else if (message.getParams() instanceof HttpLinkedParams) {
            ((HttpLinkedParams) message.getParams()).setDefaults(defaults);
        }
    }
}

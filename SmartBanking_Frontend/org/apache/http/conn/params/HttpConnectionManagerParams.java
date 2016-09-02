package org.apache.http.conn.params;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.conn.HostConfiguration;
import org.apache.http.params.HttpParams;

public final class HttpConnectionManagerParams {
    public static final int DEFAULT_MAX_HOST_CONNECTIONS = 2;
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    public static final String MAX_HOST_CONNECTIONS = "http.connection-manager.max-per-host";
    public static final String MAX_TOTAL_CONNECTIONS = "http.connection-manager.max-total";

    public static void setDefaultMaxConnectionsPerHost(HttpParams params, int maxHostConnections) {
        setMaxConnectionsPerHost(params, HostConfiguration.ANY_HOST_CONFIGURATION, maxHostConnections);
    }

    public static void setMaxConnectionsPerHost(HttpParams params, HostConfiguration hostConfiguration, int maxHostConnections) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else if (maxHostConnections <= 0) {
            throw new IllegalArgumentException("maxHostConnections must be greater than 0");
        } else {
            Map newValues;
            Map currentValues = (Map) params.getParameter(MAX_HOST_CONNECTIONS);
            if (currentValues == null) {
                newValues = new HashMap();
            } else {
                newValues = new HashMap(currentValues);
            }
            newValues.put(hostConfiguration, new Integer(maxHostConnections));
            params.setParameter(MAX_HOST_CONNECTIONS, newValues);
        }
    }

    public static int getDefaultMaxConnectionsPerHost(HttpParams params) {
        return getMaxConnectionsPerHost(params, HostConfiguration.ANY_HOST_CONFIGURATION);
    }

    public static int getMaxConnectionsPerHost(HttpParams params, HostConfiguration hostConfiguration) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        Map m = (Map) params.getParameter(MAX_HOST_CONNECTIONS);
        if (m == null) {
            return DEFAULT_MAX_HOST_CONNECTIONS;
        }
        Integer max = (Integer) m.get(hostConfiguration);
        if (max == null && hostConfiguration != HostConfiguration.ANY_HOST_CONFIGURATION) {
            return getMaxConnectionsPerHost(params, HostConfiguration.ANY_HOST_CONFIGURATION);
        }
        if (max != null) {
            return max.intValue();
        }
        return DEFAULT_MAX_HOST_CONNECTIONS;
    }

    public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setIntParameter(MAX_TOTAL_CONNECTIONS, maxTotalConnections);
    }

    public static int getMaxTotalConnections(HttpParams params) {
        if (params != null) {
            return params.getIntParameter(MAX_TOTAL_CONNECTIONS, DEFAULT_MAX_TOTAL_CONNECTIONS);
        }
        throw new IllegalArgumentException("HTTP parameters may not be null");
    }
}

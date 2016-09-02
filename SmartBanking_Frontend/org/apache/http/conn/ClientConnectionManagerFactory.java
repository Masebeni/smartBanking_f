package org.apache.http.conn;

import org.apache.http.params.HttpParams;

public interface ClientConnectionManagerFactory {
    ClientConnectionManager newInstance(HttpParams httpParams, SchemeRegistry schemeRegistry);
}

package org.apache.http.params;

public interface HttpLinkedParams extends HttpParams {
    HttpParams getDefaults();

    boolean isParameterSetLocally(String str);

    void setDefaults(HttpParams httpParams);
}

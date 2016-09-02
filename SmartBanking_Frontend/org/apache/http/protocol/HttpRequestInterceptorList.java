package org.apache.http.protocol;

import java.util.List;
import org.apache.http.HttpRequestInterceptor;

public interface HttpRequestInterceptorList {
    void addRequestInterceptor(HttpRequestInterceptor httpRequestInterceptor);

    void clearRequestInterceptors();

    HttpRequestInterceptor getRequestInterceptor(int i);

    int getRequestInterceptorCount();

    void setInterceptors(List list);
}

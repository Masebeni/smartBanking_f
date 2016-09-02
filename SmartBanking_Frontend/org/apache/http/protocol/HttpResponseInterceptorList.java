package org.apache.http.protocol;

import java.util.List;
import org.apache.http.HttpResponseInterceptor;

public interface HttpResponseInterceptorList {
    void addResponseInterceptor(HttpResponseInterceptor httpResponseInterceptor);

    void clearResponseInterceptors();

    HttpResponseInterceptor getResponseInterceptor(int i);

    int getResponseInterceptorCount();

    void setInterceptors(List list);
}

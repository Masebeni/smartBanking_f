package org.apache.http;

import java.util.Iterator;
import org.apache.http.params.HttpParams;

public interface HttpMessage {
    void addHeader(String str, String str2);

    void addHeader(Header header);

    boolean containsHeader(String str);

    Header[] getAllHeaders();

    Header getFirstHeader(String str);

    Header[] getHeaders(String str);

    HttpVersion getHttpVersion();

    Header getLastHeader(String str);

    HttpParams getParams();

    Iterator headerIterator();

    void removeHeader(Header header);

    void removeHeaders(String str);

    void setHeader(String str, String str2);

    void setHeader(Header header);

    void setHeaders(Header[] headerArr);

    void setParams(HttpParams httpParams);
}

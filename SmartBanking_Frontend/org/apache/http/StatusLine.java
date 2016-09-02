package org.apache.http;

public interface StatusLine {
    HttpVersion getHttpVersion();

    String getReasonPhrase();

    int getStatusCode();
}

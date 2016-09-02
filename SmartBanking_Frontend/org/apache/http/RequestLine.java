package org.apache.http;

public interface RequestLine {
    HttpVersion getHttpVersion();

    String getMethod();

    String getUri();
}

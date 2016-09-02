package org.apache.http;

import java.util.Locale;

public interface HttpResponse extends HttpMessage {
    HttpEntity getEntity();

    Locale getLocale();

    StatusLine getStatusLine();

    void setEntity(HttpEntity httpEntity);

    void setLocale(Locale locale);

    void setReasonPhrase(String str) throws IllegalStateException;

    void setStatusCode(int i) throws IllegalStateException;

    void setStatusLine(HttpVersion httpVersion, int i);

    void setStatusLine(HttpVersion httpVersion, int i, String str);

    void setStatusLine(StatusLine statusLine);
}

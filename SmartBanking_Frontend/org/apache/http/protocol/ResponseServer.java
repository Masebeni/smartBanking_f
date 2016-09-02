package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.params.HttpProtocolParams;

public class ResponseServer implements HttpResponseInterceptor {
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        } else if (!response.containsHeader(HTTP.SERVER_DIRECTIVE)) {
            String s = (String) response.getParams().getParameter(HttpProtocolParams.ORIGIN_SERVER);
            if (s != null) {
                response.addHeader(HTTP.SERVER_DIRECTIVE, s);
            }
        }
    }
}

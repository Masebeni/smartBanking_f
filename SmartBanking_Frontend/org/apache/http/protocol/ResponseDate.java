package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;

public class ResponseDate implements HttpResponseInterceptor {
    private static final HttpDateGenerator DATE_GENERATOR;

    static {
        DATE_GENERATOR = new HttpDateGenerator();
    }

    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        } else if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK) {
            response.setHeader(HTTP.DATE_DIRECTIVE, DATE_GENERATOR.getCurrentDate());
        }
    }
}

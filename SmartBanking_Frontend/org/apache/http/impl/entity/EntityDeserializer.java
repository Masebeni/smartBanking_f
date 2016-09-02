package org.apache.http.impl.entity;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.HttpDataInputStream;
import org.apache.http.io.HttpDataReceiver;
import org.apache.http.protocol.HTTP;

public class EntityDeserializer {
    private final ContentLengthStrategy lenStrategy;

    public EntityDeserializer(ContentLengthStrategy lenStrategy) {
        if (lenStrategy == null) {
            throw new IllegalArgumentException("Content length strategy may not be null");
        }
        this.lenStrategy = lenStrategy;
    }

    protected BasicHttpEntity doDeserialize(HttpDataReceiver datareceiver, HttpMessage message) throws HttpException, IOException {
        if (datareceiver == null) {
            throw new IllegalArgumentException("HTTP data receiver may not be null");
        } else if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        } else {
            BasicHttpEntity entity = new BasicHttpEntity();
            long len = this.lenStrategy.determineLength(message);
            if (len == -2) {
                entity.setChunked(true);
                entity.setContentLength(-1);
                entity.setContent(new ChunkedInputStream(datareceiver));
            } else if (len == -1) {
                entity.setChunked(false);
                entity.setContentLength(-1);
                entity.setContent(new HttpDataInputStream(datareceiver));
            } else {
                entity.setChunked(false);
                entity.setContentLength(len);
                entity.setContent(new ContentLengthInputStream(datareceiver, len));
            }
            Header contentTypeHeader = message.getFirstHeader(HTTP.CONTENT_TYPE);
            if (contentTypeHeader != null) {
                entity.setContentType(contentTypeHeader);
            }
            Header contentEncodingHeader = message.getFirstHeader(HTTP.CONTENT_ENCODING);
            if (contentEncodingHeader != null) {
                entity.setContentEncoding(contentEncodingHeader);
            }
            return entity;
        }
    }

    public HttpEntity deserialize(HttpDataReceiver datareceiver, HttpMessage message) throws HttpException, IOException {
        return doDeserialize(datareceiver, message);
    }
}

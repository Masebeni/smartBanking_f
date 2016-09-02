package org.apache.http.impl.entity;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.impl.io.ContentLengthOutputStream;
import org.apache.http.impl.io.IdentityOutputStream;
import org.apache.http.io.HttpDataTransmitter;

public class EntitySerializer {
    private final ContentLengthStrategy lenStrategy;

    public EntitySerializer(ContentLengthStrategy lenStrategy) {
        if (lenStrategy == null) {
            throw new IllegalArgumentException("Content length strategy may not be null");
        }
        this.lenStrategy = lenStrategy;
    }

    protected OutputStream doSerialize(HttpDataTransmitter datatransmitter, HttpMessage message) throws HttpException, IOException {
        long len = this.lenStrategy.determineLength(message);
        if (len == -2) {
            return new ChunkedOutputStream(datatransmitter);
        }
        if (len == -1) {
            return new IdentityOutputStream(datatransmitter);
        }
        return new ContentLengthOutputStream(datatransmitter, len);
    }

    public void serialize(HttpDataTransmitter datatransmitter, HttpMessage message, HttpEntity entity) throws HttpException, IOException {
        if (datatransmitter == null) {
            throw new IllegalArgumentException("HTTP data transmitter may not be null");
        } else if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        } else if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        } else {
            OutputStream outstream = doSerialize(datatransmitter, message);
            entity.writeTo(outstream);
            outstream.close();
        }
    }
}

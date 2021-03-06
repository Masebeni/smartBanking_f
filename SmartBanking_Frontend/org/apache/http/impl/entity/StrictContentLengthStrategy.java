package org.apache.http.impl.entity;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.protocol.HTTP;

public class StrictContentLengthStrategy implements ContentLengthStrategy {
    public long determineLength(HttpMessage message) throws HttpException {
        String s;
        if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        }
        Header transferEncodingHeader = message.getFirstHeader(HTTP.TRANSFER_ENCODING);
        Header contentLengthHeader = message.getFirstHeader(HTTP.CONTENT_LEN);
        if (transferEncodingHeader != null) {
            s = transferEncodingHeader.getValue();
            if (HTTP.CHUNK_CODING.equalsIgnoreCase(s)) {
                if (!message.getHttpVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                    return -2;
                }
                throw new ProtocolException(new StringBuffer().append("Chunked transfer encoding not allowed for ").append(message.getHttpVersion()).toString());
            } else if (HTTP.IDENTITY_CODING.equalsIgnoreCase(s)) {
                return -1;
            } else {
                throw new ProtocolException(new StringBuffer().append("Unsupported transfer encoding: ").append(s).toString());
            }
        } else if (contentLengthHeader == null) {
            return -1;
        } else {
            s = contentLengthHeader.getValue();
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new ProtocolException(new StringBuffer().append("Invalid content length: ").append(s).toString());
            }
        }
    }
}

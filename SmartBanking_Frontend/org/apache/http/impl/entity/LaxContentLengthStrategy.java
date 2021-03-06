package org.apache.http.impl.entity;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.ProtocolException;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class LaxContentLengthStrategy implements ContentLengthStrategy {
    public long determineLength(HttpMessage message) throws HttpException {
        int i;
        if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        }
        boolean strict = message.getParams().isParameterTrue(HttpProtocolParams.STRICT_TRANSFER_ENCODING);
        Header transferEncodingHeader = message.getFirstHeader(HTTP.TRANSFER_ENCODING);
        Header contentLengthHeader = message.getFirstHeader(HTTP.CONTENT_LEN);
        if (transferEncodingHeader != null) {
            HeaderElement[] encodings = transferEncodingHeader.getElements();
            if (strict) {
                i = 0;
                while (i < encodings.length) {
                    String encoding = encodings[i].getName();
                    if (encoding == null || encoding.length() <= 0 || encoding.equalsIgnoreCase(HTTP.CHUNK_CODING) || encoding.equalsIgnoreCase(HTTP.IDENTITY_CODING)) {
                        i++;
                    } else {
                        throw new ProtocolException(new StringBuffer().append("Unsupported transfer encoding: ").append(encoding).toString());
                    }
                }
            }
            int len = encodings.length;
            if (HTTP.IDENTITY_CODING.equalsIgnoreCase(transferEncodingHeader.getValue())) {
                return -1;
            }
            if (len > 0 && HTTP.CHUNK_CODING.equalsIgnoreCase(encodings[len - 1].getName())) {
                return -2;
            }
            if (!strict) {
                return -1;
            }
            throw new ProtocolException("Chunk-encoding must be the last one applied");
        } else if (contentLengthHeader == null) {
            return -1;
        } else {
            long contentlen = -1;
            Header[] headers = message.getHeaders(HTTP.CONTENT_LEN);
            if (!strict || headers.length <= 1) {
                i = headers.length - 1;
                while (i >= 0) {
                    Header header = headers[i];
                    try {
                        contentlen = Long.parseLong(header.getValue());
                        break;
                    } catch (NumberFormatException e) {
                        if (strict) {
                            throw new ProtocolException(new StringBuffer().append("Invalid content length: ").append(header.getValue()).toString());
                        }
                        i--;
                    }
                }
                if (contentlen < 0) {
                    return -1;
                }
                return contentlen;
            }
            throw new ProtocolException("Multiple content length headers");
        }
    }
}

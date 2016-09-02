package org.apache.http.client.methods;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

public class UrlEncodedFormEntity extends AbstractHttpEntity {
    public static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final byte[] content;

    public UrlEncodedFormEntity(NameValuePair[] fields, String charset) throws UnsupportedEncodingException {
        this.content = EncodingUtils.getAsciiBytes(URLUtils.formUrlEncode(fields, charset));
    }

    public UrlEncodedFormEntity(NameValuePair[] fields) {
        this.content = EncodingUtils.getAsciiBytes(URLUtils.simpleFormUrlEncode(fields, HTTP.UTF_8));
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return (long) this.content.length;
    }

    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    public Header getContentType() {
        return new BasicHeader(HTTP.CONTENT_TYPE, FORM_URL_ENCODED_CONTENT_TYPE);
    }

    public boolean isStreaming() {
        return false;
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        outstream.write(this.content);
        outstream.flush();
    }
}

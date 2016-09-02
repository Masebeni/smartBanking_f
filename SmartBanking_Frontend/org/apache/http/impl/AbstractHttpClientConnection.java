package org.apache.http.impl;

import android.support.v4.media.TransportMediator;
import java.io.IOException;
import java.util.Iterator;
import org.apache.http.Header;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.io.HttpDataReceiver;
import org.apache.http.io.HttpDataTransmitter;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.BufferedHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.HeaderUtils;

public abstract class AbstractHttpClientConnection implements HttpClientConnection {
    private final CharArrayBuffer buffer;
    private HttpDataReceiver datareceiver;
    private HttpDataTransmitter datatransmitter;
    private final EntityDeserializer entitydeserializer;
    private final EntitySerializer entityserializer;
    private int maxGarbageLines;
    private int maxHeaderCount;
    private int maxLineLen;
    private HttpConnectionMetricsImpl metrics;
    private final HttpResponseFactory responsefactory;

    protected abstract void assertOpen() throws IllegalStateException;

    public AbstractHttpClientConnection() {
        this.datareceiver = null;
        this.datatransmitter = null;
        this.maxHeaderCount = -1;
        this.maxLineLen = -1;
        this.maxGarbageLines = -1;
        this.buffer = new CharArrayBuffer(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        this.entityserializer = createEntitySerializer();
        this.entitydeserializer = createEntityDeserializer();
        this.responsefactory = createHttpResponseFactory();
    }

    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new LaxContentLengthStrategy());
    }

    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }

    protected HttpResponseFactory createHttpResponseFactory() {
        return new DefaultHttpResponseFactory();
    }

    protected void init(HttpDataReceiver datareceiver, HttpDataTransmitter datatransmitter, HttpParams params) {
        if (datareceiver == null) {
            throw new IllegalArgumentException("HTTP data receiver may not be null");
        } else if (datatransmitter == null) {
            throw new IllegalArgumentException("HTTP data transmitter may not be null");
        } else {
            this.datareceiver = datareceiver;
            this.datatransmitter = datatransmitter;
            this.maxHeaderCount = params.getIntParameter(HttpConnectionParams.MAX_HEADER_COUNT, -1);
            this.maxLineLen = params.getIntParameter(HttpConnectionParams.MAX_LINE_LENGTH, -1);
            this.maxGarbageLines = params.getIntParameter(HttpConnectionParams.MAX_STATUS_LINE_GARBAGE, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            this.metrics = new HttpConnectionMetricsImpl(datareceiver.getMetrics(), datatransmitter.getMetrics());
        }
    }

    public boolean isResponseAvailable(int timeout) throws IOException {
        assertOpen();
        return this.datareceiver.isDataAvailable(timeout);
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        sendRequestLine(request);
        sendRequestHeaders(request);
        this.metrics.incrementRequestCount();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        if (request.getEntity() != null) {
            this.entityserializer.serialize(this.datatransmitter, request, request.getEntity());
        }
    }

    protected void doFlush() throws IOException {
        this.datatransmitter.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    protected void sendRequestLine(HttpRequest request) throws HttpException, IOException {
        this.buffer.clear();
        BasicRequestLine.format(this.buffer, request.getRequestLine());
        this.datatransmitter.writeLine(this.buffer);
    }

    protected void sendRequestHeaders(HttpRequest request) throws HttpException, IOException {
        Iterator it = request.headerIterator();
        while (it.hasNext()) {
            Header header = (Header) it.next();
            if (header instanceof BufferedHeader) {
                this.datatransmitter.writeLine(((BufferedHeader) header).getBuffer());
            } else {
                this.buffer.clear();
                BasicHeader.format(this.buffer, header);
                this.datatransmitter.writeLine(this.buffer);
            }
        }
        this.buffer.clear();
        this.datatransmitter.writeLine(this.buffer);
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        assertOpen();
        HttpResponse response = readResponseStatusLine();
        readResponseHeaders(response);
        if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK) {
            this.metrics.incrementResponseCount();
        }
        return response;
    }

    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        assertOpen();
        response.setEntity(this.entitydeserializer.deserialize(this.datareceiver, response));
    }

    protected static boolean startsWithHTTP(CharArrayBuffer buffer) {
        int i = 0;
        while (HTTP.isWhitespace(buffer.charAt(i))) {
            try {
                i++;
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        }
        if (buffer.charAt(i) == 'H' && buffer.charAt(i + 1) == 'T' && buffer.charAt(i + 2) == 'T' && buffer.charAt(i + 3) == 'P') {
            return true;
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected org.apache.http.HttpResponse readResponseStatusLine() throws org.apache.http.HttpException, java.io.IOException {
        /*
        r6 = this;
        r5 = -1;
        r3 = r6.buffer;
        r3.clear();
        r0 = 0;
    L_0x0007:
        r3 = r6.datareceiver;
        r4 = r6.buffer;
        r1 = r3.readLine(r4);
        if (r1 != r5) goto L_0x001b;
    L_0x0011:
        if (r0 != 0) goto L_0x001b;
    L_0x0013:
        r3 = new org.apache.http.NoHttpResponseException;
        r4 = "The target server failed to respond";
        r3.<init>(r4);
        throw r3;
    L_0x001b:
        r3 = r6.buffer;
        r3 = startsWithHTTP(r3);
        if (r3 == 0) goto L_0x0038;
    L_0x0023:
        r3 = r6.buffer;
        r4 = 0;
        r5 = r6.buffer;
        r5 = r5.length();
        r2 = org.apache.http.message.BasicStatusLine.parse(r3, r4, r5);
        r3 = r6.responsefactory;
        r4 = 0;
        r3 = r3.newHttpResponse(r2, r4);
        return r3;
    L_0x0038:
        if (r1 == r5) goto L_0x003e;
    L_0x003a:
        r3 = r6.maxGarbageLines;
        if (r0 < r3) goto L_0x0046;
    L_0x003e:
        r3 = new org.apache.http.ProtocolException;
        r4 = "The server failed to respond with a valid HTTP response";
        r3.<init>(r4);
        throw r3;
    L_0x0046:
        r0 = r0 + 1;
        goto L_0x0007;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.AbstractHttpClientConnection.readResponseStatusLine():org.apache.http.HttpResponse");
    }

    protected void readResponseHeaders(HttpResponse response) throws HttpException, IOException {
        response.setHeaders(HeaderUtils.parseHeaders(this.datareceiver, this.maxHeaderCount, this.maxLineLen));
    }

    public boolean isStale() {
        assertOpen();
        try {
            this.datareceiver.isDataAvailable(1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}

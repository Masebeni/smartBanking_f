package org.apache.http.impl;

import android.support.v4.media.TransportMediator;
import java.io.IOException;
import java.util.Iterator;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.io.HttpDataReceiver;
import org.apache.http.io.HttpDataTransmitter;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.message.BufferedHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.HeaderUtils;

public abstract class AbstractHttpServerConnection implements HttpServerConnection {
    private final CharArrayBuffer buffer;
    private HttpDataReceiver datareceiver;
    private HttpDataTransmitter datatransmitter;
    private final EntityDeserializer entitydeserializer;
    private final EntitySerializer entityserializer;
    private int maxHeaderCount;
    private int maxLineLen;
    private HttpConnectionMetricsImpl metrics;
    private final HttpRequestFactory requestfactory;

    protected abstract void assertOpen() throws IllegalStateException;

    public AbstractHttpServerConnection() {
        this.datareceiver = null;
        this.datatransmitter = null;
        this.maxHeaderCount = -1;
        this.maxLineLen = -1;
        this.buffer = new CharArrayBuffer(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        this.entityserializer = createEntitySerializer();
        this.entitydeserializer = createEntityDeserializer();
        this.requestfactory = createHttpRequestFactory();
    }

    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new LaxContentLengthStrategy());
    }

    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }

    protected HttpRequestFactory createHttpRequestFactory() {
        return new DefaultHttpRequestFactory();
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
            this.metrics = new HttpConnectionMetricsImpl(datareceiver.getMetrics(), datatransmitter.getMetrics());
        }
    }

    public HttpRequest receiveRequestHeader() throws HttpException, IOException {
        assertOpen();
        HttpRequest request = receiveRequestLine();
        receiveRequestHeaders(request);
        this.metrics.incrementRequestCount();
        return request;
    }

    public void receiveRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        request.setEntity(this.entitydeserializer.deserialize(this.datareceiver, request));
    }

    protected HttpRequest receiveRequestLine() throws HttpException, IOException {
        this.buffer.clear();
        if (this.datareceiver.readLine(this.buffer) == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        return this.requestfactory.newHttpRequest(BasicRequestLine.parse(this.buffer, 0, this.buffer.length()));
    }

    protected void receiveRequestHeaders(HttpRequest request) throws HttpException, IOException {
        request.setHeaders(HeaderUtils.parseHeaders(this.datareceiver, this.maxHeaderCount, this.maxLineLen));
    }

    protected void doFlush() throws IOException {
        this.datatransmitter.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        assertOpen();
        sendResponseStatusLine(response);
        sendResponseHeaders(response);
        if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK) {
            this.metrics.incrementResponseCount();
        }
    }

    public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
        if (response.getEntity() != null) {
            this.entityserializer.serialize(this.datatransmitter, response, response.getEntity());
        }
    }

    protected void sendResponseStatusLine(HttpResponse response) throws HttpException, IOException {
        this.buffer.clear();
        BasicStatusLine.format(this.buffer, response.getStatusLine());
        this.datatransmitter.writeLine(this.buffer);
    }

    protected void sendResponseHeaders(HttpResponse response) throws HttpException, IOException {
        Iterator it = response.headerIterator();
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

package org.apache.http.util;

import android.support.v4.media.TransportMediator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class HeaderGroup {
    private List headers;

    public HeaderGroup() {
        this.headers = new ArrayList(16);
    }

    public void clear() {
        this.headers.clear();
    }

    public void addHeader(Header header) {
        if (header != null) {
            this.headers.add(header);
        }
    }

    public void removeHeader(Header header) {
        if (header != null) {
            this.headers.remove(header);
        }
    }

    public void updateHeader(Header header) {
        if (header != null) {
            for (int i = 0; i < this.headers.size(); i++) {
                if (((Header) this.headers.get(i)).getName().equalsIgnoreCase(header.getName())) {
                    this.headers.set(i, header);
                    return;
                }
            }
            this.headers.add(header);
        }
    }

    public void setHeaders(Header[] headers) {
        clear();
        if (headers != null) {
            for (Object add : headers) {
                this.headers.add(add);
            }
        }
    }

    public Header getCondensedHeader(String name) {
        Header[] headers = getHeaders(name);
        if (headers.length == 0) {
            return null;
        }
        if (headers.length == 1) {
            return headers[0];
        }
        CharArrayBuffer valueBuffer = new CharArrayBuffer(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        valueBuffer.append(headers[0].getValue());
        for (int i = 1; i < headers.length; i++) {
            valueBuffer.append(", ");
            valueBuffer.append(headers[i].getValue());
        }
        return new BasicHeader(name.toLowerCase(), valueBuffer.toString());
    }

    public Header[] getHeaders(String name) {
        ArrayList headersFound = new ArrayList();
        for (int i = 0; i < this.headers.size(); i++) {
            Header header = (Header) this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                headersFound.add(header);
            }
        }
        return (Header[]) headersFound.toArray(new Header[headersFound.size()]);
    }

    public Header getFirstHeader(String name) {
        for (int i = 0; i < this.headers.size(); i++) {
            Header header = (Header) this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public Header getLastHeader(String name) {
        for (int i = this.headers.size() - 1; i >= 0; i--) {
            Header header = (Header) this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public Header[] getAllHeaders() {
        return (Header[]) this.headers.toArray(new Header[this.headers.size()]);
    }

    public boolean containsHeader(String name) {
        for (int i = 0; i < this.headers.size(); i++) {
            if (((Header) this.headers.get(i)).getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Iterator iterator() {
        return this.headers.iterator();
    }
}

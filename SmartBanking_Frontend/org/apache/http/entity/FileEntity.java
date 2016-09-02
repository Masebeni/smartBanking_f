package org.apache.http.entity;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileEntity extends AbstractHttpEntity {
    private final File file;

    public FileEntity(File file, String contentType) {
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        setContentType(contentType);
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return this.file.length();
    }

    public InputStream getContent() throws IOException {
        return new FileInputStream(this.file);
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = new FileInputStream(this.file);
        try {
            byte[] tmp = new byte[AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD];
            while (true) {
                int l = instream.read(tmp);
                if (l == -1) {
                    break;
                }
                outstream.write(tmp, 0, l);
            }
            outstream.flush();
        } finally {
            instream.close();
        }
    }

    public boolean isStreaming() {
        return false;
    }
}

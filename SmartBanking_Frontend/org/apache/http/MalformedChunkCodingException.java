package org.apache.http;

import java.io.IOException;

public class MalformedChunkCodingException extends IOException {
    static final long serialVersionUID = 3138679343859749668L;

    public MalformedChunkCodingException(String message) {
        super(message);
    }
}

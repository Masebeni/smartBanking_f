package org.apache.http;

public class UnsupportedHttpVersionException extends ProtocolException {
    static final long serialVersionUID = 6838964812421632743L;

    public UnsupportedHttpVersionException(String message) {
        super(message);
    }
}

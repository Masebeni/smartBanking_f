package org.apache.http.client;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.util.CharArrayBuffer;

public class AuthState {
    private AuthScheme authScheme;
    private AuthScope authScope;
    private Credentials credentials;

    public AuthState() {
        this.authScheme = null;
        this.authScope = null;
        this.credentials = null;
    }

    public void invalidate() {
        this.authScheme = null;
        this.authScope = null;
        this.credentials = null;
    }

    public void setAuthScheme(AuthScheme authScheme) {
        if (authScheme == null) {
            invalidate();
        } else {
            this.authScheme = authScheme;
        }
    }

    public AuthScheme getAuthScheme() {
        return this.authScheme;
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public AuthScope getAuthScope() {
        return this.authScope;
    }

    public void setAuthScope(AuthScope authScope) {
        this.authScope = authScope;
    }

    public String toString() {
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        buffer.append("auth scope [");
        buffer.append(this.authScope);
        buffer.append("]; credentials set [");
        buffer.append(this.credentials != null ? "true" : "false");
        buffer.append("]");
        return buffer.toString();
    }
}

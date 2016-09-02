package org.apache.http.auth;

public interface Credentials {
    String getPassword();

    String getPrincipalName();
}

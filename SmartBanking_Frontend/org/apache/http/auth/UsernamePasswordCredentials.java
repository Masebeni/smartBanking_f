package org.apache.http.auth;

import org.apache.http.util.LangUtils;

public class UsernamePasswordCredentials implements Credentials {
    private String password;
    private String userName;

    public UsernamePasswordCredentials(String usernamePassword) {
        if (usernamePassword == null) {
            throw new IllegalArgumentException("Username:password string may not be null");
        }
        int atColon = usernamePassword.indexOf(58);
        if (atColon >= 0) {
            this.userName = usernamePassword.substring(0, atColon);
            this.password = usernamePassword.substring(atColon + 1);
            return;
        }
        this.userName = usernamePassword;
    }

    public UsernamePasswordCredentials(String userName, String password) {
        if (userName == null) {
            throw new IllegalArgumentException("Username may not be null");
        }
        this.userName = userName;
        this.password = password;
    }

    public String getPrincipalName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String toText() {
        return toString();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(this.userName);
        result.append(":");
        result.append(this.password == null ? "null" : this.password);
        return result.toString();
    }

    public int hashCode() {
        return LangUtils.hashCode(LangUtils.hashCode(17, this.userName), this.password);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        UsernamePasswordCredentials that = (UsernamePasswordCredentials) o;
        if (LangUtils.equals(this.userName, that.userName) && LangUtils.equals(this.password, that.password)) {
            return true;
        }
        return false;
    }
}

package org.apache.http.client;

import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.protocol.HttpContext;

public interface AuthenticationHandler {
    Map getProxyChallenges(HttpResponse httpResponse, HttpContext httpContext) throws MalformedChallengeException;

    Map getTargetChallenges(HttpResponse httpResponse, HttpContext httpContext) throws MalformedChallengeException;

    boolean isProxyAuthenticationRequested(HttpResponse httpResponse, HttpContext httpContext);

    boolean isTargetAuthenticationRequested(HttpResponse httpResponse, HttpContext httpContext);

    AuthScheme selectScheme(Map map, HttpResponse httpResponse, HttpContext httpContext) throws AuthenticationException;
}

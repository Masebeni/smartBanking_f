package org.apache.http.impl.client;

import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v4.widget.SwipeRefreshLayout;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthState;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.ClientRequestDirector;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpState;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RoutedRequest;
import org.apache.http.client.RoutedRequest.Impl;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpRoute;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.RouteDirector;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsLinker;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExecutionContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.CharArrayBuffer;

public class DefaultClientRequestDirector implements ClientRequestDirector {
    private static final Log LOG;
    static Class class$org$apache$http$impl$client$DefaultClientRequestDirector;
    private final AuthenticationHandler authHandler;
    protected final ClientConnectionManager connManager;
    protected final HttpProcessor httpProcessor;
    protected ManagedClientConnection managedConn;
    private int maxRedirects;
    protected final HttpParams params;
    private final AuthState proxyAuthState;
    private int redirectCount;
    protected final RedirectHandler redirectHandler;
    protected final HttpRequestExecutor requestExec;
    protected final HttpRequestRetryHandler retryHandler;
    protected final ConnectionReuseStrategy reuseStrategy;
    private final AuthState targetAuthState;

    static {
        Class class$;
        if (class$org$apache$http$impl$client$DefaultClientRequestDirector == null) {
            class$ = class$("org.apache.http.impl.client.DefaultClientRequestDirector");
            class$org$apache$http$impl$client$DefaultClientRequestDirector = class$;
        } else {
            class$ = class$org$apache$http$impl$client$DefaultClientRequestDirector;
        }
        LOG = LogFactory.getLog(class$);
    }

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError().initCause(x1);
        }
    }

    public DefaultClientRequestDirector(ClientConnectionManager conman, ConnectionReuseStrategy reustrat, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectHandler redirectHandler, AuthenticationHandler authHandler, HttpParams params) {
        if (conman == null) {
            throw new IllegalArgumentException("Client connection manager may not be null");
        } else if (reustrat == null) {
            throw new IllegalArgumentException("Connection reuse strategy may not be null");
        } else if (httpProcessor == null) {
            throw new IllegalArgumentException("HTTP protocol processor may not be null");
        } else if (retryHandler == null) {
            throw new IllegalArgumentException("HTTP request retry handler may not be null");
        } else if (redirectHandler == null) {
            throw new IllegalArgumentException("Redirect handler may not be null");
        } else if (authHandler == null) {
            throw new IllegalArgumentException("Authentication handler may not be null");
        } else if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            this.connManager = conman;
            this.reuseStrategy = reustrat;
            this.httpProcessor = httpProcessor;
            this.retryHandler = retryHandler;
            this.redirectHandler = redirectHandler;
            this.authHandler = authHandler;
            this.params = params;
            this.requestExec = new HttpRequestExecutor();
            this.managedConn = null;
            this.redirectCount = 0;
            this.maxRedirects = this.params.getIntParameter(HttpClientParams.MAX_REDIRECTS, 100);
            this.targetAuthState = new AuthState();
            this.proxyAuthState = new AuthState();
        }
    }

    public ManagedClientConnection getConnection() {
        return this.managedConn;
    }

    private RequestWrapper wrapRequest(HttpRequest request) throws ProtocolException {
        try {
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return new RequestWrapper(request);
            }
            return new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest) request);
        } catch (URISyntaxException ex) {
            throw new ProtocolException(new StringBuffer().append("Invalid URI: ").append(request.getRequestLine().getUri()).toString(), ex);
        }
    }

    private void rewriteRequestURI(RequestWrapper request, HttpRoute route) throws ProtocolException {
        try {
            URI uri = request.getURI();
            if (route.getProxyHost() == null || route.isTunnelled()) {
                if (uri.isAbsolute()) {
                    request.setURI(new URI(null, null, null, -1, uri.getPath(), uri.getQuery(), uri.getFragment()));
                    return;
                }
            } else if (!uri.isAbsolute()) {
                HttpHost target = route.getTargetHost();
                request.setURI(new URI(target.getSchemeName(), null, target.getHostName(), target.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment()));
                return;
            }
            URI uri2 = uri;
        } catch (URISyntaxException ex) {
            throw new ProtocolException(new StringBuffer().append("Invalid URI: ").append(request.getRequestLine().getUri()).toString(), ex);
        }
    }

    public HttpResponse execute(RoutedRequest roureq, HttpContext context) throws HttpException, IOException {
        HttpRequest orig = roureq.getRequest();
        HttpParamsLinker.link(orig, this.params);
        Collection<Header> defHeaders = (Collection) orig.getParams().getParameter(HttpClientParams.DEFAULT_HEADERS);
        if (defHeaders != null) {
            for (Header addHeader : defHeaders) {
                orig.addHeader(addHeader);
            }
        }
        long timeout = HttpClientParams.getConnectionManagerTimeout(this.params);
        int execCount = 0;
        HttpResponse response = null;
        boolean done = false;
        while (!done) {
            try {
                HttpRoute route = roureq.getRoute();
                if (this.managedConn == null) {
                    this.managedConn = allocateConnection(route, timeout);
                }
                if (!this.managedConn.isOpen()) {
                    this.managedConn.open(route, context, this.params);
                }
                try {
                    establishRoute(route, context);
                    if (HttpConnectionParams.isStaleCheckingEnabled(this.params)) {
                        LOG.debug("Stale connection check");
                        if (this.managedConn.isStale()) {
                            LOG.debug("Stale connection detected");
                            this.managedConn.close();
                        }
                    }
                    RequestWrapper request = wrapRequest(roureq.getRequest());
                    rewriteRequestURI(request, route);
                    HttpHost target = (HttpHost) request.getParams().getParameter(HttpClientParams.VIRTUAL_HOST);
                    if (target == null) {
                        target = route.getTargetHost();
                    }
                    HttpHost proxy = route.getProxyHost();
                    context.setAttribute(HttpExecutionContext.HTTP_TARGET_HOST, target);
                    context.setAttribute(HttpExecutionContext.HTTP_PROXY_HOST, proxy);
                    context.setAttribute(HttpContext.HTTP_CONNECTION, this.managedConn);
                    context.setAttribute(HttpClientContext.TARGET_AUTH_STATE, this.targetAuthState);
                    context.setAttribute(HttpClientContext.PROXY_AUTH_STATE, this.proxyAuthState);
                    this.requestExec.preProcess(request, this.httpProcessor, context);
                    if (orig instanceof AbortableHttpRequest) {
                        ((AbortableHttpRequest) orig).setReleaseTrigger(this.managedConn);
                    }
                    context.setAttribute(HttpContext.HTTP_REQUEST, request);
                    execCount++;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(new StringBuffer().append("Attempt ").append(execCount).append(" to execute request").toString());
                    }
                    response = this.requestExec.execute(request, this.managedConn, context);
                    HttpParamsLinker.link(request, this.params);
                    this.requestExec.postProcess(response, this.httpProcessor, context);
                    RoutedRequest followup = handleResponse(roureq, request, response, context);
                    if (followup == null) {
                        done = true;
                    } else {
                        if (this.reuseStrategy.keepAlive(response, context)) {
                            LOG.debug("Connection kept alive");
                            HttpEntity entity = response.getEntity();
                            if (entity != null) {
                                entity.consumeContent();
                            }
                        } else {
                            this.managedConn.close();
                        }
                        if (this.managedConn != null) {
                            if (!followup.getRoute().equals(roureq.getRoute())) {
                                this.connManager.releaseConnection(this.managedConn);
                                this.managedConn = null;
                            }
                        }
                        roureq = followup;
                    }
                } catch (TunnelRefusedException ex) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(ex.getMessage());
                    }
                    response = ex.getResponse();
                }
            } catch (IOException ex2) {
                LOG.debug("Closing the connection.");
                this.managedConn.close();
                if (this.retryHandler.retryRequest(ex2, execCount, context)) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info(new StringBuffer().append("I/O exception (").append(ex2.getClass().getName()).append(") caught when processing request: ").append(ex2.getMessage()).toString());
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(ex2.getMessage(), ex2);
                    }
                    LOG.info("Retrying request");
                } else {
                    throw ex2;
                }
            } catch (HttpException ex3) {
                abortConnection();
                throw ex3;
            } catch (RuntimeException ex4) {
                abortConnection();
                throw ex4;
            } catch (IOException ex22) {
                abortConnection();
                throw ex22;
            }
        }
        boolean reuse = this.reuseStrategy.keepAlive(response, context);
        if (response == null || response.getEntity() == null || !response.getEntity().isStreaming()) {
            if (reuse) {
                this.managedConn.markReusable();
            }
            this.connManager.releaseConnection(this.managedConn);
            this.managedConn = null;
        } else {
            response.setEntity(new BasicManagedEntity(response.getEntity(), this.managedConn, reuse));
        }
        return response;
    }

    protected ManagedClientConnection allocateConnection(HttpRoute route, long timeout) throws HttpException, ConnectionPoolTimeoutException {
        return this.connManager.getConnection(route, timeout);
    }

    protected void establishRoute(HttpRoute route, HttpContext context) throws HttpException, IOException {
        RouteDirector rowdy = new RouteDirector();
        int step;
        do {
            HttpRoute fact = this.managedConn.getRoute();
            step = rowdy.nextStep(route, fact);
            switch (step) {
                case ExploreByTouchHelper.HOST_ID /*-1*/:
                    throw new IllegalStateException(new StringBuffer().append("Unable to establish route.\nplanned = ").append(route).append("\ncurrent = ").append(fact).toString());
                case SwipeRefreshLayout.LARGE /*0*/:
                    break;
                case SwipeRefreshLayout.DEFAULT /*1*/:
                case DrawerLayout.STATE_SETTLING /*2*/:
                    this.managedConn.open(route, context, this.params);
                    continue;
                case WearableExtender.SIZE_MEDIUM /*3*/:
                    boolean secure = createTunnel(route, context);
                    LOG.debug("Tunnel created");
                    this.managedConn.tunnelCreated(secure, this.params);
                    continue;
                case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                    throw new UnsupportedOperationException("Proxy chains are not supported.");
                case WearableExtender.SIZE_FULL_SCREEN /*5*/:
                    this.managedConn.layerProtocol(context, this.params);
                    continue;
                default:
                    throw new IllegalStateException(new StringBuffer().append("Unknown step indicator ").append(step).append(" from RouteDirector.").toString());
            }
        } while (step > 0);
    }

    protected boolean createTunnel(HttpRoute route, HttpContext context) throws HttpException, IOException {
        HttpEntity entity;
        HttpHost proxy = route.getProxyHost();
        HttpHost target = route.getTargetHost();
        HttpResponse response = null;
        boolean done = false;
        while (!done) {
            done = true;
            if (!this.managedConn.isOpen()) {
                this.managedConn.open(route, context, this.params);
            }
            HttpRequest connect = createConnectRequest(route, context);
            String agent = HttpProtocolParams.getUserAgent(this.params);
            if (agent != null) {
                connect.addHeader(HTTP.USER_AGENT, agent);
            }
            connect.addHeader(HTTP.TARGET_HOST, target.toHostString());
            AuthScheme authScheme = this.proxyAuthState.getAuthScheme();
            AuthScope authScope = this.proxyAuthState.getAuthScope();
            Credentials creds = this.proxyAuthState.getCredentials();
            if (!(creds == null || (authScope == null && authScheme.isConnectionBased()))) {
                try {
                    connect.addHeader(authScheme.authenticate(creds, connect));
                } catch (AuthenticationException ex) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(new StringBuffer().append("Proxy authentication error: ").append(ex.getMessage()).toString());
                    }
                }
            }
            response = this.requestExec.execute(connect, this.managedConn, context);
            if (response.getStatusLine().getStatusCode() < 200) {
                throw new HttpException(new StringBuffer().append("Unexpected response to CONNECT request: ").append(response.getStatusLine()).toString());
            }
            HttpState state = (HttpState) context.getAttribute(HttpClientContext.HTTP_STATE);
            if (state != null) {
                if (HttpClientParams.isAuthenticating(this.params)) {
                    if (this.authHandler.isProxyAuthenticationRequested(response, context)) {
                        LOG.debug("Proxy requested authentication");
                        Map challenges = this.authHandler.getProxyChallenges(response, context);
                        try {
                            processChallenges(challenges, this.proxyAuthState, response, context);
                        } catch (AuthenticationException ex2) {
                            if (LOG.isWarnEnabled()) {
                                LOG.warn(new StringBuffer().append("Authentication error: ").append(ex2.getMessage()).toString());
                                break;
                            }
                        }
                        updateAuthState(this.proxyAuthState, proxy, state);
                        if (this.proxyAuthState.getCredentials() != null) {
                            done = false;
                            if (this.reuseStrategy.keepAlive(response, context)) {
                                LOG.debug("Connection kept alive");
                                entity = response.getEntity();
                                if (entity != null) {
                                    entity.consumeContent();
                                }
                            } else {
                                this.managedConn.close();
                            }
                        }
                    } else {
                        this.proxyAuthState.setAuthScope(null);
                    }
                } else {
                    continue;
                }
            }
        }
        if (response.getStatusLine().getStatusCode() > 299) {
            entity = response.getEntity();
            if (entity != null) {
                response.setEntity(new BufferedHttpEntity(entity));
            }
            this.managedConn.close();
            throw new TunnelRefusedException(new StringBuffer().append("CONNECT refused by proxy: ").append(response.getStatusLine()).toString(), response);
        }
        this.managedConn.markReusable();
        return false;
    }

    protected HttpRequest createConnectRequest(HttpRoute route, HttpContext context) {
        HttpHost target = route.getTargetHost();
        String host = target.getHostName();
        int port = target.getPort();
        if (port < 0) {
            port = this.connManager.getSchemeRegistry().getScheme(target.getSchemeName()).getDefaultPort();
        }
        CharArrayBuffer buffer = new CharArrayBuffer(host.length() + 6);
        buffer.append(host);
        buffer.append(":");
        buffer.append(Integer.toString(port));
        return new BasicHttpRequest("CONNECT", buffer.toString(), HttpProtocolParams.getVersion(this.params));
    }

    protected RoutedRequest handleResponse(RoutedRequest roureq, HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRoute route = roureq.getRoute();
        HttpHost target = route.getTargetHost();
        HttpHost proxy = route.getProxyHost();
        InetAddress localAddress = route.getLocalAddress();
        HttpParams params = request.getParams();
        if (!HttpClientParams.isRedirecting(params) || !this.redirectHandler.isRedirectRequested(response, context)) {
            HttpState state = (HttpState) context.getAttribute(HttpClientContext.HTTP_STATE);
            if (state != null && HttpClientParams.isAuthenticating(params)) {
                if (this.authHandler.isTargetAuthenticationRequested(response, context)) {
                    target = (HttpHost) context.getAttribute(HttpExecutionContext.HTTP_TARGET_HOST);
                    if (target == null) {
                        target = route.getTargetHost();
                    }
                    LOG.debug("Target requested authentication");
                    try {
                        processChallenges(this.authHandler.getTargetChallenges(response, context), this.targetAuthState, response, context);
                    } catch (AuthenticationException ex) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn(new StringBuffer().append("Authentication error: ").append(ex.getMessage()).toString());
                            return null;
                        }
                    }
                    updateAuthState(this.targetAuthState, target, state);
                    if (this.targetAuthState.getCredentials() == null) {
                        return null;
                    }
                    return roureq;
                }
                this.targetAuthState.setAuthScope(null);
                if (this.authHandler.isProxyAuthenticationRequested(response, context)) {
                    LOG.debug("Proxy requested authentication");
                    try {
                        processChallenges(this.authHandler.getProxyChallenges(response, context), this.proxyAuthState, response, context);
                    } catch (AuthenticationException ex2) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn(new StringBuffer().append("Authentication error: ").append(ex2.getMessage()).toString());
                            return null;
                        }
                    }
                    updateAuthState(this.proxyAuthState, proxy, state);
                    if (this.proxyAuthState.getCredentials() == null) {
                        return null;
                    }
                    return roureq;
                }
                this.proxyAuthState.setAuthScope(null);
            }
            return null;
        } else if (this.redirectCount >= this.maxRedirects) {
            throw new RedirectException(new StringBuffer().append("Maximum redirects (").append(this.maxRedirects).append(") exceeded").toString());
        } else {
            this.redirectCount++;
            try {
                boolean z;
                URI uri = this.redirectHandler.getLocationURI(response, context);
                HttpHost newTarget = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
                boolean isLayered = this.connManager.getSchemeRegistry().getScheme(newTarget.getSchemeName()).isLayered();
                boolean z2 = proxy != null;
                if (proxy != null) {
                    z = true;
                } else {
                    z = false;
                }
                HttpRoute newRoute = new HttpRoute(newTarget, localAddress, proxy, isLayered, z2, z);
                HttpGet redirect = new HttpGet(uri);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(new StringBuffer().append("Redirecting to '").append(uri).append("' via ").append(newRoute).toString());
                }
                return new Impl(redirect, newRoute);
            } catch (ProtocolException ex3) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(ex3.getMessage());
                }
                return null;
            }
        }
    }

    private void abortConnection() throws IOException {
        ManagedClientConnection mcc = this.managedConn;
        if (mcc != null) {
            this.managedConn = null;
            try {
                mcc.abortConnection();
            } catch (IOException ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(ex.getMessage(), ex);
                }
            }
        }
    }

    private void processChallenges(Map challenges, AuthState authState, HttpResponse response, HttpContext context) throws MalformedChallengeException, AuthenticationException {
        if (authState.getAuthScheme() == null) {
            authState.setAuthScheme(this.authHandler.selectScheme(challenges, response, context));
        }
        AuthScheme authscheme = authState.getAuthScheme();
        String id = authscheme.getSchemeName();
        Header challenge = (Header) challenges.get(id.toLowerCase());
        if (challenge == null) {
            throw new AuthenticationException(new StringBuffer().append(id).append(" authorization challenge expected, but not found").toString());
        }
        authscheme.processChallenge(challenge);
        LOG.debug("Authorization challenge processed");
    }

    private void updateAuthState(AuthState authState, HttpHost host, HttpState state) {
        String hostname = host.getHostName();
        int port = host.getPort();
        if (port < 0) {
            port = this.connManager.getSchemeRegistry().getScheme(host).getDefaultPort();
        }
        AuthScheme authScheme = authState.getAuthScheme();
        AuthScope authScope = new AuthScope(hostname, port, authScheme.getRealm(), authScheme.getSchemeName());
        if (LOG.isDebugEnabled()) {
            LOG.debug(new StringBuffer().append("Authentication scope: ").append(authScope).toString());
        }
        Credentials creds = authState.getCredentials();
        if (creds == null) {
            creds = state.getCredentials(authScope);
            if (LOG.isDebugEnabled()) {
                if (creds != null) {
                    LOG.debug("Found credentials");
                } else {
                    LOG.debug("Credentials not found");
                }
            }
        } else if (authScheme.isComplete()) {
            LOG.debug("Authentication failed");
            creds = null;
        }
        authState.setAuthScope(authScope);
        authState.setCredentials(creds);
    }
}

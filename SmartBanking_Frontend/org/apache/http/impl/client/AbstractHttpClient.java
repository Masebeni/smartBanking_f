package org.apache.http.impl.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.ClientRequestDirector;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpState;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RoutedRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestInterceptorList;
import org.apache.http.protocol.HttpResponseInterceptorList;

public abstract class AbstractHttpClient implements HttpClient, HttpRequestInterceptorList, HttpResponseInterceptorList {
    private AuthenticationHandler authHandler;
    private ClientConnectionManager connManager;
    private HttpContext defaultContext;
    private HttpParams defaultParams;
    private HttpState defaultState;
    private BasicHttpProcessor httpProcessor;
    private RedirectHandler redirectHandler;
    private HttpRequestRetryHandler retryHandler;
    private ConnectionReuseStrategy reuseStrategy;
    private AuthSchemeRegistry supportedAuthSchemes;
    private CookieSpecRegistry supportedCookieSpecs;

    protected abstract AuthSchemeRegistry createAuthSchemeRegistry();

    protected abstract AuthenticationHandler createAuthenticationHandler();

    protected abstract ClientConnectionManager createClientConnectionManager();

    protected abstract ConnectionReuseStrategy createConnectionReuseStrategy();

    protected abstract CookieSpecRegistry createCookieSpecRegistry();

    protected abstract HttpContext createHttpContext();

    protected abstract HttpParams createHttpParams();

    protected abstract BasicHttpProcessor createHttpProcessor();

    protected abstract HttpRequestRetryHandler createHttpRequestRetryHandler();

    protected abstract HttpState createHttpState();

    protected abstract RedirectHandler createRedirectHandler();

    protected abstract RoutedRequest determineRoute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws HttpException;

    protected abstract void populateContext(HttpContext httpContext);

    protected AbstractHttpClient(ClientConnectionManager conman, HttpParams params) {
        this.defaultParams = params;
        this.connManager = conman;
    }

    public final synchronized HttpParams getParams() {
        if (this.defaultParams == null) {
            this.defaultParams = createHttpParams();
        }
        return this.defaultParams;
    }

    public synchronized void setParams(HttpParams params) {
        this.defaultParams = params;
    }

    public final synchronized ClientConnectionManager getConnectionManager() {
        if (this.connManager == null) {
            this.connManager = createClientConnectionManager();
        }
        return this.connManager;
    }

    public final synchronized AuthSchemeRegistry getAuthSchemes() {
        if (this.supportedAuthSchemes == null) {
            this.supportedAuthSchemes = createAuthSchemeRegistry();
        }
        return this.supportedAuthSchemes;
    }

    public synchronized void setAuthSchemes(AuthSchemeRegistry authSchemeRegistry) {
        this.supportedAuthSchemes = authSchemeRegistry;
    }

    public final synchronized CookieSpecRegistry getCookieSpecs() {
        if (this.supportedCookieSpecs == null) {
            this.supportedCookieSpecs = createCookieSpecRegistry();
        }
        return this.supportedCookieSpecs;
    }

    public synchronized void setCookieSpecs(CookieSpecRegistry cookieSpecRegistry) {
        this.supportedCookieSpecs = cookieSpecRegistry;
    }

    public final synchronized ConnectionReuseStrategy getConnectionReuseStrategy() {
        if (this.reuseStrategy == null) {
            this.reuseStrategy = createConnectionReuseStrategy();
        }
        return this.reuseStrategy;
    }

    public synchronized void setReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
    }

    public final synchronized HttpRequestRetryHandler getHttpRequestRetryHandler() {
        if (this.retryHandler == null) {
            this.retryHandler = createHttpRequestRetryHandler();
        }
        return this.retryHandler;
    }

    public synchronized void setHttpRequestRetryHandler(HttpRequestRetryHandler retryHandler) {
        this.retryHandler = retryHandler;
    }

    public final synchronized RedirectHandler getRedirectHandler() {
        if (this.redirectHandler == null) {
            this.redirectHandler = createRedirectHandler();
        }
        return this.redirectHandler;
    }

    public synchronized void setRedirectHandler(RedirectHandler redirectHandler) {
        this.redirectHandler = redirectHandler;
    }

    public final synchronized AuthenticationHandler getAuthenticationHandler() {
        if (this.authHandler == null) {
            this.authHandler = createAuthenticationHandler();
        }
        return this.authHandler;
    }

    public synchronized void setAuthenticationHandler(AuthenticationHandler authHandler) {
        this.authHandler = authHandler;
    }

    public final synchronized HttpState getState() {
        if (this.defaultState == null) {
            this.defaultState = createHttpState();
        }
        return this.defaultState;
    }

    public synchronized void setState(HttpState state) {
        this.defaultState = state;
    }

    protected final synchronized BasicHttpProcessor getHttpProcessor() {
        if (this.httpProcessor == null) {
            this.httpProcessor = createHttpProcessor();
        }
        return this.httpProcessor;
    }

    public final synchronized HttpContext getDefaultContext() {
        if (this.defaultContext == null) {
            this.defaultContext = createHttpContext();
        }
        populateContext(this.defaultContext);
        return this.defaultContext;
    }

    public synchronized void addResponseInterceptor(HttpResponseInterceptor itcp) {
        getHttpProcessor().addInterceptor(itcp);
    }

    public synchronized void clearResponseInterceptors() {
        getHttpProcessor().clearResponseInterceptors();
    }

    public synchronized HttpResponseInterceptor getResponseInterceptor(int index) {
        return getHttpProcessor().getResponseInterceptor(index);
    }

    public synchronized int getResponseInterceptorCount() {
        return getHttpProcessor().getResponseInterceptorCount();
    }

    public synchronized void addRequestInterceptor(HttpRequestInterceptor itcp) {
        getHttpProcessor().addInterceptor(itcp);
    }

    public synchronized void clearRequestInterceptors() {
        getHttpProcessor().clearRequestInterceptors();
    }

    public synchronized HttpRequestInterceptor getRequestInterceptor(int index) {
        return getHttpProcessor().getRequestInterceptor(index);
    }

    public synchronized int getRequestInterceptorCount() {
        return getHttpProcessor().getRequestInterceptorCount();
    }

    public synchronized void setInterceptors(List itcps) {
        getHttpProcessor().setInterceptors(itcps);
    }

    public final HttpResponse execute(HttpUriRequest request) throws HttpException, IOException {
        return execute(request, null);
    }

    public final HttpResponse execute(HttpUriRequest request, HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null.");
        }
        HttpHost target = null;
        URI requestURI = request.getURI();
        if (requestURI.isAbsolute()) {
            target = new HttpHost(requestURI.getHost(), requestURI.getPort(), requestURI.getScheme());
        }
        synchronized (this) {
            if (context == null) {
                context = new HttpClientContext(getDefaultContext());
            }
        }
        return execute(determineRoute(target, request, context), context);
    }

    public HttpResponse execute(RoutedRequest roureq) throws HttpException, IOException {
        return execute(roureq, null);
    }

    public final HttpResponse execute(RoutedRequest roureq, HttpContext context) throws HttpException, IOException {
        Throwable th;
        if (roureq == null) {
            throw new IllegalArgumentException("Routed request must not be null.");
        } else if (roureq.getRequest() == null) {
            throw new IllegalArgumentException("Request must not be null.");
        } else if (roureq.getRoute() == null) {
            throw new IllegalArgumentException("Route must not be null.");
        } else {
            synchronized (this) {
                ClientRequestDirector clientRequestDirector;
                if (context == null) {
                    try {
                        context = new HttpClientContext(getDefaultContext());
                    } catch (Throwable th2) {
                        th = th2;
                        clientRequestDirector = null;
                        throw th;
                    }
                }
                clientRequestDirector = new DefaultClientRequestDirector(getConnectionManager(), getConnectionReuseStrategy(), getHttpProcessor().copy(), getHttpRequestRetryHandler(), getRedirectHandler(), getAuthenticationHandler(), getParams());
                try {
                    return clientRequestDirector.execute(roureq, context);
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
    }
}

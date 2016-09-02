package org.apache.http.impl.client;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpState;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RoutedRequest;
import org.apache.http.client.RoutedRequest.Impl;
import org.apache.http.client.VersionInfo;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.client.protocol.RequestTargetAuthentication;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.HttpRoute;
import org.apache.http.conn.PlainSocketFactory;
import org.apache.http.conn.Scheme;
import org.apache.http.conn.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
import org.apache.http.impl.cookie.RFC2109SpecFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.protocol.SyncHttpExecutionContext;

public class DefaultHttpClient extends AbstractHttpClient {
    public DefaultHttpClient(ClientConnectionManager conman, HttpParams params) {
        super(conman, params);
    }

    public DefaultHttpClient(HttpParams params) {
        super(null, params);
    }

    public DefaultHttpClient() {
        super(null, null);
    }

    protected HttpParams createHttpParams() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.ISO_8859_1);
        HttpProtocolParams.setUserAgent(params, new StringBuffer().append("Apache-HttpClient/").append(VersionInfo.getReleaseVersion()).append(" (java 1.4)").toString());
        HttpProtocolParams.setUseExpectContinue(params, true);
        return params;
    }

    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        HttpParams params = getParams();
        String className = (String) params.getParameter(HttpClientParams.CONNECTION_MANAGER_FACTORY);
        if (className == null) {
            return new SingleClientConnManager(getParams(), registry);
        }
        try {
            return ((ClientConnectionManagerFactory) Class.forName(className).newInstance()).newInstance(params, registry);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(new StringBuffer().append("Invalid class name: ").append(className).toString());
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        } catch (InstantiationException ex2) {
            throw new InstantiationError(ex2.getMessage());
        }
    }

    protected HttpContext createHttpContext() {
        return new SyncHttpExecutionContext(null);
    }

    protected ConnectionReuseStrategy createConnectionReuseStrategy() {
        return new DefaultConnectionReuseStrategy();
    }

    protected AuthSchemeRegistry createAuthSchemeRegistry() {
        AuthSchemeRegistry registry = new AuthSchemeRegistry();
        registry.register(AuthPolicy.BASIC, new BasicSchemeFactory());
        registry.register(AuthPolicy.DIGEST, new DigestSchemeFactory());
        return registry;
    }

    protected CookieSpecRegistry createCookieSpecRegistry() {
        CookieSpecRegistry registry = new CookieSpecRegistry();
        registry.register(CookiePolicy.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory());
        registry.register(CookiePolicy.NETSCAPE, new NetscapeDraftSpecFactory());
        registry.register(CookiePolicy.RFC_2109, new RFC2109SpecFactory());
        return registry;
    }

    protected BasicHttpProcessor createHttpProcessor() {
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        httpproc.addInterceptor(new RequestConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());
        httpproc.addInterceptor(new RequestAddCookies());
        httpproc.addInterceptor(new ResponseProcessCookies());
        httpproc.addInterceptor(new RequestTargetAuthentication());
        httpproc.addInterceptor(new RequestProxyAuthentication());
        return httpproc;
    }

    protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
        return new DefaultHttpRequestRetryHandler();
    }

    protected RedirectHandler createRedirectHandler() {
        return new DefaultRedirectHandler();
    }

    protected AuthenticationHandler createAuthenticationHandler() {
        return new DefaultAuthenticationHandler();
    }

    protected HttpState createHttpState() {
        return new HttpState();
    }

    protected void populateContext(HttpContext context) {
        context.setAttribute(HttpClientContext.AUTHSCHEME_REGISTRY, getAuthSchemes());
        context.setAttribute(HttpClientContext.COOKIESPEC_REGISTRY, getCookieSpecs());
        context.setAttribute(HttpClientContext.HTTP_STATE, getState());
    }

    protected RoutedRequest determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        if (target == null) {
            target = (HttpHost) request.getParams().getParameter(HttpClientParams.DEFAULT_HOST);
        }
        if (target == null) {
            throw new IllegalStateException("Target host must not be null.");
        }
        HttpRoute route;
        HttpHost proxy = (HttpHost) request.getParams().getParameter(HttpClientParams.DEFAULT_PROXY);
        boolean secure = getConnectionManager().getSchemeRegistry().getScheme(target.getSchemeName()).isLayered();
        if (proxy == null) {
            route = new HttpRoute(target, null, secure);
        } else {
            route = new HttpRoute(target, null, proxy, secure);
        }
        return new Impl(request, route);
    }
}

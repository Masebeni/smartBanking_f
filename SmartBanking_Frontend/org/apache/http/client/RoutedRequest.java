package org.apache.http.client;

import org.apache.http.HttpRequest;
import org.apache.http.conn.HttpRoute;

public interface RoutedRequest {

    public static class Impl implements RoutedRequest {
        protected final HttpRequest request;
        protected final HttpRoute route;

        public Impl(HttpRequest req, HttpRoute rou) {
            this.request = req;
            this.route = rou;
        }

        public final HttpRequest getRequest() {
            return this.request;
        }

        public final HttpRoute getRoute() {
            return this.route;
        }
    }

    HttpRequest getRequest();

    HttpRoute getRoute();
}

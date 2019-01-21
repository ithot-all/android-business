package org.ithot.android.business.transmit.http.core;


import java.util.HashMap;
import java.util.Map;

public abstract class HTTPInterceptor implements IHTTPInterceptor {

    public void disconnected(Req req) {

    }

    public void pre(Req req) {

    }

    public Map<String, String> headers() {
        return new HashMap<>();
    }

    public void post(Req req, boolean cache) {

    }

    public void fail(Req req, int status, Map<String, String> headers, String body) {

    }
}

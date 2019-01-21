package org.ithot.android.business.transmit.http.core;

import java.util.Map;

public interface IHTTPInterceptor {

    void disconnected(Req req);

    void pre(Req req);

    Map<String, String> headers();

    void post(Req req, boolean cache);

    void fail(Req req, int status, Map<String, String> headers, String body);
}

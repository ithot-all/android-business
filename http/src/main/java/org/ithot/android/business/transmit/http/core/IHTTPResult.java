package org.ithot.android.business.transmit.http.core;

import java.util.Map;

public interface IHTTPResult {

    void success(Req req, int status, Map<String, String> headers, String body);

    void cache(Req req, String body);

    void fail(Req req, int status, Map<String, String> headers, String body);

    void disconnected(Req req);
}

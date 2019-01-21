package org.ithot.android.business.transmit.http.core;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class HTTPHandler extends TextHttpResponseHandler {

    private Req req;

    HTTPHandler(Req req) {
        this.req = req;
    }

    @Override
    public void onSuccess(int status, Header[] headers, final String body) {
        InnerUtils.print(body);
        Req.hook().post(req, false);
        if (req.method() == Method.GET) {
            req.res().success(req, status, InnerUtils.arrToMap(headers), body);
            if (req.policy() != Policy.NoCache) {
                Req.client().getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Req.cache().put(req.key(), body);
                    }
                });
            }
        } else {
            req.res().success(req, status, InnerUtils.arrToMap(headers), body);
        }
    }

    @Override
    public void onFailure(int status, Header[] headers, String body, Throwable throwable) {
        InnerUtils.error(throwable);
        InnerUtils.print(body);
        Req.hook().fail(req, status, InnerUtils.arrToMap(headers), body);
        req.res().fail(req, status, InnerUtils.arrToMap(headers), body);
    }
}

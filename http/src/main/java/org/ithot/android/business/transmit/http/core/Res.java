package org.ithot.android.business.transmit.http.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class Res<T> implements IHTTPResult {

    private Type getType() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType parameter = (ParameterizedType) type;
        return parameter.getActualTypeArguments()[0];
    }

    public abstract void ok(T res);

    private void full(int status, Map<String, String> headers, T res) {
        ok(res);
    }

    @Override
    public void cache(Req req, String body) {
        InnerUtils.print("cache|" + body);
        Type type;
        try {
            type = getType();
        } catch (Exception e) {
            type = null;
        }
        if (type != null && type == String.class) {
            full(0, null, (T) body);
            return;
        }
        T t = (T) Req.serializer().parse(body, type);
        full(0, null, t);
    }

    @Override
    public void disconnected(Req req) {
        Req.hook().disconnected(req);
    }

    @Override
    public void fail(Req req, int status, Map<String, String> headers, String body) {

    }

    @Override
    public void success(Req req, int status, Map<String, String> headers, String body) {
        Type type;
        try {
            type = getType();
        } catch (Exception e) {
            type = null;
        }
        if (type != null && type == String.class) {
            full(status, headers, (T) body);
            return;
        }
        T t = (T) Req.serializer().parse(body, type);
        full(status, headers, t);
        Req.hook().post(req, false);
    }
}

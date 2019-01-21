package org.ithot.android.business.transmit.http.core;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.ithot.android.business.transmit.http.cache.Builder;
import org.ithot.android.business.transmit.http.cache.CacheSerializer;
import org.ithot.android.business.transmit.http.cache.DualCache;
import org.ithot.android.business.transmit.http.cache.StringSerializer;
import org.ithot.android.serializerinterface.JSONSerializer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

import static org.ithot.android.business.transmit.http.core.InnerUtils.isNetworkConnected;
import static org.ithot.android.business.transmit.http.core.InnerUtils.md5;
import static org.ithot.android.business.transmit.http.core.InnerUtils.progress;

public class Req {

    /**
     * STATIC
     */

    private static final String TYPE_JSON = "application/json";
    private static final String CHARSET = "UTF-8";

    private static String CACHE_PREFIX_KEY = "";
    private static String BASE_URL = "";
    private static boolean DEBUG = false;

    private static AsyncHttpClient CLIENT;
    private static DualCache<String> CACHE;
    private static IHTTPInterceptor HOOK = new HTTPInterceptor() {
    };
    private static JSONSerializer SERIALIZER;

    static JSONSerializer serializer() {
        return SERIALIZER;
    }

    static AsyncHttpClient client() {
        return CLIENT;
    }

    static DualCache<String> cache() {
        return CACHE;
    }

    private static void lazy(Context context, JSONSerializer serializer) {
        SERIALIZER = serializer;
        CacheSerializer<String> stringSerializer = new StringSerializer();
        CACHE = new Builder<String>("[android-http]", 1)
                .useSerializerInRam(500 * 1024, stringSerializer)
                .useSerializerInDisk(10 * 1024 * 1024, true, stringSerializer, context)
                .build();
    }

    public static IHTTPInterceptor hook() {
        return HOOK;
    }

    public static void debug(boolean debug) {
        DEBUG = debug;
    }

    public static boolean debug() {
        return DEBUG;
    }

    public static void hook(IHTTPInterceptor hook) {
        HOOK = hook;
    }

    public static void base(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static void prefix(String prefix) {
        CACHE_PREFIX_KEY = prefix;
    }

    public static void init(Context context, JSONSerializer json) {
        CLIENT = new AsyncHttpClient(true, 80, 443);
        lazy(context, json);
    }

    public static void init(Context context, int http, JSONSerializer json) {
        CLIENT = new AsyncHttpClient(true, http, 443);
        lazy(context, json);
    }

    public static void init(Context context, int http, int https, JSONSerializer json) {
        CLIENT = new AsyncHttpClient(true, http, https);
        lazy(context, json);
    }

    public static void cancel(Context context) {
        CLIENT.cancelRequests(context, true);
    }

    public static void cancelAll() {
        CLIENT.cancelAllRequests(true);
    }

    /**
     * INSTANCE
     */

    private String mUrl;
    private Context mContext;
    private Map<String, String> mHeaders = new HashMap<>();
    private String mContentType = TYPE_JSON;
    private Policy mPolicy = Policy.CacheAndRemote;
    private Res mRes;
    private HttpEntity mBody;
    private HTTPHandler mHandler;
    private RequestHandle mRequest;
    private boolean mBase = true;
    private boolean mPrefix = true;
    private RequestParams mParams;
    private FileRes mFileRes;
    private Method mMethod;

    public static Req create(Context context) {
        Req req = new Req();
        req.mContext = context;
        if (HOOK != null && HOOK.headers() != null) {
            req.mHeaders.putAll(HOOK.headers());
        }
        req.mHandler = new HTTPHandler(req);
        return req;
    }

    public Req query(Map<String, Object> kvs) {
        if (mUrl == null) throw new RuntimeException("query() must call after url set.");
        if (!mUrl.contains("?")) {
            mUrl += "?";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : kvs.entrySet()) {
            builder.append(String.format("%s=%s&", entry.getKey(), entry.getValue().toString()));
        }
        mUrl += builder.toString();
        return this;
    }

    public Req query(String key, String value) {
        if (mUrl == null) throw new RuntimeException("query() must call after url set.");
        if (!mUrl.contains("?")) {
            mUrl += "?";
        }
        mUrl += String.format("%s=%s&", key, value);
        return this;
    }

    public Req url(String url) {
        if (mBase) {
            if (BASE_URL == null) {
                this.mUrl = url;
            } else {
                this.mUrl = String.format("%s%s", BASE_URL, url);
            }
        } else {
            this.mUrl = url;
        }
        return this;
    }

    public Req base(boolean base) {
        this.mBase = base;
        return this;
    }

    public Req body(Object body) {
        this.mBody = new StringEntity(serializer().stringify(body), CHARSET);
        return this;
    }

    public Req nc() {
        this.mPolicy = Policy.NoCache;
        return this;
    }

    public Req cr() {
        this.mPolicy = Policy.CacheAndRemote;
        return this;
    }

    public Req co() {
        this.mPolicy = Policy.CacheOnly;
        return this;
    }

    public Req ic() {
        this.mPolicy = Policy.IgnoreCache;
        return this;
    }

    public Req cor() {
        this.mPolicy = Policy.CacheOrRemote;
        return this;
    }

    public Req body(Map<String, Object> map) {
        String json = serializer().stringify(map);
        this.mBody = new StringEntity(json, CHARSET);
        return this;
    }

    public Req headers(Map<String, String> headers) {
        mHeaders.putAll(headers);
        return this;
    }

    public Req header(String key, String value) {
        this.mHeaders.put(key, value);
        return this;
    }

    public Req type(String type) {
        this.mContentType = type;
        return this;
    }

    public Req handler(HTTPHandler handler) {
        this.mHandler = handler;
        return this;
    }

    public Req res(Res res) {
        this.mRes = res;
        return this;
    }

    public void cancel() {
        if (this.mRequest != null) {
            mRequest.cancel(true);
        }
    }

    public Req prefix(boolean prefix) {
        this.mPrefix = prefix;
        return this;
    }

    public String key() {
        if (this.mPrefix)
            return md5(CACHE_PREFIX_KEY + mUrl);
        else
            return md5(mUrl);
    }

    /**
     * GETTER
     */

    Context context() {
        return mContext;
    }

    Method method() {
        return mMethod;
    }

    Res res() {
        return mRes;
    }

    Policy policy() {
        return mPolicy;
    }

    public String url() {
        if (mUrl != null && mUrl.endsWith("&")) {
            return mUrl.substring(0, mUrl.length() - 1);
        }
        return mUrl;
    }

    /**
     * REQUEST
     */

    public void get() {
        mMethod = Method.GET;
        Req.hook().pre(this);
        if (mPolicy == Policy.NoCache || mPolicy == Policy.IgnoreCache) {
            if (isNetworkConnected(mContext)) {
                mRequest = CLIENT.get(mContext, url(), headerTransfer(), null, mHandler);
            } else {
                mRes.disconnected(this);
            }
        } else if (mPolicy == Policy.CacheAndRemote) {
            String c = CACHE.get(key());
            if (c != null) {
                mRes.cache(this, c);
                Req.hook().post(this, true);
            }
            if (isNetworkConnected(mContext)) {
                mRequest = CLIENT.get(mContext, mUrl, headerTransfer(), null, mHandler);
            } else {
                mRes.disconnected(this);
            }
        } else if (mPolicy == Policy.CacheOnly) {
            String c = CACHE.get(key());
            if (c != null) {
                mRes.cache(this, c);
                if (!isNetworkConnected(mContext)) {
                    mRes.disconnected(this);
                } else {
                    Req.hook().post(this, true);
                }
            } else {
                if (!isNetworkConnected(mContext)) {
                    mRes.disconnected(this);
                } else {
                    mRequest = CLIENT.get(mContext, mUrl, headerTransfer(), null, mHandler);
                }
            }
        } else if (mPolicy == Policy.CacheOrRemote) {
            if (isNetworkConnected(mContext)) {
                mRequest = CLIENT.get(mContext, mUrl, headerTransfer(), null, mHandler);
            } else {
                mRes.disconnected(this);
                String c = CACHE.get(key());
                mRes.cache(this, c);
                Req.hook().post(this, true);
            }
        }
    }

    public void post() {
        mMethod = Method.POST;
        Req.hook().pre(this);
        if (!isNetworkConnected(mContext)) {
            mRes.disconnected(this);
            return;
        }
        mRequest = CLIENT.post(mContext, mUrl, headerTransfer(), mBody, mContentType, mHandler);
    }

    public void put() {
        mMethod = Method.PUT;
        Req.hook().pre(this);
        if (!isNetworkConnected(mContext)) {
            mRes.disconnected(this);
            return;
        }
        mRequest = CLIENT.put(mContext, mUrl, headerTransfer(), mBody, mContentType, mHandler);
    }

    public void delete() {
        mMethod = Method.DELETE;
        Req.hook().pre(this);
        if (!isNetworkConnected(mContext)) {
            mRes.disconnected(this);
            return;
        }
        mRequest = CLIENT.delete(mContext, mUrl, headerTransfer(), mHandler);
    }

    /**
     * DOWNLOAD
     */

    private Req params(RequestParams params) {
        this.mParams = params;
        return this;
    }

    public Req res(FileRes fileRes) {
        mFileRes = fileRes;
        return this;
    }

    public void download(File file) {
        download(file, false);
    }

    public void download(File file, boolean ugly) {
        if (!isNetworkConnected(mContext)) {
            mFileRes.disconnected(this);
            return;
        }
        FileAsyncHttpResponseHandler handler;
        if (ugly) {
            handler = new FileAsyncHttpResponseHandler(file) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    mFileRes.undone();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    mFileRes.done(file);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    mFileRes.progress(progress(bytesWritten, totalSize));
                }
            };
        } else {
            handler = new RangeFileAsyncHttpResponseHandler(file) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    mFileRes.undone();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    mFileRes.done(file);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    mFileRes.progress(progress(bytesWritten, totalSize));
                }
            };
        }

        CLIENT.get(mContext, mUrl, headerTransfer(), mParams, handler);
    }

    private Header[] headerTransfer() {
        if (mHeaders == null || mHeaders.isEmpty()) return null;
        Header[] results = new Header[mHeaders.size()];
        int i = 0;
        for (String key : mHeaders.keySet()) {
            results[i] = new BasicHeader(key, mHeaders.get(key));
            i++;
        }
        return results;
    }

}

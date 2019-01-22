package org.ithot.android.business.cache.rlcache;

public interface IRlDownloader {
    void start(String remote, IRlGetter strict);

    void cancel();
}

package org.ithot.android.business.cache.rlcache;

import android.util.Log;

public final class RlLog {

    private static final String TAG = "[android-rl-cache]";

    public static void debug(String msg) {
        if (Rl.DEBUG) Log.d(TAG, msg + "");
    }

    public static void error(String msg) {
        if (Rl.DEBUG) Log.e(TAG, msg + "");
    }
}

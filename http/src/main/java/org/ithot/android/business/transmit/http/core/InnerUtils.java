package org.ithot.android.business.transmit.http.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class InnerUtils {

    private static final String TAG = "[android-http]";

    static void print(String message) {
        if (Req.debug()) {
            Log.d(TAG, "debug:" + message);
        }
    }

    static void error(Throwable throwable) {
        throwable.printStackTrace();
    }

    static Map<String, String> arrToMap(Header[] headers) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            map.put(header.getName(), header.getValue());
        }
        return map;
    }

    static String md5(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
                str[(k++)] = hexDigits[(byte0 & 0xF)];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = connectivity.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    static double progress(long bytesWritten, long totalSize) {
        return totalSize > 0L ? (double) bytesWritten * 1.0D / (double) totalSize * 100.0D : -1.0D;
    }

}

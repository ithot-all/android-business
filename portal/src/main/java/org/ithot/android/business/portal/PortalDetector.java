package org.ithot.android.business.portal;

public final class PortalDetector {

    static String URL_204 = "http://g.cn/generate_204";
    static int CONNECT_TIMEOUT = 10 * 1000;
    static int READ_TIMEOUT = 10 * 1000;

    public static void url204(String url) {
        URL_204 = url;
    }

    public static void connectTimeout(int timeout) {
        CONNECT_TIMEOUT = timeout;
    }

    public static void readTimeout(int timeout) {
        READ_TIMEOUT = timeout;
    }

    public static void launch(IPortalResult listener) {
        Checker.check(listener);
    }

}

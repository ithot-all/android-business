package org.ithot.android.business.portal;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class Checker extends AsyncTask<Integer, Integer, Boolean> {

    private IPortalResult listener;

    private Checker(IPortalResult listener) {
        super();
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        return isPortalAvailable();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (listener != null) {
            listener.portal(result);
        }
    }

    static void check(IPortalResult listener) {
        new Checker(listener).execute();
    }

    private static boolean isPortalAvailable() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(PortalDetector.URL_204);
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(PortalDetector.CONNECT_TIMEOUT);
            conn.setReadTimeout(PortalDetector.READ_TIMEOUT);
            conn.setUseCaches(false);
            conn.getInputStream();
            return conn.getResponseCode() != 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
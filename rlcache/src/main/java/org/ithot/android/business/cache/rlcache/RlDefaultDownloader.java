package org.ithot.android.business.cache.rlcache;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RlDefaultDownloader implements IRlDownloader {

    private String remote;
    private String local;
    private static final long MAX_PROGRESS = 100;
    private final static int DOWNLOAD_BUFFER_SIZE = 4096;
    private DownloadAsyncTask task;
    private IRlGetter getter;

    private static String path() {
        String p = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "rlCache" + File.separator + "apk";
        File path = new File(p);
        if (!path.exists()) {
            path.mkdirs();
        }
        return path.getAbsolutePath();
    }

    @Override
    public void start(String remote, IRlGetter getter) {
        if (getter == null || remote == null) {
            throw new RuntimeException("getter and remote not be NULL");
        }
        this.getter = getter;
        this.remote = remote;
        if (task == null) {
            task = new DownloadAsyncTask();
        }
        task.execute(remote);
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel(true);
        }
    }

    private class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == 200) {
                    int totalSize = conn.getContentLength();
                    local = path() + File.separator + new File(params[0]).getName();
                    File file = new File(local);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
                    int bufferLength = 0;
                    long downloadedSize = 0;
                    FileOutputStream fileOut = new FileOutputStream(file);
                    InputStream in = conn.getInputStream();
                    while ((bufferLength = in.read(buffer)) > 0) {
                        fileOut.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        long __s__ = downloadedSize * MAX_PROGRESS;
                        long __t__ = totalSize;
                        int progress = (int) (__s__ / __t__);
                        publishProgress(new Integer[]{progress});
                    }
                    fileOut.close();
                    in.close();
                    if (downloadedSize == totalSize) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Boolean is_success) {
            if (is_success) {
                RlLog.debug(remote + "：download success " + local);
                Rl.put(remote, local, true);
                getter.get(local);
            } else {
                RlLog.debug(remote + "：download failed ");
                getter.get(null);
            }
        }
    }

}

package org.ithot.android.business.regulator;

import android.os.Handler;

public class Throttler<T> implements Runnable {

    public interface IThrottler<T> {

        void perform(T val);

    }

    private Handler handler = new Handler();
    private IThrottler<T> listener;
    private long threshold;
    private long last;
    private T val;
    private long now;

    public Throttler(long threshold) {
        this.threshold = threshold;
    }

    public Throttler() {
        this.threshold = 250;
    }

    public void setListener(IThrottler<T> listener) {
        this.listener = listener;
    }

    public void performAction(T value) {
        val = value;
        now = System.currentTimeMillis();
        if (last != 0 && now < last + threshold) {
            handler.removeCallbacks(this);
            handler.postDelayed(this, threshold);
        } else {
            last = now;
            if (listener != null) {
                listener.perform(val);
            }
        }
    }

    public void performAction() {
        performAction(null);
    }

    public void release() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void run() {
        last = now;
        if (listener != null) {
            listener.perform(val);
        }
    }
}

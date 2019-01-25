package org.ithot.android.business.regulator;

import android.os.Handler;

public class Debouncer<T> implements Runnable {

    public interface IDebouncer<T> {

        void perform(T val);
    }

    private Handler handler = new Handler();
    private boolean enabled = true;
    private IDebouncer<T> listener;
    private long threshold;

    public Debouncer(long threshold) {
        this.threshold = threshold;
    }

    public Debouncer() {
        this.threshold = 500;
    }

    public void setListener(IDebouncer<T> listener) {
        this.listener = listener;
    }

    public void performAction(T val) {
        if (enabled) {
            enabled = false;
            handler.postDelayed(this, threshold);
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
        enabled = true;
    }

}

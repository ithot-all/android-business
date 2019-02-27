package org.ithot.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.ithot.android.business.cache.rlcache.Rl;
import org.ithot.android.business.portal.IPortalResult;
import org.ithot.android.business.portal.PortalDetector;
import org.ithot.android.business.regulator.Debouncer;
import org.ithot.android.business.regulator.Throttler;
import org.ithot.android.business.transmit.http.core.Req;
import org.ithot.android.business.transmit.http.core.Res;
import org.ithot.android.serializer.gson.JSON;
import org.ithot.android.ui.inter.UIListener;
import org.ithot.android.ui.slider.SliderView;

public class MainActivity extends Activity {

    class Dummy {
        public String id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // http
        Req.init(this, new JSON());
        Req.debug(true);
        Req.create(this)
                .url("https://ithot.org/dummy")
                .res(new Res<Dummy>() {
                    @Override
                    public void ok(Dummy dummy) {
                        Log.e("dummy", dummy.id);
                    }
                }).get();
        // rlcache
        Rl.init(this);
        // start upload
        /**
         * Debouncer
         */
        final Debouncer debouncer = new Debouncer(500);
        debouncer.setListener(new Debouncer.IDebouncer() {
            @Override
            public void perform(Object val) {
                Log.e("perform", "debouncer");
            }
        });

        findViewById(R.id.btn_debouncer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debouncer.performAction();
            }
        });
        /**
         * Throttle
         */
        final Throttler<Float> throttler = new Throttler<>();
        throttler.setListener(new Throttler.IThrottler<Float>() {
            @Override
            public void perform(Float val) {
                Log.e("Throttle", val.intValue() + "");
            }
        });
        SliderView sliderView = findViewById(R.id.slider);
        sliderView.setListener(new UIListener() {
            @Override
            public void move(float value) {
                throttler.performAction(value);
            }
        });

        PortalDetector.launch(new IPortalResult() {
            @Override
            public void portal(boolean need) {
                if (need){
                    // Open a browser and visit any website
                }
            }
        });
    }
}

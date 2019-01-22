package org.ithot.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.ithot.android.business.cache.rlcache.Rl;
import org.ithot.android.business.transmit.http.core.Req;
import org.ithot.android.business.transmit.http.core.Res;
import org.ithot.android.serializer.gson.JSON;

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
    }
}

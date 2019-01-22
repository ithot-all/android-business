package org.ithot.android.business.cache.rlcache;

import android.content.Intent;

import java.util.List;

public class RlUploadScheduler {

    static final String LOCAL = "local";

    synchronized static void dispatch() {
        List<IRl> locals = Rl.gets(RlType.UN_UPLOAD);
        RlLog.debug("un upload count: " + locals.size());
        if (locals.size() == 0) return;
        for (int i = 0; i < locals.size(); i++) {
            IRl rl = locals.get(i);
            Intent intent = new Intent(Rl.ctx(), RlUploadService.class);
            intent.putExtra(LOCAL, new RlBean(rl.getRlRemote(), rl.getRlLocal(), rl.getRlIsUpload()));
            Rl.ctx().startService(intent);
        }
    }
}

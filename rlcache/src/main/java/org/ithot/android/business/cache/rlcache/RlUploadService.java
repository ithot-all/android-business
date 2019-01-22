package org.ithot.android.business.cache.rlcache;

import android.app.IntentService;
import android.content.Intent;

import static org.ithot.android.business.cache.rlcache.RlUploadScheduler.LOCAL;

public class RlUploadService extends IntentService {

    public RlUploadService() {
        super("RlUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RlBean bean = intent.getParcelableExtra(LOCAL);
        try {
            IRlUploader uploader = Rl.uploader().newInstance();
            uploader.upload(bean, new RlUploaderStatus(bean) {
                @Override
                void succeed(RlBean bean) {
                    RlLog.debug(bean.getRlRemote() + " succeed");
                    Rl.modify(bean.getRlRemote(), bean.getRlLocal(), true);
                }

                @Override
                void failed(RlBean bean) {
                    RlLog.debug(bean.getRlRemote() + " failed");
                    Rl.modify(bean.getRlRemote(), bean.getRlLocal(), false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RlLog.debug("service destroy");
    }
}

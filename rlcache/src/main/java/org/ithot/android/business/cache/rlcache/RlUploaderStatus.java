package org.ithot.android.business.cache.rlcache;

public abstract class RlUploaderStatus implements IRlUploaderStater {

    private RlBean bean;

    RlUploaderStatus(RlBean bean) {
        this.bean = bean;
    }

    abstract void succeed(RlBean bean);

    abstract void failed(RlBean bean);

    @Override
    public void done() {
        succeed(bean);
    }

    @Override
    public void undone() {
        failed(bean);
    }
}

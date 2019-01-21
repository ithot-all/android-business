package org.ithot.android.business.transmit.http.core;

import java.io.File;

public interface IHTTPFileRes {

    void done(File file);

    void undone();

    void progress(double rate);

    void disconnected(Req req);

}

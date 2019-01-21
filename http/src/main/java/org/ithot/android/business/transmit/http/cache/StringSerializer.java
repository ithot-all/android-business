package org.ithot.android.business.transmit.http.cache;

/**
 */

public class StringSerializer implements CacheSerializer<String> {
    @Override
    public String fromString(String data) {
        return data;
    }

    @Override
    public String toString(String object) {
        return object;
    }
}

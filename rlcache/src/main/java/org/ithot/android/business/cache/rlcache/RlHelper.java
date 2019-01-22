package org.ithot.android.business.cache.rlcache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RlHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "rl.db";
    private final static int VERSION = 1;

    public static final String TABLE_NAME = "TB_RL";
    public static final String LOCAL_FIELD = "local";
    public static final String REMOTE_FIELD = "remote";
    public static final String IS_UPLOAD_FIELD = "is_upload";
    private static final String T = "CREATE TABLE  %s ( %s text NOT NULL, %s text NOT NULL, %s integer NOT NULL)";
    private static final String TABLE_DEFINITION = String.format(T, TABLE_NAME, LOCAL_FIELD, REMOTE_FIELD, IS_UPLOAD_FIELD);

    private RlHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_DEFINITION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Inner {
        private static final RlHelper INSTANCE = new RlHelper(Rl.ctx());
    }

    public static RlHelper instance() {
        return Inner.INSTANCE;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

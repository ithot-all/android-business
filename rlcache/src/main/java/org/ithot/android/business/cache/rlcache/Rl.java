package org.ithot.android.business.cache.rlcache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Rl {

    private Rl() {

    }

    private static Map<String, String> MEMORY = new ConcurrentHashMap<>();
    public static boolean DEBUG = false;

    private static Context CONTEXT;
    private static Class<? extends IRlDownloader> DOWNLOADER = RlDefaultDownloader.class;
    private static Class<? extends IRlUploader> UPLOADER;

    public static void downloader(Class<? extends IRlDownloader> downloader) {
        DOWNLOADER = downloader;
    }

    public static void uploader(Class<? extends IRlUploader> uploader) {
        UPLOADER = uploader;
    }

    public static Class<? extends IRlUploader> uploader() {
        return UPLOADER;
    }

    public static Context ctx() {
        return CONTEXT;
    }

    public static void debug(boolean d) {
        DEBUG = d;
    }

    public static Map<String, String> memory() {
        return MEMORY;
    }

    public static String get(String remote) {
        String local = MEMORY.get(remote);
        if (local == null) {
            return remote;
        } else {
            if (new File(local).exists()) {
                return local;
            } else {
                remove(remote);
                return remote;
            }
        }
    }

    public static void get(String remote, IRlGetter getter) {
        String local = MEMORY.get(remote);
        if (local != null && new File(local).exists()) {
            getter.get(local);
        } else {
            remove(remote);
            IRlDownloader downloader;
            try {
                downloader = DOWNLOADER.newInstance();
                downloader.start(remote, getter);
            } catch (InstantiationException e) {
                getter.get(null);
            } catch (IllegalAccessException e) {
                getter.get(null);
            }
        }
    }

    public static boolean exist(String remote) {
        String local = MEMORY.get(remote);
        if (local == null) return false;
        if (new File(local).exists()) {
            return true;
        } else {
            remove(remote);
            return false;
        }
    }

    public static List<IRl> gets(RlType type) {
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        String sql = "SELECT remote, local, is_upload FROM " + RlHelper.TABLE_NAME;
        switch (type) {
            case UN_UPLOAD:
                sql += " WHERE is_upload = 0";
                break;
            case UPLOADED:
                sql += " WHERE is_upload != 0";
                break;
        }
        Cursor cursor = db.rawQuery(sql, null);
        List<IRl> rls = new ArrayList<>();
        while (cursor.moveToNext()) {
            RlBean bean = new RlBean();
            bean.remote = cursor.getString(cursor.getColumnIndex(RlHelper.REMOTE_FIELD));
            bean.local = cursor.getString(cursor.getColumnIndex(RlHelper.LOCAL_FIELD));
            bean.is_upload = cursor.getInt(cursor.getColumnIndex(RlHelper.IS_UPLOAD_FIELD)) != 0;
            rls.add(bean);
        }
        cursor.close();
        db.close();
        return rls;
    }

    public static void puts(List<IRl> rls) {
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues kvs = new ContentValues();
            for (IRl rl : rls) {
                if (!MEMORY.containsKey(rl.getRlRemote())) {
                    kvs.put(RlHelper.REMOTE_FIELD, rl.getRlRemote());
                    kvs.put(RlHelper.LOCAL_FIELD, rl.getRlLocal());
                    kvs.put(RlHelper.IS_UPLOAD_FIELD, rl.getRlIsUpload() ? 1 : 0);
                    db.insert(RlHelper.TABLE_NAME, null, kvs);
                    kvs.clear();
                }
            }
            db.setTransactionSuccessful();
            for (IRl rl : rls) {
                MEMORY.put(rl.getRlRemote(), rl.getRlLocal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public static void modify(String remote, String local, boolean is_upload) {
        if (!MEMORY.containsKey(remote)) return;
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        ContentValues kvs = new ContentValues();
        kvs.put(RlHelper.REMOTE_FIELD, remote);
        kvs.put(RlHelper.LOCAL_FIELD, local);
        kvs.put(RlHelper.IS_UPLOAD_FIELD, is_upload ? 1 : 0);
        db.update(RlHelper.TABLE_NAME, kvs, "remote=?", new String[]{remote});
        db.close();
        MEMORY.put(remote, local);
    }

    public static void modify(IRl rl) {
        if (!MEMORY.containsKey(rl.getRlRemote())) return;
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        ContentValues kvs = new ContentValues();
        kvs.put(RlHelper.REMOTE_FIELD, rl.getRlRemote());
        kvs.put(RlHelper.LOCAL_FIELD, rl.getRlLocal());
        kvs.put(RlHelper.IS_UPLOAD_FIELD, rl.getRlIsUpload() ? 1 : 0);
        db.update(RlHelper.TABLE_NAME, kvs, "remote=?", new String[]{rl.getRlRemote()});
        db.close();
        MEMORY.put(rl.getRlRemote(), rl.getRlLocal());
    }

    public static void remove(String remote) {
        if (!MEMORY.containsKey(remote)) return;
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        db.delete(RlHelper.TABLE_NAME, "remote=?", new String[]{remote});
        db.close();
        MEMORY.remove(remote);
    }

    public static void clear() {
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        db.delete(RlHelper.TABLE_NAME, null, null);
        db.close();
        MEMORY.clear();
    }

    public static void put(String remote, String local, boolean is_upload) {
        if (MEMORY.containsKey(remote)) return;
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        ContentValues kvs = new ContentValues();
        kvs.put(RlHelper.REMOTE_FIELD, remote);
        kvs.put(RlHelper.LOCAL_FIELD, local);
        kvs.put(RlHelper.IS_UPLOAD_FIELD, is_upload ? 1 : 0);
        db.insert(RlHelper.TABLE_NAME, null, kvs);
        kvs.clear();
        db.close();
        MEMORY.put(remote, local);
    }

    public static void put(IRl rl) {
        if (MEMORY.containsKey(rl.getRlRemote())) return;
        SQLiteDatabase db = RlHelper.instance().getWritableDatabase();
        ContentValues kvs = new ContentValues();
        kvs.put(RlHelper.REMOTE_FIELD, rl.getRlRemote());
        kvs.put(RlHelper.LOCAL_FIELD, rl.getRlLocal());
        kvs.put(RlHelper.IS_UPLOAD_FIELD, rl.getRlIsUpload());
        db.insert(RlHelper.TABLE_NAME, null, kvs);
        kvs.clear();
        db.close();
        MEMORY.put(rl.getRlRemote(), rl.getRlLocal());
    }

    public static void init(Context ctx) {
        CONTEXT = ctx;
        List<IRl> rls = gets(RlType.ALL);
        MEMORY.clear();
        if (rls.size() == 0) return;
        for (IRl rl : rls) {
            MEMORY.put(rl.getRlRemote(), rl.getRlLocal());
        }
    }

    public static void syncRl() {
        if (UPLOADER == null) {
            RlLog.error("uploader can not be null.");
            return;
        }
        if (Rl.ctx() == null) {
            RlLog.error("CONTEXT can not be null.");
            return;
        }

        RlUploadScheduler.dispatch();
    }
}

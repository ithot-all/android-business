# android-business

[![Build Status](https://img.shields.io/travis/ithot-all/android-business/master.svg?style=flat-square)](https://travis-ci.org/ithot-all/android-business)

:fire: A series of Android business type libraries 

## AndroidHttp

### install
```gradle
implementation 'org.ithot.android.serializer:gson:1.0.1'
implementation 'org.ithot.android.business.transmit.http:httpc:0.0.1'
```

### usage
```java
/* once */
Req.init(this, new JSON());
/* example */
class Dummy{
    public String id;
}
Req.create(this)
    .url("https://ithot.org/dummy")
    .res(new Res<Dummy>() {
        @Override
        public void ok(Dummy dummy) {
            Log.e("dummy", dummy.id);
        }
    }).get();
```

## AndroidRlCache

### required
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
```

### install
```gradle
implementation 'org.ithot.android.business.cache.rlcache:rlcache:0.0.1'
```

### usage
```java
/* once */
Rl.init(this);
/* example */
Rl.put("remoteKey", "localKey", false);
Rl.get("remoteKey");
Rl.get("remoteKey", new IRlGetter() {
    @Override
    public void get(String path) {

    }
});
```

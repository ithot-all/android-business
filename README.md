# Android-Business

[![Build Status](https://img.shields.io/travis/ithot-all/android-business/master.svg?style=flat-square)](https://travis-ci.org/ithot-all/android-business)

:fire: a series of android business type libraries 

## AndroidHTTP

### required
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### install
```gradle
implementation 'org.ithot.android.serializer:gson:1.0.1'
implementation 'org.ithot.android.business:http-s:0.0.1'
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
implementation 'org.ithot.android.business:rl-cache:0.0.1'
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
## AndroidRegulator

### install
```gradle
implementation 'org.ithot.android.business:regulator-core:0.0.1'
```

### usage
```java
/* example */
Debouncer debouncer = new Debouncer(500);

debouncer.setListener(new Debouncer.IDebouncer() {
    @Override
    public void perform(Object val) {
        Log.e("perform", "Debouncer");
    }
});

findViewById(R.id.btn_some).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        debouncer.performAction();
    }
});
Throttler<Integer> throttler = new Throttler<>();

throttler.setListener(new Throttler.IThrottler<Integer>() {
    @Override
    public void perform(Integer val) {
        Log.e("Throttle", val.intValue() + "");
    }
});
SeekBar seekView = findViewById(R.id.seek);
seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        throttler.performAction(progress);
    }
});
```

## AndroidPortal

### required
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### install
```gradle
implementation 'org.ithot.android.business:portal-detect:0.0.1'
```

### usage
```java
PortalDetector.launch(new IPortalResult() {
    @Override
    public void portal(boolean need) {
        if (need){
            // Open a browser and visit any website
        }
    }
});
```

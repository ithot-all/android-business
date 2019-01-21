# android-business
:fire: A series of Android business type libraries

## android-http

### install
```
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

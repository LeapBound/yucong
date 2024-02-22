# yc-action-server-example

### Start Up

1. Maven Dependencies
```xml
<dependency>
    <groupId>yzg-gy</groupId>
    <artifactId>yc-action-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
or you can load jar as
```xml
<dependency>
    <groupId>yzg-gy</groupId>
    <artifactId>yc-action-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/yc-action-server-0.0.1-SNAPSHOT.jar</systemPath>
</dependency>
```

2. Configure the `yc-action-server` in your `application.yml`

   Here, I use `Apollo` configuration center as an example in `application-apollo.yml`.

3. Then you can do with [yc-action-server/README.md](https://github.com/LeapBound/yucong/blob/master/yc-action-server/README.md)
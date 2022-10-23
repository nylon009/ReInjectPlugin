# ReInjectPlugin
 ReInjectPlugin is an android build plugin. It uses ASM to modify classes without write duplicate code in many places.
 Inspired by xposed, the user just define original class and method, and write inject class and method, then the code will be injected to apk.
 
# Sample
## use plugin
ReInjectPlugin is already published to mavenCenter, please add mavenCenter in your mvn config
update root project's build.gradle
```groovy
buildscript {
    dependencies {
        classpath 'io.github.nylon009:reinject-plugin:0.0.1'
    }
}
```
update app/build.gradle
```groovy
plugins {
    id 'io.github.nylon009.reinject-plugin'
}
```
## Config file
add ReInject.json file in the app project path:
Please note the method for static and non static method are different in InjectUtil.java, 
for non static method, the inject function should use the origin class as the first parameter.
```
{
  "injects": [
    {
      "className": "com.nylon.app.Utils",
      "methodName": "foobar",
      "methodSignature": "(Ljava/lang/String;)V",
      "injectClassName": "com.nylon.app.InjectUtils",
      "injectEnterMethodName": "foobarEnter",
      "injectExitMethodName": "foobarExit"
    },
    {
      "className": "com.nylon.app.Utils",
      "methodName": "hello",
      "methodSignature": "(Ljava/lang/String;)V",
      "injectClassName": "com.nylon.app.InjectUtils",
      "injectEnterMethodName": "helloEnter",
      "injectExitMethodName": "helloExit"
    }
  ]
}
``` 
## Inject for static method
original code:
com.nylon.app.Utils
```
public static void foobar(String test) {
    Log.d(TAG, "foobar");
}
```

after injected:
```
public static void foobar(String test) {
    InjectUtils.foobarEnter(test); // this is injected by asm
    Log.d(TAG, "foobar");
    InjectUtils.foobarExit(test);  // this is injected by asm
}
```
## Inject for none-static method
origin
```
public void hello(String msg) {
    Log.d(TAG, "hello " + msg);
}
```
after injected
```
public void hello(String msg) {
    InjectUtils.helloEnter(this, msg);
    Log.d(TAG, "hello " + msg);
    InjectUtils.helloExit(this, msg);
}
```

# How to build your own
Update the Plugin module, change to snapshot version, and publish plugin to local repo directory
```
 ./gradlew :Plugin:assemble :Plugin:publishAllPublicationsToMyRepoRepository
```
this project already add local repo directory as maven center, so no need to change it.
update the app/build.gradle to use snapshot version.
 
# Reference
* [Android ASM自动埋点方案实践](https://www.jianshu.com/p/9039a3e46dbc)
* [ASM homepage](https://asm.ow2.io/)
* [Sentry: Bytecode transformations: The Android Gradle Plugin](https://blog.sentry.io/2021/12/14/bytecode-transformations-the-android-gradle-plugin/)
* [AndroidPluginStudy](https://github.com/stven0king/AndroidPluginStudy)
* [神策 Android 全埋点插件介绍](https://opensource.sensorsdata.cn/opensource/%E7%A5%9E%E7%AD%96-android-%E5%85%A8%E5%9F%8B%E7%82%B9%E6%8F%92%E4%BB%B6%E4%BB%8B%E7%BB%8D/)

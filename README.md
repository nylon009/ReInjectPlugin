# ReInjectPlugin
 ReInjectPlugin is an android build plugin. It uses ASM to modify classes without write duplicate code in many places.
 Inspired by xposed, the user just define original class and method, and write inject class and method, then the code will be injected to apk.
 
# Sample
## config file
add ReInject.json file in the project path as below:
Please note that InjectUtils.java, the method for static and nonstatic method are different:　
For instance method, the inject function should be the origin class as the first parameter.
```
{
  "injects": [
    {
      "className": "com.nylon.app.Utils",
      "methodName": "foobar",
      "methodSignature": "(Ljava/lang/String;)V",
      "injectClassName": "com.nylon.app.InjectUtils",
      "injectBeforeMethodName": "foobarBefore",
      "injectAfterMethodName": "foobarAfter"
    },
    {
      "className": "com.nylon.app.Utils",
      "methodName": "hello",
      "methodSignature": "(Ljava/lang/String;)V",
      "injectClassName": "com.nylon.app.InjectUtils",
      "injectBeforeMethodName": "helloBefore"
    }
  ]
}
``` 
## Inject for static method
original code:
com.nylon.app.Utils
```
    public static void foobar(String test){
        Log.d(TAG, "foobar");
    }
```

after injected:
```
    public static void foobar(String test) {
        InjectUtils.foobarBefore(test); // this is injected by asm
        Log.d(TAG, "foobar");
        InjectUtils.foobarAfter(test);  // this is injected by asm
    }
```
## Inject for none-static method
origin
```
    public void hello(String msg) {
        InjectUtils.helloBefore(this, msg);
        InjectUtils.helloBefore(this, msg);
        Log.d(TAG, "hello " + msg);
    }
```
after injected
```
    public void hello(String msg) {
        InjectUtils.helloBefore(this, msg);
        Log.d(TAG, "hello " + msg);
    }
```

# How to build project
## publish plugin to local repo
Comment below lines in build.gradle
```
#buildscript {
#    dependencies {
#        classpath 'com.nylon.reinject:reinject-plugin:0.0.1'
#    }
#}
```
Comment line in app/build.gradle
```
   # id 'com.nylon.reinject'
```
build and publish plugin
```
./gradlew :Plugin:assemble :Plugin:publish
```
# use plugin
revert the comments add in previous steps, and sync gradle again.
build app
```
./gradlew :app:assemble
```
 
# Reference
* [Sentry: Bytecode transformations: The Android Gradle Plugin](https://blog.sentry.io/2021/12/14/bytecode-transformations-the-android-gradle-plugin/)

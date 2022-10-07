# ReInjectPlugin
 ReInjectPlugin is an android build plugin. It uses ASM to modify classes without write duplicate code in many places.
 Inspired by xposed, the user just define original class and method, and write inject class and method, then the code will be injected to apk.
 
# Config file
add ReInject.json file in the project path as below:
```
{
  "injects":[
    {
        "className":"com.nylon.app.Utils",
        "methodName": "foobar",
        "methodSignature": "(Ljava/lang/String;)V",
        "injectClassName": "com/nylon/app/InjectUtils",
        "injectBeforeMethodName": "foobarBefore",
        "injectAfterMethodName": "foobarAfter"
    }
  ]
}

``` 

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
 
# Reference
* [Sentry: Bytecode transformations: The Android Gradle Plugin](https://blog.sentry.io/2021/12/14/bytecode-transformations-the-android-gradle-plugin/)

# ReInjectPlugin
ReInjectPlugin是一个Android gradle插件, 使用ASM技术修改已编译好的代码，达到无侵入修改的目的。
类似于AOP编程或Xposed，我们经常发现下面的这些场景，需要添加一些代码，但又不想在原来的项目业务逻辑：
* 性能打点：对http, 文件访问，数据库访问，rpc调用
* 业务打点：如界面打点工具，记录进出每一个activity的时间
* 实例监控：记录某一个特定对象的分配和释放情况，用于调测定位内存泄漏的场景

这些要修改的文件，一方面有可能是三方库的代码，如sqlite,okhttp等，必须要编译自己的版本或在本项目内做一个封装类，才能拦截到；
也有可能，这些类的修改涉及非常多，修改非常发散。
ReInjectPlugin正是为了解决此类问题而来，只需要写好配置文件，并编写拦截后的代码，就可以实现对apk内任何一个类的注入和修改。



# 使用说明
## 添加插件配置
插件已发布到mavenCenter，需要添加mavenCenter作为开源引用仓。
修改根项目build.gradle文件，添加脚本引用说明
```groovy
buildscript {
    dependencies {
        classpath 'io.github.nylon009:reinject-plugin:0.0.1'
    }
}
```
在app/build.gradle使用此插件
```groovy
plugins {
    id 'io.github.nylon009.reinject-plugin'
}
```
## 配置文件
在app工程下增加ReInject.json配置文件
注意在实现类InjectUtils中，静态方法和动态方法的拦截实现类是不一样的。
动态方法拦截时，第一个参数永远是原来调用的类实例，这样可以方便获取实例内部的信息，甚至修改成员值。
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
## 拦截静态方法
原始代码com.nylon.app.Utils
```
public static void foobar(String test){
    Log.d(TAG, "foobar");
}
```
编译完成后，通过jadx可以查看修改后的代码如下：
```
public static void foobar(String test) {
    InjectUtils.foobarEnter(test); // 这一行是注入的代码
    Log.d(TAG, "foobar");
    InjectUtils.foobarExit(test);  // 这一行是注入的代码
}
```
## 注入非静态函数
原始类com.nylon.app.Utils
```
public void hello(String msg) {
    Log.d(TAG, "hello " + msg);
}
```
注入后的代码
```
public void hello(String msg) {
    InjectUtils.helloEnter(this, msg); // 这一行是注入的代码，第一个参数是新增参数
    Log.d(TAG, "hello " + msg);
    InjectUtils.helloExit(this, msg); // 这一行是注入的代码，第一个参数是新增参数
}
```

# 怎么样修改插件并调试
修改Plugin模块中，发布插件的版本号,并通过以下命令发布到本地repo目录
```
 ./gradlew :Plugin:assemble :Plugin:publishAllPublicationsToMyRepoRepository
```
在项目里已添加了repo目录为maven仓，因此无须再手动添加。
更新app/build.gradle里插件使用的版本号，就可以用上新版本了。

# 其它
致谢：本项目在实现时，参考了[AndroidPluginStudy](https://github.com/stven0king/AndroidPluginStudy)的部分实现代码，增加了插件配置功能。

# 参考资料
* [Android ASM自动埋点方案实践](https://www.jianshu.com/p/9039a3e46dbc)
* [ASM homepage](https://asm.ow2.io/)
* [Sentry: Bytecode transformations: The Android Gradle Plugin](https://blog.sentry.io/2021/12/14/bytecode-transformations-the-android-gradle-plugin/)
* [AndroidPluginStudy](https://github.com/stven0king/AndroidPluginStudy)
* [神策 Android 全埋点插件介绍](https://opensource.sensorsdata.cn/opensource/%E7%A5%9E%E7%AD%96-android-%E5%85%A8%E5%9F%8B%E7%82%B9%E6%8F%92%E4%BB%B6%E4%BB%8B%E7%BB%8D/)

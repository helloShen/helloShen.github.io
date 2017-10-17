---
layout: post
title: "How Tomcat Works - Chapter 8 - Loader"
date: 2017-10-14 21:27:33
author: "Wei SHEN"
categories: ["java","how tomcat works"]
tags: ["class loader"]
description: >
---

### 前言
本章的主题是Tomcat的Web应用加载器。最基本的功能是一个类加载器，从HTTP请求中解析出目标Servlet程序的全具名以后，负责利用反射动态加载Servlet类。前几章都是简单使用了`java.net.URLClassLoader`。但是工业级别的服务器在安全性和可用性有更高的要求，
1. 什么类应该加载？什么类不应该加载？
2. 使用一个公共加载器？还是让不同的应用专属的加载器访问不同的仓库？
3. 系统资源路径是否应该对Tomcat服务器是可见的？
4. 不重新启动服务器的情况下及时更新Servlet资源

所以Tomcat使用了自定义的类加载器。本章主要集中讨论的是和一个`Servlet Context`关联的加载器，但Tomcat全局的加载器布局更加复杂。除了局部的`Web App Loader`，还需要关注全局`Common Loader`的行为，以及多个层级的加载器之间是怎样分工协作的。

了解了多层级的加载器布局之后，就可以明确各个接口之间的职责边界，比如，
* `WebappLoader`定义了广义上的“应用加载器”的角色，不仅仅只是一个“类加载器”
* `WebappClassLoader`接口专注于传统的“类加载器”的工作
* `ClassLoader`定义了普适的类加载器的行为
* `Reloader`接口为了支持类的自动重载功能

### 回顾Java类载入器的“代理模型”
JVM使用3中类载入器来载入需要的类：
1. 引导类载入器（Bootstrap Class Loader）: C++实现的本地代码。加载JVM核心库。`<JAVA_HOME>/jre/lib`。
2. 扩展类载入器（Extension Class Loader）: 负责加载标准扩展目录(`<JAVA_HOME>/jre/lib/ext`)中的类。
3. 系统类载入器（System Class Loader）: 默认的载入器，它会搜索环境变量`CLASSPATH`中指明的路径和JAR文件。

![loader-three-types](/images/how-tomcat-works-chapter-eight-loader/loader-three-types.jpg)

在具体用哪个载入器载入类的策略上，JVM使用“委托模型（Delegation Pattern）”。每当需要载入一个类的时候，首先调用最低级的“系统类载入器”，然后将任务交给其父载入器，即“扩展类载入器”，然后再进一步交给更上层的父类载入器“引导类载入器”。如果“引导类载入器”在`<JAVA_HOME>/jre/lib`路径下找不到需要载入的类，那么“扩展类载入器”会尝试在`<JAVA_HOME>/jre/lib/ext`路径下查找该类，如果还是找不到，才轮到“系统类载入器”在环境变量`CLASSPATH`指定的仓库寻找资源，如果还找不到则会抛出`java.lang.ClassNotFoundException`异常。

代理模型主要为了解决载入过程中的安全性问题。优先使用“引导类载入器”，然后是“扩展类载入器”，为了在出现和JAVA核心库同名资源的时候，加载的总是正确的系统组件。比如说就算我在自己的CLASSPATH下写了一个恶意的`java.lang.Object`类，也不会被载入。JVM载入的永远是系统核心库中的正确的`java.lang.Object`类。

在“代理模型”的基础上，用户可以使用自定义的加载器。自定义加载器一般都实现了`ClassLoader`抽象类，它为类加载器定义了一组基本行为。

### Tomcat类加载器的结构
除了系统自带的三层加载器以外，Tomcat还有一个服务于全局的`Common Class Loader`，以及和每一个`Context`容器关联的“应用加载器”。本章主要介绍的是最后的`Webapp Loader`，并没有介绍`Common Loader`。
```
     Bootstrap      // <JAVA_HOME>/jre/lib
        |
     Extension      // <JAVA_HOME>/jre/lib/ext
        |
      System        // CLASSPATH(Tomcat 启动脚本中修改成一下3个包)
        |           //    1. "$CATALINA_HOME/bin/bootstrap.jar"
        |           //    2. "$CATALINA_BASE/bin/tomcat-juli.jar"
        |           //    3. "$CATALINA_HOME/bin/commons-daemon.jar"
        |
      Common        // "$CATALINA_BASE/lib"中的class文件和jar包
        |           // "$CATALINA_HOME/lib"中的class文件和jar包
        |
     /     \
Webapp1   Webapp2   // "WEB-INF/classes"
                    // "WEB-INF/lib"
```
如上图所示，Tomcat 在初始化时会创建如下这些类加载器：

1. Bootstrap 这种类加载器包含 JVM 所提供的基本的运行时类 (`$JAVA_HOME/jre/lib`)。

2. Extension 来自系统扩展目录（`$JAVA_HOME/jre/lib/ext`）里 JAR 文件中的类。注意：在有些 JVM 的实现中，Bootstrap 和 Extension 加载器的作用不仅仅是类加载器，或者它可能根本不可见（作为类加载器）。

3. System 这种类加载器通常是根据 `CLASSPATH` 环境变量内容进行初始化的。**所有的这些类对于 Tomcat 内部类以及 Web 应用来说都是可见的**。不过，标准的 Tomcat 启动脚本（`$CATALINA_HOME/bin/catalina.sh` 或 `$CATALINA_HOME\bin\catalina.bat`）完全忽略了 CLASSPATH 环境变量自身的内容，相反从下列仓库来构建系统类加载器：

    * `$CATALINA_HOME/bin/bootstrap.jar` 包含用来初始化 Tomcat 服务器的 main() 方法，以及它所依赖的类加载器实现类。
    * `$CATALINA_BASE/bin/tomcat-juli.jar` 或 `$CATALINA_HOME/bin/tomcat-juli.jar` 日志实现类。其中包括了对 java.util.logging API 的功能增强类（Tomcat JULI），以及对 Tomcat 内部使用的 Apache Commons 日志库的包重命名副本。详情参看 Tomcat 日志文档。如果 `$CATALINA_BASE/bin` 中存在 `tomcat-juli.jar`，就不会使用 `$CATALINA_HOME/bin` 中的那一个。它有助于日志的特定配置。   
    * `$CATALINA_HOME/bin/commons-daemon.jar` Apache Commons Daemon 项目的类。该 JAR 文件并不存在于由 `catalina.bat` 或 `catalina.sh` 脚本所创建的 CLASSPATH 中，而是引用自 `bootstrap.jar` 的清单文件。

4. Common 这种类加载器包含更多的额外类，它们对于Tomcat 内部类以及所有 Web 应用都是可见的。通常，应用类不会放在这里。该类加载器所搜索的位置定义在 `$CATALINA_BASE/conf/catalina.properties` 的 `common.loader` 属性中。默认的设置会搜索下列位置（按照列表中的上下顺序）。
    * `$CATALINA_BASE/lib` 中的解包的类和资源。
    * `$CATALINA_BASE/lib` 中的 JAR 文件。
    * `$CATALINA_HOME/lib` 中的解包类和资源。
    * `$CATALINA_HOME/lib` 中的 JAR 文件。

    默认，它包含以下这些内容：

    * `annotations-api.jar` JavaEE 注释类。
    * `catalina.jar` Tomcat 的 Catalina servlet 容器部分的实现。
    * `catalina-ant.jar` Tomcat Catalina Ant 任务。
    * `catalina-ha.jar` 高可用性包。
    * `catalina-storeconfig.jar`
    * `catalina-tribes.jar` 组通信包
    * `ecj-*.jar` Eclipse JDT Java 编译器
    * `el-api.jar` EL 3.0 API
    * `jasper.jar` Tomcat Jasper JSP 编译器与运行时
    * `jasper-el.jar` Tomcat Jasper EL 实现
    * `jsp-api.jar` JSP 2.3 API
    * `servlet-api.jar` Servlet 3.1 API
    * `tomcat-api.jar` Tomcat 定义的一些接口
    * `tomcat-coyote.jar` Tomcat 连接器与工具类。
    * `tomcat-dbcp.jar` 数据库连接池实现，基于 Apache Commons Pool 的包重命名副本和 * Apache Commons DBCP。
    * `tomcat-i18n-**.jar` 包含其他语言资源束的可选 JAR。因为默认的资源束也可以包含在每个单独的 JAR 文件中，所以如果不需要国际化信息，可以将其安全地移除。 `tomcat-jdbc.jar` 一个数据库连接池替代实现，又被称作 Tomcat JDBC 池。详情参看 JDBC 连接池文档。
    * `tomcat-util.jar` Apache Tomcat 多种组件所使用的常用类。
    * `tomcat-websocket.jar` WebSocket 1.1 实现
    * `websocket-api.jar` WebSocket 1.1 API

5. WebappX 为每个部署在单个 Tomcat 实例中的 Web 应用创建的类加载器。你的 Web 应用的 `·/WEB-INF/classes` 目录中所有的解包类及资源，以及 `/WEB-INF/lib` 目录下 JAR 文件中的所有类及资源，对于该应用而言都是可见的，但对于其他应用来说则不可见。

以上内容摘自《Tomcat 8 使用手册》。梳理一下各层资源可见性，
* Bootstrap和Extention加载器的可见性取决于系统实现。但由于这部分只掌管Java核心库和扩展库，都是授信的资源，就算是可见的也不会危害到其他系统资源。
* System加载器和CLASSPATH对所有Tomcat内部类和应用类都是可见的。这是危害的主要来源。所以Tomcat启动脚本从一开始就修改了系统的CLASSPATH环境变量，为的就是避免让Servlet应用访问到系统资源。这是Tomcat重要的防御措施，修改以后，就算委托模式往上调用System加载器，可以看到的只有它限定的3个包。

根据Stackoverflow的一个相关回答，Tomcat 6是这样修改CLASSPATH环境变量的，
```
In Tomcat 6, the CLASSPATH in your environment is ignored. In setclasspath.bat you'll see

    set CLASSPATH=%JAVA_HOME%\lib\tools.jar

then in catalina.bat, it's used like so

    %_EXECJAVA% %JAVA_OPTS% %CATALINA_OPTS% %DEBUG_OPTS%
    -Djava.endorsed.dirs="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%"
    -Dcatalina.base="%CATALINA_BASE%" -Dcatalina.home="%CATALINA_HOME%"
    -Djava.io.tmpdir="%CATALINA_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTIONw%
```

* Common类加载器负责的`$CATALINA_BASE/lib`和`$CATALINA_HOME/lib`对所有Tomcat内部类和应用类都是可见的。但这两个目录下的资源本来就是为了被共享给整个Tomcat服务器才存放在这里的，所以仅仅公开这两个资源路径是合理而且安全的。
* 每个应用的专属资源被限定在`WEB-INF/lib`和`WEB-INF/classes`路径下，只对应用专属加载器可见。

下面这段是《Tomcat使用手册》对`WEB-INF`目录的相关描述，
```
应用程序层次结构中存在一个名为“WEB-INF”的特殊目录。这个目录包含了与应用程序相关的所有东西， 这些东西不在应用程序的归档目录中。大多数 WEB-INF 节点都不是应用程序公共文档树的一部分。除了 静态资源和 `WEB-INF/lib` 目录下打包在 JAR 文件中 `META-INF/resources` 目录下的 JSP 文件之外，WEB-INF 目录下包含的其他任何文件都不能由容器直接提供给客户端访问。然而，WEB-INF 目录中的内容可以通过 servlet 代码调用 `ServletContext` 的 `getResource` 和 `getResourceAsStream` 方法来访问，并可使用 RequestDispatcher 调用公开这些内容。因此，如果应用开发人员想通过 servlet 代码访问这些内容，而不愿 意直接对客户端公开应用程序指定配置信息，那么可以把它放在这个目录下。

■ servlet 和实用工具类目录/WEB-INF/classes/。此目录中的类对应用程序类加载器必须是可见的。
■ java 归档文件区域/WEB-INF/lib/*.jar。这些文件中包括了 servlet，beans，静态资源和打包在 JAR 文件中 的 JSP 文件，以及其他对 Web 应用程序有用的实用工具类。Web 应用程序的类加载器必须能够从这些归档 文件中加载类。
Web 应用程序类加载器必须先从 WEB-INF/classes 目录下加载类，然后从 WEB-INF/lib 目录下的 JAR 库中 加载
```

### Tomcat尝试调用加载器的顺序
`WebappClassLoader#loadClass()`方法负责Tomcat特殊的“委托逻辑”, 具体逻辑如下，

1. Call `findLoadedClass(String)` to check if the class has already been loaded.  If it has, the same Class object is returned.
2. If the delegate property is set to true, call the `loadClass()` method of the parent class loader, if any.
3. Call `findClass()` to find this class in our locally defined repositories. Call the `loadClass()` method of our parent class loader, if any.
4. If the class was found using the above steps, and the resolve flag is true, this method will then call `resolveClass(Class)` on the resulting Class object.
5. If calss was not found, returns `ClassNotFoundException`.


所以 Web 应用类加载器背离了默认的 Java 委托模式（根据 Servlet 规范 2.4 版的 9.7.2 Web Application Classloader一节中提供的建议）。当某个请求想从 Web 应用的 WebappX 类加载器中加载类时，该类加载器会先查看自己的仓库，而不是预先进行委托处理。There are exceptions。JRE 基类的部分类不能被重写。对于一些类（比如 J2SE 1.4+ 的 XML 解析器组件），可以使用 J2SE 1.4 支持的特性。最后，类加载器会显式地忽略所有包含 Servlet API 类的 JAR 文件，所以不要在 Web 应用包含任何这样的 JAR 文件。Tomcat 其他的类加载器则遵循常用的委托模式。


因此，从 Web 应用的角度来看，加载类或资源时，要查看的仓库及其顺序如下：

* JVM 的 Bootstrap 类
* Web 应用的 `/WEB-INF/classes` 类
* Web 应用的 `/WEB-INF/lib/*.jar` 类
* System 类加载器的类（如上所述）
* Common 类加载器的类（如上所述）


如果 Web 应用类加载器配置有 <Loader delegate="true"/>，则顺序变为：

* JVM 的 Bootstrap 类
* System 类加载器的类（如上所述）
* Common 类加载器的类（如上所述）
* Web 应用的 `/WEB-INF/classes` 类
* Web 应用的 `/WEB-INF/lib/*.jar` 类

但没有了“委托模式”的保护，本地的一些和核心库同名的类会被误当做核心库组件加载。为了避免安全性的问题，`WebappClassLoader`类不允许载入指定的某些类，也不会将载入类的任务委托给系统类载入器去执行。这些类的名字储存在一个字符串数组变量`triggers`中，
```java
private static final String[] triggers = {
    "javax.servlet.Servlet",    // Servlet API
    "javax",                    // Java extensions
    "org.xml.sax",              // Sax 1 & 2
    "org.w3c.dom",              // Dom 1 & 2
    "org.apache.xerces",        // Xerces 1 & 2
    "org.apache.xalan"          // Xalan
};
```

### `loadClass()`和`findClass()`和`defineClass()`
他们是`ClassLoader`里比较重要的3个方法，
* `loadClass()`封装了委托模式，以及缓存检查。如果确实需要当前加载器加载类，才调用`findClass()`。
* `findClass()`方法是根据类的全具名读取类文件具体执行的地方。
* `defineClass()`根据`findClass()`方法读取的`byte[]`字节流，生成一个`Class`类实例。

自己开发加载器最好不要腹泻`loadClass()`方法，而是覆写`findClass()`方法。而且由于委托模式的存在，最初调用的`loadClass()`方法和最终调用的`defineClass()`函数可能不属于同一个加载器。执行`defineClass()`的是执行`loadClass()`的加载器的父加载器。这时候，
* 最初调用`loadClass()`所属的加载器叫 **“初始加载器”**。
* 最终调用`defineClass()`所在的加载器叫 **“定义加载器”**。

系统在判定两个类是否是同一个类的时候，不但要看他们是否具有相同的全具名，还要看加载他们的是不是同一个加载器。系统判定所属加载器依据的是“定义加载器”。就是看是哪个加载器最终调用了`defindClass()`生成`Class`对象。


### Servlet的加载器组件`WebappLoader`，和类加载器`WebappClassLoader`的概念要分开
`org.apache.catalina.Loader`接口定义的不是类加载器，而是Tomcat广义上的Servlet加载器的概念。`org.apache.cataline.loader.WebappLoader`是它对应的实现类。

`org.apache.cataline.loader.WebappLoader`里以一个包含着一个`org.apache.catalina.loader.WebappClassLoader`类的实例，它才是一个自定义的“类加载器”。它继承自`java.net.URLClassLoader`。所以和前几章本质上一样，实际负责载入Servlet类文件的都是`java.net.URLClassLoader`。

`WebappLoader`通过`getClassLoader()`方法拿到内部`WebappClassLoader`实例的引用。


#### 设置“仓库（Repository）”路径
`WebappClassLoader`拿不到Tomcat核心组件运行时的环境变量`CLASSPATH`，但需要用到的仓库`WEB-INF/classes`和`WEB-INF/lib`路径会在`WebappLoader`类的`start()`方法中调用`setRepositories()`方法传入到`WebappClassLoader`的`addRepository()`方法和`setJarPath()`方法中。

#### 设置访问权限
如果运行Tomcat时启用了"安全管理器（`java.lang.SecurityManager`）"，则`WebappLoader`的`setPermissions()`方法会为类载入器`WebappClassLoader`设置访问目录的权限。例如只能访问
`WEB-INF/classes`和`WEB-INF/lib`目录下的内容。

相关内容先查阅`java.lang.SecurityManager`类的手册。

### 重新载入
如果`WEB-INF/classes`和`WEB-INF/lib`目录下的某些类被重新编译了，那么这个类会被重新载入，无需重新启动Tomcat。

为了实现这个目的。每个资源都带有一个“时间戳”属性。`WebappLoader`类实现了`Runnable`接口，在一个独立的线程中周期性地检查每个资源的时间戳。间隔时间由`checkInterval`指定。

有两个方法比较重要，
* 调用`WebappLoader#modified()`方法检查已经载入的类是否被修改。
* 若已修改，调用`WebappLoader#nofityContext()`方法通知与WebappLoader实例关联的Context容器重新载入相关类。

```java
/**
 * The background thread that checks for session timeouts and shutdown.
 */
public void run() {

    if (debug >= 1)
        log("BACKGROUND THREAD Starting");

    // Loop until the termination semaphore is set
    while (!threadDone) {

        // Wait for our check interval
        threadSleep();

        if (!started)
            break;

        try {
            // Perform our modification check
            if (!classLoader.modified())
                continue;
        } catch (Exception e) {
            log(sm.getString("webappLoader.failModifiedCheck"), e);
            continue;
        }

        // Handle a need for reloading
        notifyContext();
        break;

    }

    if (debug >= 1)
        log("BACKGROUND THREAD Stopping");

}
```

`notifyContext()`方法不会直接调用Context的`reload()`方法，而是会实例化一个内部类`WebappContextNotifier`实例（也实现了`Runnable`接口），然后再一个新的线程中调用Context的`reload()`方法。
```java
/**
 * Notify our Context that a reload is appropriate.
 */
private void notifyContext() {

    WebappContextNotifier notifier = new WebappContextNotifier();
    (new Thread(notifier)).start();

}
```

```java
/**
 * Private thread class to notify our associated Context that we have
 * recognized the need for a reload.
 */
protected class WebappContextNotifier implements Runnable {


    /**
     * Perform the requested notification.
     */
    public void run() {

        ((Context) container).reload();

    }


}
```

第12章中会详细介绍`reload()`方法的实现。

#### `org.apache.catalina.loader.Reloader`接口
支持类的自动重载功能的类载入器需要实现`org.apache.catalina.loader.Reloader`接口。

### 类的缓存
每个由`WebappClassLoader`载入的类都被是为“资源”。资源用`org.apache.cataline.loader.ResourceEntry`类表示。每个`ResourceEntry`实例会保存所代表class文件的`byte[]`字节流，最后一次修改日期，Manifest信息，等。所有已经缓存的类会储存在一个名为`resourceEntries`的`HashMap`实例中。

### 总结 - 类加载器载入类的策略

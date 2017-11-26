---
layout: post
title: "How Tomcat Works - Chapter 13 - Host and Engine"
date: 2017-11-20 19:11:02
author: "Wei SHEN"
categories: ["java","how tomcat works","web"]
tags: ["container"]
description: >
---

### 部署
默认情况下，Tomcat会使用Engine容器，并且有一个Host容器作为其子容器。

### Host接口
Host接口中最重要的方法是`map()`，通过一个URI查找对应的Context子容器。
```java
public Context map(String uri);
```

### `StandardHost`类
构造`StandardHost`类实例时，构造函数默认会添加一个基础阀`org.apache.catalina.core.StandardHostValve`实例。

在`start()`方法中，还会再加两个阀`org.apache.catalina.valves.ErrorReportValve`类和`org.apache.catalina.valves.ErrorDispatcherValve`类。

### Host容器映射器查找Context容器: “切屁股”法
我们拿到`/app13/Primitive`这个完整的URI，怎么查找对应的Context容器呢？首先我们配置的唯一一个Context容器名字叫`app13`，
```java
/** code in com.ciaoshen.howtomcatworks.ex13.startup.Bootstrap1.java */

Context context = new StandardContext();
// StandardContext's start method adds a default mapper
context.setPath("/app13");
context.setDocBase("app13");
```
核心算法在`StandardHost#map(String)`函数里，我叫他“切屁股”算法。如果匹配失败，就切掉最后一节斜杠后的内容，继续匹配。
```java
while (true) {
    context = (Context) findChild(mapuri);
    if (context != null)
        break;
    int slash = mapuri.lastIndexOf('/');
    if (slash < 0)
        break;
    mapuri = mapuri.substring(0, slash);
}
```

比如我们拿到`/app13/Primitive`的完整URI，第一次完整匹配失败，就把最后一段`/Primitive`切掉，剩下的`/app13`继续匹配，从而匹配成功。

#### 映射器调用链
Host容器的映射函数调用过程有点曲折，`org.apache.catalina.core.StandardHostValve`类的`invoke()`函数会先调用`org.apache.catalina.core.StandardHost`类的基类`org.apache.catalina.core.ContainerBase`类的`map(Request request, boolean update)`函数。后者继续调用`org.apache.catalina.core.StandardHostMapper`类的`map(Request request, boolean update)`函数。后者最终再调用回`org.apache.catalina.core.StandardHost`类的`map(String uri)`。

```
org.apache.catalina.core.StandardHostValve # invoke()
                    |
                    |
org.apache.catalina.core.ContainerBase # Container map(Request request, boolean update)
                    |
                    |
org.apache.catalina.core.StandardHostMapper # Container map(Request request, boolean update)
                    |
                    |
org.apache.catalina.core.StandardHost # Context map(String uri)
```

### 为什么必须有一个Host容器？
因为需要用`org.apache.catalina.startup.ContextConfig`实例来配置一个Context对象。`ContextConfig`对象会在`applicationConfig()`方法里找到`web.xml`的位置，
```java
synchronized(webDigester) {
    try {
        // 调用org.apache.catalina.core.ApplicationContext#getResource()函数
        URL url = servletContext.getResource(Constants.ApplicationWebXml);
        InputSource is = new InputSource(url.toExternalForm());
        is.setByteStream(stream);
        ...
        webDigester.parse(is);
        ...
        ...
    } ...
}
```
找到`web.xml`的资源路径需要调用`org.apache.catalina.core.ApplicationContext`的`getResource()`函数。后者需要一个`hostName`的字符串变量，是Host对象的一个字段。所以父容器Host不能为空。
```java
public URL getResource(String path) throws MalformedURLException {
    DirContext resources = context.getResources();    
    if (resources != null) {
        String fullPath = context.getName() + path;
        // This is the problem! Host must not be null
        String hostName = context.getParent().getName();
        ...
    }
    ...
}
```

如果想不使用Host容器，必须自己实现一个`ContextConfig`类，就像`com.ciaoshen.howtomcatworks.ex13.core.SimpleContextConfig`类那样。

### 本章应用

### 关于`contextPath`的值
访问本章应用，在浏览器输入的是
> 127.0.0.1:8080/app13/Primitive

服务器在控制台输出显示`request.contextPath = /app13`。
```bash
/** 13章的应用 */
...
...
contextPath = /app13
requestURI = /app13/Primitive
relativeURI = /Primitive
servletPath = /Primitive
Find Exaxt Match Name: Primitive
...
...
```

这很好理解，因为在`Bootstrap`类里我们设置了Context容器的名字为`app13`。
```java
/** in com.ciaoshen.howtomcatworks.ex13.startup.Bootstrap1.java */

Context context = new StandardContext();
// StandardContext's start method adds a default mapper
context.setPath("/app13");
context.setDocBase("app13");
```

但在第11章的应用中，在浏览器输入的是。
> 127.0.0.1:8080/Primitive

并且如果在控制台打印`request.contextPath`的值为空`""`。
```bash
/** 11章的应用 */
...
...
contextPath =
requestURI = /Primitive
relativeURI = /Primitive
servletPath = /Primitive
Find Exaxt Match Name: Primitive
...
...
```

如果输入带有Context容器的名字`127.0.0.1:8080/app11/Primitive`，服务器将无法找到正确的资源。问题是第11章的应用同样设置了Context容器的名字为`app11`（`setPath()`函数设置的是Context容器的`name`字段）。
```java
/** in com.ciaoshen.howtomcatworks.ex11.startup.Bootstrap.java */

Context context = new StandardContext();
// StandardContext's start method adds a default mapper
context.setPath("/app11");
context.setDocBase("app11");
```

为什么13章有了Host容器，就能正确解析`contextPath`，而11章用Context容器作为顶级容器就不能？解析`contextPath`的操作是在什么时候完成的？是像我最初猜测的`HttpProcessor#parseRequest()`函数里解析的吗？

查了源码以后发现，
> `HttpRequestImpl#contextPath`字段不是在`HttpProcessor#parseRequest()`函数里解析的，而是在`StandardHost#map()`函数里解析，然后在`StandardHostMapper#map()`函数里更新的。

#### 首先明确`contextPath`是什么
1. 容器从Connector拿到的`request`参数，是`org.apache.catalina.connector.http.HttpRequestImpl`类实例。
2. `HttpRequestImpl`类继承自`org.apache.catalina.connector.HttpRequestBase`类。
3. `HttpRequestBase`类有一个`contextPath`字段，可以通过`getContextPath()`和`setContextPath()`函数获取和设置此字段。
4. `contextPath`字段的默认值为空`""`。

```java
public class HttpRequestBase
    extends RequestBase
    implements HttpRequest, HttpServletRequest {

    /**
     * The context path for this request.
     */
    protected String contextPath = "";

    // many code omitted

    /**
     * Return the portion of the request URI used to select the Context
     * of the Request.
     */
    public String getContextPath() {

        return (contextPath);

    }
    /**
     * Set the context path for this Request.  This will normally be called
     * when the associated Context is mapping the Request to a particular
     * Wrapper.
     *
     * @param path The context path
     */
    public void setContextPath(String path) {

        if (path == null)
            this.contextPath = "";
        else
            this.contextPath = path;

    }

    // many code omitted

}
```

#### `contextPath`有什么用？
在`org.apache.catalina.core.StandardContextMapper`映射器的`map()`函数里，拿到完整的URI（"/Primitive"），从里面扣掉`contextPath`，剩下来的赋值给`relativeURI`变量。这个`relativeURI`就直接被用来匹配Wrapper容器。11章的应用里`contextPath`值为空字符串，所以最终就拿"/Primitive"来匹配Wrapper容器。

```java
public Container map(Request request, boolean update) {

    // code omitted

    // Identify the context-relative URI to be mapped
    String contextPath = ((HttpServletRequest) request.getRequest()).getContextPath();
    String requestURI = ((HttpRequest) request).getDecodedRequestURI();
    String relativeURI = requestURI.substring(contextPath.length());

    // code omitted
}
```

之前猜测可能是在`org.apache.catalina.connector.http.HttpProcessor`的`parseRequest()`函数里解析。但检查源码以后，发现只解析了`protocol`，`port`等信息，资源路径的主体部分`/app13/Primitive`是作为完整的字符串保留的。没有进一步解析。

#### 最终答案: 在`StandardHost#map()`函数里解析
在`org.apache.catalina.connector.HttpRequestBase`类的`setContextPath()`函数的注解里写了设置`contextPath`的函数主要是在映射子容器的时候被用到。
```java
/**
 * Set the context path for this Request.  This will normally be called
 * when the associated Context is mapping the Request to a particular
 * Wrapper.
 *
 * @param path The context path
 */
public void setContextPath(String path) {

    if (path == null)
        this.contextPath = "";
    else
        this.contextPath = path;

}
```

最后在在`org.apache.catalina.core.StandardHostMapper`的`map()`函数里找到答案，如果成功匹配到了Context容器，就会调用`org.apache.catalina.connector.http.HttpRequestImpl`的`setContextPath()`函数更新`contextPath`字段的值。
```java
public class StandardHostMapper
    implements Mapper {

    // many code omitted

    /**
     * Return the child Container that should be used to process this Request,
     * based upon its characteristics.  If no such child Container can be
     * identified, return <code>null</code> instead.
     *
     * @param request Request being processed
     * @param update Update the Request to reflect the mapping selection?
     */
    public Container map(Request request, boolean update) {
        // Has this request already been mapped?
        if (update && (request.getContext() != null))
            return (request.getContext());

        // Perform mapping on our request URI
        String uri = ((HttpRequest) request).getDecodedRequestURI();
        Context context = host.map(uri);

        // Update the request (if requested) and return the selected Context
        if (update) {
            request.setContext(context);
            if (context != null)
                ((HttpRequest) request).setContextPath(context.getPath());
            else
                ((HttpRequest) request).setContextPath(null);
        }
        return (context);

    }

    // many code omitted

}
```

而`StandardHostMapper`的`map()`函数最终调用的是`StandardHost`的`map()`函数查找Host容器的Context子容器。所以最终实际解析的工作就在那里完成。实际最核心的解析策略代码如下，就是通过逐步缩短prefix来匹配到Context容器的名称。比如我们拿到`/app13/Primitive`，没有匹配到对应的Context容器，就切掉最后的`/Primitive`，得到`/app13`，成功匹配。匹配成功后在`StandardHostMapper`的`map()`函数里就更新这个`/app13`这个Context容器实例当前绑定的`HttpRequestImpl`实例的`contextPath`字段。然后当这个Context容器要进行后续查找Wrapper容器的时候，就可以从完整的URI`/app13/Primitive`里把`contextPath`抠掉，找到正确的`/Primitive`Wrapper容器名。
```java
while (true) {
    context = (Context) findChild(mapuri);
    if (context != null)
        break;
    int slash = mapuri.lastIndexOf('/');
    if (slash < 0)
        break;
    mapuri = mapuri.substring(0, slash);
}
```

### 应用`Bootstrap2`

#### 临时添加默认Host子容器
`Bootstrap2.java`里调用`setDefaultHost()`函数将`localhost`设置为Engine的默认Host子容器。因为`org.apache.catalina.core.StandardEngine`类的默认映射器`org.apache.catalina.core.StandardEngineMapper`类的`map()`函数的查找Host子容器的策略需要用到和Engine关联的Server对象的`serverName`，如果找不到`serverName`，就启用默认Host子容器。这一章Server对象为空（留到14章介绍），所以暂且将仅有的一个Host容器设置为默认容器，使应用程序可以运行。
```java
public Container map(Request request, boolean update) {

    int debug = engine.getDebug();

    // Extract the requested server name
    String server = request.getRequest().getServerName();
    if (server == null) {
        server = engine.getDefaultHost(); // server为空，启用默认Host子容器
        if (update)
            request.setServerName(server);
    }

    // ...
    // ...

}
```

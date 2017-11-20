---
layout: post
title: "How Tomcat Works - Chapter 12 - StandardContext"
date: 2017-11-17 22:46:35
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["container"]
description: >
---

### `ContextConfig`监听器为StandardContext实例配置
StandardContext实例的`start()`方法中，会触发一个`BEFORE_START_EVENT`事件，然后监听器`org.apache.catalina.startup.ContextConfig`实例（实现了`LifecycleListener`接口），开始对StandardContext实例进行配置。
```java
// Notify our interested LifecycleListeners
lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
```

配置过程中会用`Digester`库解析`web.xml`配置文件，文件的具体路径如下，
> CATALINA_HOME/conf/web.xml

关于`Digester`库的细节，详见第15章。

### 默认Mapper
StandardContext定义的默认`mapperClass`字段是硬编码的，但也可以自己设置。
```java
private String mapperClass = "org.apache.catalina.core.StandardContextMapper";
```

`StandardContextMapper`里最重要的是`map()`函数，匹配`Wrapper`容器的策略是“4步走”:
1. Exaxt Match: 匹配`relativeURI`(`relativeURI` = `requestURI` - `contextPath`)
2. Prifix Match: 匹配`relativeURI` + `/*`
3. Extension Match: 匹配`*.扩展名`
4. Default Match: 匹配`/`

比如我的路径`127.0.0.1:8080/Primitive`，首先`contextPath`为空，`requestURI`就是端口后面的部分`/Primitive`，然后从`requestURI`里面出去`contextPath`，剩下的就是一个相对路径`relativeURI`。因为`contextPath`为空，所以最后`relativeURI`就是`/Primitive`。
```bash
contextPath = ""
requestURI = "/Primitive"
relativeURI = "/Primitive"
```
我们的例子里根据第一步"Exaxt Match"就匹配到了Servlet Name = `Primitive`。 因为在`Bootstrap.java`里设置了从`URI`到"Servlet Name"的映射，这样就能帮助我们找到需要加载的`Wrapper`。
```java
context.addServletMapping("/Primitive", "Primitive");
```

然后再查找`StandardWrapper`里设置的实际类全具名`PrimitiveServlet`就不是`Mapper`的工作了，
```java
Wrapper wrapper1 = new StandardWrapper();
wrapper1.setName("Primitive");
wrapper1.setServletClass("PrimitiveServlet");
```

```java
public Container map(Request request, boolean update) {


    int debug = context.getDebug();

    // Has this request already been mapped?
    if (update && (request.getWrapper() != null))
        return (request.getWrapper());

    // Identify the context-relative URI to be mapped
    String contextPath = ((HttpServletRequest) request.getRequest()).getContextPath();
    String requestURI = ((HttpRequest) request).getDecodedRequestURI();
    String relativeURI = requestURI.substring(contextPath.length());

    if (debug >= 1)
        context.log("Mapping contextPath='" + contextPath +
                    "' with requestURI='" + requestURI +
                    "' and relativeURI='" + relativeURI + "'");

    // Apply the standard request URI mapping rules from the specification
    Wrapper wrapper = null;
    String servletPath = relativeURI;
    String pathInfo = null;
    String name = null;

    // Rule 1 -- Exact Match
    if (wrapper == null) {
        if (debug >= 2)
            context.log("  Trying exact match");
        if (!(relativeURI.equals("/")))
            name = context.findServletMapping(relativeURI);
        if (name != null)
            wrapper = (Wrapper) context.findChild(name);
        if (wrapper != null) {
            servletPath = relativeURI;
            pathInfo = null;
        }
    }

    // Rule 2 -- Prefix Match
    if (wrapper == null) {
        if (debug >= 2)
            context.log("  Trying prefix match");
        servletPath = relativeURI;
        while (true) {
            name = context.findServletMapping(servletPath + "/*");
            if (name != null)
                wrapper = (Wrapper) context.findChild(name);
            if (wrapper != null) {
                pathInfo = relativeURI.substring(servletPath.length());
                if (pathInfo.length() == 0)
                    pathInfo = null;
                break;
            }
            int slash = servletPath.lastIndexOf('/');
            if (slash < 0)
                break;
            servletPath = servletPath.substring(0, slash);
        }
    }

    // Rule 3 -- Extension Match
    if (wrapper == null) {
        if (debug >= 2)
            context.log("  Trying extension match");
        int slash = relativeURI.lastIndexOf('/');
        if (slash >= 0) {
            String last = relativeURI.substring(slash);
            int period = last.lastIndexOf('.');
            if (period >= 0) {
                String pattern = "*" + last.substring(period);
                name = context.findServletMapping(pattern);
                if (name != null)
                    wrapper = (Wrapper) context.findChild(name);
                if (wrapper != null) {
                    servletPath = relativeURI;
                    pathInfo = null;
                }
            }
        }
    }

    // Rule 4 -- Default Match
    if (wrapper == null) {
        if (debug >= 2)
            context.log("  Trying default match");
        name = context.findServletMapping("/");
        if (name != null)
            wrapper = (Wrapper) context.findChild(name);
        if (wrapper != null) {
            servletPath = relativeURI;
            pathInfo = null;
        }
    }

    // Update the Request (if requested) and return this Wrapper
    if ((debug >= 1) && (wrapper != null))
        context.log(" Mapped to servlet '" + wrapper.getName() +
                    "' with servlet path '" + servletPath +
                    "' and path info '" + pathInfo +
                    "' and update=" + update);
    if (update) {
        request.setWrapper(wrapper);
        ((HttpRequest) request).setServletPath(servletPath);
        ((HttpRequest) request).setPathInfo(pathInfo);
    }
    return (wrapper);

}
```

### 重载
当`web.xml`文件发生变化，或者`WEB-INF/classes`目录下的其中一个文件被重新编译后，应用程序会重载。

Tomcat 4中`WebappLoader`用另一个线程周期性地检查`WEB-INF`目录中的所有类和JAR文件的时间戳。需要`StandardContext`和`WebappLoader`是双向绑定的。这样加载器也能找到容器。

所以Tomcat 4运行Context容器的加载器和Session管理器这些组件都需要自己的后台线程。这就会导致资源浪费。

Tomcat 5用`ContainerBackgroundProcessor`类用一个后台线程统一检查`WEB-INF`目录中类的时间戳，还要帮助Session管理器检查会话有效期。一个后台线程大管家。

它通过`ContainerBase`的`start()`方法调用`threadStart()`方法启动。

它的`processChildren()`方法会调用自身容器的`backgroundProgress()`方法，然后递归调用每个子容器的`processChildren()`。这样可以确保每个子容器的`backgroundProgress()`方法都被调用。

### 重新理清路径
实际应用中：
* `System#catalina.base` = "/Users/Wei/github/HowTomcatWorks/webapps/"
* `Context#name` = "/app12"
* `Context#docBase` = "app12"
* 配置监听器是`org.apache.catalina.startup/ContextConfig`类实例。
* 默认解析的配置文件位于`${catalina.base}/conf/web.xml`，所以最终的绝对路径为："/Users/Wei/github/HowTomcatWorks/webapps/conf/web.xml"
* `Context#loader`默认`org.apache.catalina.loader.WebappLoader`
* `WebappLoader#classLoader`默认`org.apache.catalina.loader.WebappClassLoader`
* `Context#mappers`默认`org.apache.catalina.core.StandardContextMapper`
* `WebappClassLoader#repositories`把`catalina.base`和`docBase`拼在一起得到: "/Users/Wei/github/HowTomcatWorks/webapps/app12/WEB-INF/classes/"
* `Context#workDir`是一个相对路径：`${catalina.base}/work/_/_/app11`。最终的绝对路径是："/Users/Wei/github/HowTomcatWorks/webapps/work/_/_/app11`

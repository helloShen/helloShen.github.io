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

### 为什么必须有一个Host容器？

### `HttpServletRequest#getContextPath()`函数什么时候返回空字符串`""`？
当我在浏览器输入`127.0.0.1:8080/app13/Primitive`，服务器在控制台输出显示`contextPath = /app13`？
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

为什么在之前的应用中，如果在浏览器输入`127.0.0.1:8080/app11/Primitive`，找不到我们要的资源？只有输入`127.0.0.1:8080/Primitive`的时候才找到正确的资源？并且`contextPath = ""`?
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

```
javax.servlet.http
Interface HttpServletRequest

public java.lang.String getContextPath()

Returns the portion of the request URI that indicates the context of the request. The context path always comes first in a request URI. The path starts with a "/" character but does not end with a "/" character. For servlets in the default (root) context, this method returns "". The container does not decode this string.
Returns:
a String specifying the portion of the request URI that indicates the context of the request
```

```
You may define as many Context elements as you wish. Each such Context MUST have a unique context name within a virtual host. The context path does not need to be unique (see parallel deployment below). In addition, a Context must be present with a context path equal to a zero-length string. This Context becomes the default web application for this virtual host, and is used to process all requests that do not match any other Context's context path.
```

在`StandardContextMapper#map()`函数中就是返回了`HttpServletRequest`实例的`contextPath`字段，关键是这个字段是什么时候被设置的？设置它需要调用`setContextPath()`函数，那这个函数是什么时候被调用的？
```java
public final class StandardContextMapper
    implements Mapper {

    // many code omitted

    public Container map(Request request, boolean update) {    

        // many code omitted

        String contextPath = ((HttpServletRequest) request.getRequest()).getContextPath();

        // many code omitted

    }

    // many code omitted
}
```

#### 最终答案
在`StandardHostMapper`的`map()`函数里，如果成功匹配到了Context容器，就会调用`HttpRequestImpl`的`setContextPath()`函数更新`contextPath`字段的值。
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

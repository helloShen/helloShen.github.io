---
layout: post
title: "How Tomcat Works - Chapter 19 - Manager"
date: 2017-12-06 23:23:33
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["manager"]
description: >
---

### ManagerServlet是干什么的？
在Tomcat 4里是`org.apache.catalina.servlets.ManagerServlet`类。 在Tomcat 5里是`org.apache.catalina.manager.ManagerServlet`类。

`ManagerServlet`类是一个Servlet应用程序，但是比较特殊的一个。因为通过访问它，可以控制和管理Host上部署的其他Servlet应用。

### 通过实现`ContainerServlet`接口实现对其他Servlet应用的控制？
要实现对服务器上其他应用的控制，首先就要能访问到他们的引用。`ManagerServlet`通过实现`org.apache.catalina.ContainerServlet`接口可以拿到表示它的`StandardWrapper`对象的引用，从而可以进一步拿到关联的上层`StandardContext`实例引用和`StandardHost`实例引用（也就是部署器`Depoloyer`）。通过他们就可以访问到所有部署的应用。

`ContainerServlet`接口主要就是定义了`getWrapper()`和`setWrapper()`函数签名。

### ManagerServlet通过XML部署描述符部署
部署描述符路径为：
> CATALINA_HOME/server/webapps/manager.xml

比如下面为随书附带的`manager.xml`，其中定义了访问ManagerServlet类的路径为`/manager`,
```xml
<Context path="/manager" docBase="../server/webapps/manager"
        debug="0" privileged="true">

  <!-- Link to the user database we will get roles from -->
  <ResourceLink name="users" global="UserDatabase"
                type="org.apache.catalina.UserDatabase"/>

</Context>
```

所以下面模式开始的URL可以调用ManagerServlet:
> http://localhost:8080/manager/

另外，实际部署中经常被加上`<security-constraint>`的访问限制，只有拥有`manager`角色的用户才能访问。

Tomcat中用户和角色列表储存于：`CATALINA_HOME/conf/tomcat-users.xml`文件中。

### ManagerServlet的几个常用功能

#### 列出已经部署的Web应用程序
> http://localhost:8080/manager/list

上面的URL会调用ManagerServlet的`list()`方法，后者会调用`Deployer#findDeployedApps()`函数拿到所有已部署的Context应用的名称字符串。

```java
/**
 * Render a list of the currently active Contexts in our virtual host.
 *
 * @param writer Writer to render to
 */
protected void list(PrintWriter writer) {

    if (debug >= 1)
        log("list: Listing contexts for virtual host '" +
            deployer.getName() + "'");

    writer.println(sm.getString("managerServlet.listed",
                                deployer.getName()));
    String contextPaths[] = deployer.findDeployedApps();
    for (int i = 0; i < contextPaths.length; i++) {
        Context context = deployer.findDeployedApp(contextPaths[i]); // 拿到所有已部署的Context应用的名称字符串
        String displayPath = contextPaths[i];
        if( displayPath.equals("") )
            displayPath = "/";
        if (context != null ) {
            if (context.getAvailable()) {
                writer.println(sm.getString("managerServlet.listitem",
                                            displayPath,
                                            "running",
                                  "" + context.getManager().findSessions().length,
                                            context.getDocBase()));
            } else {
                writer.println(sm.getString("managerServlet.listitem",
                                            displayPath,
                                            "stopped",
                                            "0",
                                            context.getDocBase()));
            }
        }
    }
}
```

#### 启动Web应用程序
> http://localhost:8080/manager/start?path=/your-context-path

上面的URL可以启动某个Web应用程序，只需要在最后给出目标Web应用程序的路径。比如要启动Admin应用程序，可以使用，

> http://localhost:8080/manager/start?path=/admin

启动过程通过调用ManagerServlet的`start()`函数完成，代码和之前的`list()`函数类似。

#### 关闭Web应用程序
和启动Web应用程序的功能类似，
> http://localhost:8080/manager/stop?path=/your-context-path

上面的URL可以调用ManagerServlet的`stop()`函数，从而关闭某个Web应用程序。

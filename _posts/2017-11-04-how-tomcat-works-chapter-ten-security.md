---
layout: post
title: "How Tomcat Works - Chapter 10 - Security"
date: 2017-11-04 21:50:04
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["security"]
description: >
---

### 框架
Context容器需要关联以下4个组件，
1. `Realm`
2. `Constraint`
3. `LoginConfig`
4. `Authenticator`

其中`Realm`,`constraint`,`LoginConfig`在`Bootstrap1`里配置，最后一个`Authenticator`在`SimpleLoginConfig`里配置。

简单讲4个组件的关系就是，
> `LoginConfig`用来配置`Authenticator`。然后`Authenticator`负责调用`Realm`里的`authenticate()`验证函数。验证函数中将会将用户输入的用户名和密码，和`Constraint`中预设的合法

### 调用链
`com.ciaoshen.howtomcatworks.ex10.startup.Bootstrap1`创建了`org.apache.catalina.deploy.SecurityCollection`和`org.apache.catalina.deploy.SecurityConstraint`。把他们关联到了`org.apache.catalina.Context`上。

`Lifecycle.START_EVENT`事件触发`com.ciaoshen.howtomcatworks.ex10.core.SimpleContextConfig`的`lifecycleEvent()`函数。`lifecycleEvent()`函数调用`authenticatorConfig()`函数。在`authenticatorConfig()`函数里创建`org.apache.catalina.authenticator.BasicAuthenticator`实例，并以`org.apache.catalina.valves.Valve`的身份封装到Context容器的`org.apache.catalina.Pipeline`实例里。

之后`org.apache.catalina.authenticator.BasicAuthenticator`就会作为Context容器的一个阀被运行，先是它`invoke()`函数被调用，马上`invoke()`转而调用`authenticate()`函数验证用户名和密码，再调用`accessControl()`函数验证用户权限。


### `SecurityConstraint`
书上关于`SecurityConstraint`怎么生效的没有说名。只说了`BasicAuthenticator`作为一个阀被运行以后，`invoke()`函数会调用`authenticate()`函数进行身份验证。但实际上`authenticate()`函数只负责`[用户名，密码]`的验证。并没有用到之前在`Bootstrap1`中创建的`SecurityConstraint`访问权限的限制。

但例子里又确实进行了访问权限的验证，如果我们输入`[cindy,bamboo]`的用户名和密码，系统提示登录未通过。必须输入`[ken,blackcomb]`才能顺利登录。因为`cindy`只有普通程序员的权限，不具备管理员权限，有管理员权限的用户只有`ken`。说明`SecurityConstraint`里设置的`constraint.addAuthRole("manager")`确实生效了。

实际的访问权限验证过程也是在验证器的`invoke()`函数里被调用的。`BasicAuthenticator`的基类`AuthenticatorBase`类定义了`invoke()`函数，在调用`authenticate()`函数验证用户名和密码之后，有调用了`accessControl()`函数进行用户访问权限验证。
```java
public abstract class AuthenticatorBase
    extends ValveBase
    implements Authenticator, Lifecycle {

    // ... large number of code omitted ...
    // ... large number of code omitted ...
    // ... large number of code omitted ...

    public void invoke(Request request, Response response, ValveContext context)
        throws IOException, ServletException {

        // some code omitted ...

        // Enforce any user data constraint for this security constraint
        if (debug >= 1)
            log(" Calling checkUserData()");
        if (!checkUserData(hrequest, hresponse, constraint)) {
            if (debug >= 1)
                log(" Failed checkUserData() test");
            // ASSERT: Authenticator already set the appropriate
            // HTTP status code, so we do not have to do anything special
            return;
        }

        // Authenticate based upon the specified login configuration
        if (constraint.getAuthConstraint()) {
            if (debug >= 1)
                log(" Calling authenticate()");
            if (!authenticate(hrequest, hresponse, config)) {
                if (debug >= 1)
                    log(" Failed authenticate() test");
                // ASSERT: Authenticator already set the appropriate
                // HTTP status code, so we do not have to do anything special
                return;
            }
        }

        // Perform access control based on the specified role(s)
        if (constraint.getAuthConstraint()) {
            if (debug >= 1)
                log(" Calling accessControl()");
            /**
             * 用户访问权限验证（没有管理员权限的cindy无法通过验证。只有ken能访问资源）
             */
            if (!accessControl(hrequest, hresponse, constraint)) {
                if (debug >= 1)
                    log(" Failed accessControl() test");
                // ASSERT: AccessControl method has already set the appropriate
                // HTTP status code, so we do not have to do anything special
                return;
            }
        }

        // some code omitted ...

    }

    // some code omitted ...
}
```

---
layout: post
title: "How Tomcat Works - Chapter 15 - Digester"
date: 2017-12-01 20:57:01
author: "Wei SHEN"
categories: ["web","java","how tomcat works"]
tags: ["digester","config"]
description: >
---

### 配置文件地址
一个默认配置文件，一个应用专属配置文件。地址硬编码在`org.apache.catalina.startup.Constants`类里，
```java
/** in org.apache.catalina.startup.Constants.java */
public static final String ApplicationWebXml = "/WEB-INF/web.xml";
public static final String DefaultWebXml = "conf/web.xml";
```

### Digester对象的创建
在`org.apache.catalina.startup.ContextConfig`类里，调用`createWebDigester()`函数创建`org.apache.commons.digester.Digester`类实例，
```java
/** in org.apache.catalina.startup.ContextConfig.java */
/**
 * The <code>Digester</code> we will use to process web application
 * deployment descriptor files.
 */
private static Digester webDigester = createWebDigester();
```
`createWebDigester()`函数的代码如下，过程中需要用到`DTD`文件，DTD文件是用来定义XML文件元素所对应的数据类型的（因为创建对象，需要知道信息对应的数据类型，比如String还是int）。
```java
/**
 * Create (if necessary) and return a Digester configured to process the
 * web application deployment descriptor (web.xml).
 */
private static Digester createWebDigester() {

    URL url = null;
    Digester webDigester = new Digester();
    webDigester.setValidating(true);
    url = ContextConfig.class.getResource(Constants.WebDtdResourcePath_22);
    webDigester.register(Constants.WebDtdPublicId_22,
                         url.toString());
    url = ContextConfig.class.getResource(Constants.WebDtdResourcePath_23);
    webDigester.register(Constants.WebDtdPublicId_23,
                         url.toString());
    webDigester.addRuleSet(new WebRuleSet());
    return (webDigester);

}
```
`DTD`文件的ID和路径信息硬编码在`org.apache.catalina.startup.Constants`类里，
```java
/** in org.apache.catalina.startup.Constants.java */
public static final String WebDtdPublicId_22 =
    "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN";
public static final String WebDtdResourcePath_22 =
    //      "conf/web_22.dtd";
    "/javax/servlet/resources/web-app_2_2.dtd";

public static final String WebDtdPublicId_23 =
    "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
public static final String WebDtdResourcePath_23 =
    //      "conf/web_23.dtd";
    "/javax/servlet/resources/web-app_2_3.dtd";
```

除了DTD文件还要用到`org.apache.catalina.startup.WebRuleSet`类。它是`org.apache.commons.digester.RuleSetBase`类的子类。它定义了一系列用来解析`web.xml`部署描述器文件的规则(`Rule`)。

`RuleSetBase`类是一个抽象类，只是声明了一个`addRuleInstances(Digester)`接口方法，
```java
public abstract void addRuleInstances(Digester digester);
```

具体的规则都在`WebRuleSet`类的`addRuleInstances(Digester)`函数的实现里。


#### 调用链
```
org.apache.catalina.startup.ContextConfig#Constructor

org.apache.catalina.startup.ContextConfig.createWebDigester()

org.apache.commons.digester.Digester#Constructor

org.apache.commons.digester.Digester#register(String,String) // 注册/javax/servlet/resources/web-app_2_2.dtd

org.apache.commons.digester.Digester#register(String,String) // 注册/javax/servlet/resources/web-app_2_3.dtd

org.apache.commons.digester.Digester#addRuleSet(WebRuleSet)
```


### 调用链
```
org.apache.catalina.core.StandardHost#start()

org.apache.catalina.core.ContainerBase#start()

org.apache.catalina.core.StandardContext#start()

BEFORE_START_EVENT

org.apache.catalina.startup.ContextConfig#lifecycleEvent(LifecycleEvent)

org.apache.catalina.startup.ContextConfig#start()

org.apache.catalina.startup.ContextConfig#defaultConfig()
org.apache.catalina.startup.ContextConfig#applicationConfig()

org.apache.commons.digester.Digester.parse(InputSource)

org.apache.catalina
org.apache.catalina
org.apache.catalina
org.apache.catalina
org.apache.catalina
org.apache.catalina
org.apache.catalina
```

### 附录A - 配置文件`web.xml`样例
标准的`web.xml`文件当然不是随便编写的，所用到的元素必须符合一整套规范，具体规范参见 ->
<https://docs.oracle.com/cd/E24329_01/web.1211/e21049/web_xml.htm#WBAPP502>

#### 一个最简单的Demo
```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
      http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
     version="2.4">

     <description>developerWorks Beginning Tomcat Tutorial
      </description>
     <display-name>
          IBM developerWorks Beginning Tomcat Tutorial Step 2
     </display-name>

     <servlet>
          <servlet-name>Specials</servlet-name>
          <servlet-class>
               com.ibm.dw.tutorial.tomcat.SpecialsServlet
          </servlet-class>
     </servlet>
     <servlet-mapping>
          <servlet-name>Specials</servlet-name>
          <url-pattern>/showspecials.cgi</url-pattern>
     </servlet-mapping>

</web-app>
```

#### 一个稍复杂一点的Demo
Demo -> <https://tomcat.apache.org/tomcat-4.1-doc/appdev/web.xml.txt>

```xml
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

<?xml version="1.0" encoding="ISO-8859-1"?>

<!DOCTYPE web-app
  PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
  "http://java.sun.com/dtd/web-app_2_3.dtd">

<web-app>


  <!-- General description of your web application -->

  <display-name>My Web Application</display-name>
  <description>
    This is version X.X of an application to perform
    a wild and wonderful task, based on servlets and
    JSP pages.  It was written by Dave Developer
    (dave@mycompany.com), who should be contacted for
    more information.
  </description>


  <!-- Context initialization parameters that define shared
       String constants used within your application, which
       can be customized by the system administrator who is
       installing your application.  The values actually
       assigned to these parameters can be retrieved in a
       servlet or JSP page by calling:

           String value =
             getServletContext().getInitParameter("name");

       where "name" matches the <param-name> element of
       one of these initialization parameters.

       You can define any number of context initialization
       parameters, including zero.
  -->

  <context-param>
    <param-name>webmaster</param-name>
    <param-value>myaddress@mycompany.com</param-value>
    <description>
      The EMAIL address of the administrator to whom questions
      and comments about this application should be addressed.
    </description>
  </context-param>


  <!-- Servlet definitions for the servlets that make up
       your web application, including initialization
       parameters.  With Tomcat, you can also send requests
       to servlets not listed here with a request like this:

         http://localhost:8080/{context-path}/servlet/{classname}

       but this usage is not guaranteed to be portable.  It also
       makes relative references to images and other resources
       required by your servlet more complicated, so defining
       all of your servlets (and defining a mapping to them with
       a servlet-mapping element) is recommended.

       Servlet initialization parameters can be retrieved in a
       servlet or JSP page by calling:

           String value =
             getServletConfig().getInitParameter("name");

       where "name" matches the <param-name> element of
       one of these initialization parameters.

       You can define any number of servlets, including zero.
  -->

  <servlet>
    <servlet-name>controller</servlet-name>
    <description>
      This servlet plays the "controller" role in the MVC architecture
      used in this application.  It is generally mapped to the ".do"
      filename extension with a servlet-mapping element, and all form
      submits in the app will be submitted to a request URI like
      "saveCustomer.do", which will therefore be mapped to this servlet.

      The initialization parameter namess for this servlet are the
      "servlet path" that will be received by this servlet (after the
      filename extension is removed).  The corresponding value is the
      name of the action class that will be used to process this request.
    </description>
    <servlet-class>com.mycompany.mypackage.ControllerServlet</servlet-class>
    <init-param>
      <param-name>listOrders</param-name>
      <param-value>com.mycompany.myactions.ListOrdersAction</param-value>
    </init-param>
    <init-param>
      <param-name>saveCustomer</param-name>
      <param-value>com.mycompany.myactions.SaveCustomerAction</param-value>
    </init-param>
    <!-- Load this servlet at server startup time -->
    <load-on-startup>5</load-on-startup>
  </servlet>

  <servlet>
    <servlet-name>graph</servlet-name>
    <description>
      This servlet produces GIF images that are dynamically generated
      graphs, based on the input parameters included on the request.
      It is generally mapped to a specific request URI like "/graph".
    </description>
  </servlet>


  <!-- Define mappings that are used by the servlet container to
       translate a particular request URI (context-relative) to a
       particular servlet.  The examples below correspond to the
       servlet descriptions above.  Thus, a request URI like:

         http://localhost:8080/{contextpath}/graph

       will be mapped to the "graph" servlet, while a request like:

         http://localhost:8080/{contextpath}/saveCustomer.do

       will be mapped to the "controller" servlet.

       You may define any number of servlet mappings, including zero.
       It is also legal to define more than one mapping for the same
       servlet, if you wish to.
  -->

  <servlet-mapping>
    <servlet-name>controller</servlet-name>
    <url-pattern>*.do</url-pattern>
  </servlet-mapping>

  <servlet-mapping>
    <servlet-name>graph</servlet-name>
    <url-pattern>/graph</url-pattern>
  </servlet-mapping>


  <!-- Define the default session timeout for your application,
       in minutes.  From a servlet or JSP page, you can modify
       the timeout for a particular session dynamically by using
       HttpSession.getMaxInactiveInterval(). -->

  <session-config>
    <session-timeout>30</session-timeout>    <!-- 30 minutes -->
  </session-config>


</web-app>
```

### 附录B - `org.apache.catalina.startup.WebRuleSet#addRuleInstances(Digester)`函数的实现
解析`web.xml`的具体规则写在这个地方，这是配合`web.xml`文件的具体语法的，
```java
/**
 * <p>Add the set of Rule instances defined in this RuleSet to the
 * specified <code>Digester</code> instance, associating them with
 * our namespace URI (if any).  This method should only be called
 * by a Digester instance.</p>
 *
 * @param digester Digester instance to which the new Rule instances
 *  should be added.
 */
public void addRuleInstances(Digester digester) {

    digester.addRule(prefix + "web-app",
                     new SetPublicIdRule(digester, "setPublicId"));

    digester.addCallMethod(prefix + "web-app/context-param",
                           "addParameter", 2);
    digester.addCallParam(prefix + "web-app/context-param/param-name", 0);
    digester.addCallParam(prefix + "web-app/context-param/param-value", 1);

    digester.addCallMethod(prefix + "web-app/display-name",
                           "setDisplayName", 0);

    digester.addRule(prefix + "web-app/distributable",
                     new SetDistributableRule(digester));

    digester.addObjectCreate(prefix + "web-app/ejb-local-ref",
                             "org.apache.catalina.deploy.ContextLocalEjb");
    digester.addSetNext(prefix + "web-app/ejb-local-ref",
                        "addLocalEjb",
                        "org.apache.catalina.deploy.ContextLocalEjb");

    digester.addCallMethod(prefix + "web-app/ejb-local-ref/description",
                           "setDescription", 0);
    digester.addCallMethod(prefix + "web-app/ejb-local-ref/ejb-link",
                           "setLink", 0);
    digester.addCallMethod(prefix + "web-app/ejb-local-ref/ejb-ref-name",
                           "setName", 0);
    digester.addCallMethod(prefix + "web-app/ejb-local-ref/ejb-ref-type",
                           "setType", 0);
    digester.addCallMethod(prefix + "web-app/ejb-local-ref/local",
                           "setLocal", 0);
    digester.addCallMethod(prefix + "web-app/ejb-local-ref/local-home",
                           "setHome", 0);

    digester.addObjectCreate(prefix + "web-app/ejb-ref",
                             "org.apache.catalina.deploy.ContextEjb");
    digester.addSetNext(prefix + "web-app/ejb-ref",
                        "addEjb",
                        "org.apache.catalina.deploy.ContextEjb");

    digester.addCallMethod(prefix + "web-app/ejb-ref/description",
                           "setDescription", 0);
    digester.addCallMethod(prefix + "web-app/ejb-ref/ejb-link",
                           "setLink", 0);
    digester.addCallMethod(prefix + "web-app/ejb-ref/ejb-ref-name",
                           "setName", 0);
    digester.addCallMethod(prefix + "web-app/ejb-ref/ejb-ref-type",
                           "setType", 0);
    digester.addCallMethod(prefix + "web-app/ejb-ref/home",
                           "setHome", 0);
    digester.addCallMethod(prefix + "web-app/ejb-ref/remote",
                           "setRemote", 0);

    digester.addObjectCreate(prefix + "web-app/env-entry",
                             "org.apache.catalina.deploy.ContextEnvironment");
    digester.addSetNext(prefix + "web-app/env-entry",
                        "addEnvironment",
                        "org.apache.catalina.deploy.ContextEnvironment");

    digester.addCallMethod(prefix + "web-app/env-entry/description",
                           "setDescription", 0);
    digester.addCallMethod(prefix + "web-app/env-entry/env-entry-name",
                           "setName", 0);
    digester.addCallMethod(prefix + "web-app/env-entry/env-entry-type",
                           "setType", 0);
    digester.addCallMethod(prefix + "web-app/env-entry/env-entry-value",
                           "setValue", 0);

    digester.addObjectCreate(prefix + "web-app/error-page",
                             "org.apache.catalina.deploy.ErrorPage");
    digester.addSetNext(prefix + "web-app/error-page",
                        "addErrorPage",
                        "org.apache.catalina.deploy.ErrorPage");

    digester.addCallMethod(prefix + "web-app/error-page/error-code",
                           "setErrorCode", 0);
    digester.addCallMethod(prefix + "web-app/error-page/exception-type",
                           "setExceptionType", 0);
    digester.addCallMethod(prefix + "web-app/error-page/location",
                           "setLocation", 0);

    digester.addObjectCreate(prefix + "web-app/filter",
                             "org.apache.catalina.deploy.FilterDef");
    digester.addSetNext(prefix + "web-app/filter",
                        "addFilterDef",
                        "org.apache.catalina.deploy.FilterDef");

    digester.addCallMethod(prefix + "web-app/filter/description",
                           "setDescription", 0);
    digester.addCallMethod(prefix + "web-app/filter/display-name",
                           "setDisplayName", 0);
    digester.addCallMethod(prefix + "web-app/filter/filter-class",
                           "setFilterClass", 0);
    digester.addCallMethod(prefix + "web-app/filter/filter-name",
                           "setFilterName", 0);
    digester.addCallMethod(prefix + "web-app/filter/large-icon",
                           "setLargeIcon", 0);
    digester.addCallMethod(prefix + "web-app/filter/small-icon",
                           "setSmallIcon", 0);

    digester.addCallMethod(prefix + "web-app/filter/init-param",
                           "addInitParameter", 2);
    digester.addCallParam(prefix + "web-app/filter/init-param/param-name",
                          0);
    digester.addCallParam(prefix + "web-app/filter/init-param/param-value",
                          1);

    digester.addObjectCreate(prefix + "web-app/filter-mapping",
                             "org.apache.catalina.deploy.FilterMap");
    digester.addSetNext(prefix + "web-app/filter-mapping",
                        "addFilterMap",
                        "org.apache.catalina.deploy.FilterMap");

    digester.addCallMethod(prefix + "web-app/filter-mapping/filter-name",
                           "setFilterName", 0);
    digester.addCallMethod(prefix + "web-app/filter-mapping/servlet-name",
                           "setServletName", 0);
    digester.addCallMethod(prefix + "web-app/filter-mapping/url-pattern",
                           "setURLPattern", 0);

    digester.addCallMethod(prefix + "web-app/listener/listener-class",
                           "addApplicationListener", 0);

    digester.addObjectCreate(prefix + "web-app/login-config",
                             "org.apache.catalina.deploy.LoginConfig");
    digester.addSetNext(prefix + "web-app/login-config",
                        "setLoginConfig",
                        "org.apache.catalina.deploy.LoginConfig");

    digester.addCallMethod(prefix + "web-app/login-config/auth-method",
                           "setAuthMethod", 0);
    digester.addCallMethod(prefix + "web-app/login-config/realm-name",
                           "setRealmName", 0);
    digester.addCallMethod(prefix + "web-app/login-config/form-login-config/form-error-page",
                           "setErrorPage", 0);
    digester.addCallMethod(prefix + "web-app/login-config/form-login-config/form-login-page",
                           "setLoginPage", 0);

    digester.addCallMethod(prefix + "web-app/mime-mapping",
                           "addMimeMapping", 2);
    digester.addCallParam(prefix + "web-app/mime-mapping/extension", 0);
    digester.addCallParam(prefix + "web-app/mime-mapping/mime-type", 1);

    digester.addCallMethod(prefix + "web-app/resource-env-ref",
                           "addResourceEnvRef", 2);
    digester.addCallParam(prefix + "web-app/resource-env-ref/resource-env-ref-name", 0);
    digester.addCallParam(prefix + "web-app/resource-env-ref/resource-env-ref-type", 1);

    digester.addObjectCreate(prefix + "web-app/resource-ref",
                             "org.apache.catalina.deploy.ContextResource");
    digester.addSetNext(prefix + "web-app/resource-ref",
                        "addResource",
                        "org.apache.catalina.deploy.ContextResource");

    digester.addCallMethod(prefix + "web-app/resource-ref/description",
                           "setDescription", 0);
    digester.addCallMethod(prefix + "web-app/resource-ref/res-auth",
                           "setAuth", 0);
    digester.addCallMethod(prefix + "web-app/resource-ref/res-ref-name",
                           "setName", 0);
    digester.addCallMethod(prefix + "web-app/resource-ref/res-sharing-scope",
                           "setScope", 0);
    digester.addCallMethod(prefix + "web-app/resource-ref/res-type",
                           "setType", 0);

    digester.addObjectCreate(prefix + "web-app/security-constraint",
                             "org.apache.catalina.deploy.SecurityConstraint");
    digester.addSetNext(prefix + "web-app/security-constraint",
                        "addConstraint",
                        "org.apache.catalina.deploy.SecurityConstraint");

    digester.addRule(prefix + "web-app/security-constraint/auth-constraint",
                     new SetAuthConstraintRule(digester));
    digester.addCallMethod(prefix + "web-app/security-constraint/auth-constraint/role-name",
                           "addAuthRole", 0);
    digester.addCallMethod(prefix + "web-app/security-constraint/display-name",
                           "setDisplayName", 0);
    digester.addCallMethod(prefix + "web-app/security-constraint/user-data-constraint/transport-guarantee",
                           "setUserConstraint", 0);

    digester.addObjectCreate(prefix + "web-app/security-constraint/web-resource-collection",
                             "org.apache.catalina.deploy.SecurityCollection");
    digester.addSetNext(prefix + "web-app/security-constraint/web-resource-collection",
                        "addCollection",
                        "org.apache.catalina.deploy.SecurityCollection");
    digester.addCallMethod(prefix + "web-app/security-constraint/web-resource-collection/http-method",
                           "addMethod", 0);
    digester.addCallMethod(prefix + "web-app/security-constraint/web-resource-collection/url-pattern",
                           "addPattern", 0);
    digester.addCallMethod(prefix + "web-app/security-constraint/web-resource-collection/web-resource-name",
                           "setName", 0);

    digester.addCallMethod(prefix + "web-app/security-role/role-name",
                           "addSecurityRole", 0);

    digester.addRule(prefix + "web-app/servlet",
                     new WrapperCreateRule(digester));
    digester.addSetNext(prefix + "web-app/servlet",
                        "addChild",
                        "org.apache.catalina.Container");

    digester.addCallMethod(prefix + "web-app/servlet/init-param",
                           "addInitParameter", 2);
    digester.addCallParam(prefix + "web-app/servlet/init-param/param-name",
                          0);
    digester.addCallParam(prefix + "web-app/servlet/init-param/param-value",
                          1);

    digester.addCallMethod(prefix + "web-app/servlet/jsp-file",
                           "setJspFile", 0);
    digester.addCallMethod(prefix + "web-app/servlet/load-on-startup",
                           "setLoadOnStartupString", 0);
    digester.addCallMethod(prefix + "web-app/servlet/run-as/role-name",
                           "setRunAs", 0);

    digester.addCallMethod(prefix + "web-app/servlet/security-role-ref",
                           "addSecurityReference", 2);
    digester.addCallParam(prefix + "web-app/servlet/security-role-ref/role-link", 1);
    digester.addCallParam(prefix + "web-app/servlet/security-role-ref/role-name", 0);

    digester.addCallMethod(prefix + "web-app/servlet/servlet-class",
                          "setServletClass", 0);
    digester.addCallMethod(prefix + "web-app/servlet/servlet-name",
                          "setName", 0);

    digester.addCallMethod(prefix + "web-app/servlet-mapping",
                           "addServletMapping", 2);
    digester.addCallParam(prefix + "web-app/servlet-mapping/servlet-name", 1);
    digester.addCallParam(prefix + "web-app/servlet-mapping/url-pattern", 0);

    digester.addCallMethod(prefix + "web-app/session-config/session-timeout",
                           "setSessionTimeout", 1,
                           new Class[] { Integer.TYPE });
    digester.addCallParam(prefix + "web-app/session-config/session-timeout", 0);

    digester.addCallMethod(prefix + "web-app/taglib",
                           "addTaglib", 2);
    digester.addCallParam(prefix + "web-app/taglib/taglib-location", 1);
    digester.addCallParam(prefix + "web-app/taglib/taglib-uri", 0);

    digester.addCallMethod(prefix + "web-app/welcome-file-list/welcome-file",
                           "addWelcomeFile", 0);

}
```

---
layout: post
title: "How Tomcat Works - Chapter 15 - Digester"
date: 2017-12-01 20:57:01
author: "Wei SHEN"
categories: ["web","java","how tomcat works"]
tags: ["digester","config"]
description: >
---

### Digester库是干什么的？
Digester库可以根据一系列规则解析XML配置文件。比如下面这个例子，Digester库解析以后可以像这个xml文件所描述的结构：创建出`Employee`类实例，然后其内部包含两个`Office`类实例，然后这些实例包含定义好的的属性作为成员字段。
```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<employee firstName="Freddie" lastName="Mercury">
  <office description="Headquarters">
    <address streetName="Wellington Avenue" streetNumber="223"/>
  </office>
  <office description="Client site">
    <address streetName="Downing Street" streetNumber="10"/>
  </office>
</employee>
```

### Digester是怎么解析XML文件的？
还是看上面这个例子。上面例子里会用到3个类的实例，分别是：`Employee`,`Office`和`Address`。
他们除了有各自的属性，互相还有包含的关系，一个`Employee`对象内部可以有多个`Office`对象作为属性，而`Office`对象又以`Address`对象作为属性。这和上面的XML文件时一致的。并且类中定义了响应的添加和读取这些属性的方法。后面解析的时候会用到这些方法。
```java
public class Employee {
  private String firstName;
  private String lastName;
  private ArrayList offices = new ArrayList();

  public Employee() {
    System.out.println("Creating Employee");
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    System.out.println("Setting firstName : " + firstName);
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    System.out.println("Setting lastName : " + lastName);
    this.lastName = lastName;
  }
  public void addOffice(Office office) {
    System.out.println("Adding Office to this employee");
    offices.add(office);
  }
  public ArrayList getOffices() {
    return offices;
  }
  public void printName() {
    System.out.println("My name is " + firstName + " " + lastName);
  }
}
```

```java
public class Office {
  private Address address;
  private String description;
  public Office() {
    System.out.println("..Creating Office");
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    System.out.println("..Setting office description : " + description);
    this.description = description;
  }
  public Address getAddress() {
    return address;
  }
  public void setAddress(Address address) {
    System.out.println("..Setting office address : " + address);
    this.address = address;
  }
}
```

```java
public class Address {
  private String streetName;
  private String streetNumber;
  public Address() {
    System.out.println("....Creating Address");
  }
  public String getStreetName() {
    return streetName;
  }
  public void setStreetName(String streetName) {
    System.out.println("....Setting streetName : " + streetName);
    this.streetName = streetName;
  }
  public String getStreetNumber() {
    return streetNumber;
  }
  public void setStreetNumber(String streetNumber) {
    System.out.println("....Setting streetNumber : " + streetNumber);
    this.streetNumber = streetNumber;
  }
  public String toString() {
    return "...." + streetNumber + " " + streetName;
  }
}
```
#### `addObjectCreate()`函数创建对象
```java
digester.addObjectCreate("employee","com.ciaoshen.howtomcatworks.ex15.digestertest.Employee");
```
上面这行代码定义了一条“规则”（后面会具体说什么是规则），这条规则规定了：
> 当遍历解析XML文件的时候，如果遇到了<employee>标签（一个顶级标签），就会调用`Employee`类的构造函数，创建一个实例。

这里XML的标签是有层级的，`<employee>`表示一个顶级标签，`<employee/office>`表示一个在`<employee>`标签管辖内的`<office>`标签。

#### `addSetProperties()`函数设置对象属性
比如，`<employee>`标签可以有`firstName`和`lastName`两个属性，
```xml
<employee firstName="Freddie" lastName="Mercury">
    // ... ...
</employee>
```

针对上面这个XML文件，下面这行代码会调用`Employee#setFirstName()`和`Employee#setLastName()`两个函数，然后分别把`Freddie`和`Mercury`两个名字设置为这两个属性的值。
```java
digester.addSetProperties("employee");
```

注意这里面耍的“小聪明”就是，调用的方法名字必须是：
> set + 属性名

属性名为`firstName`，`Employee`类的成员字段也必须叫`firstName`，设置这个属性的方法名必须叫`setFirstName()`。

#### `addCallMethod()`函数调用方法
```java
digester.addCallMethod("employee","printName");
```
上面这行代码，就可以在解析器遇到`<employee>`标签的时候，调用`Employee#printName()`函数。这个函数是没有参数的。如果有参数则可以用`addCallMethod()`的另一个重载版本，
```java
digester.addCallMethod("employee","setFirstName",1);
```
上面这行代码，过一会儿会调用`Employee#setFirstName(String)`函数。调用之前，需要先找到一个参数，找参数用`addCallParam()`函数，
```java
digester.addCallParam("employee/firstName",0);
```
这时候如果XML文件像下面这个样子，Digester库就会创建一个`String`参数`Freddie`，然后传递给`setFirstName(String)`函数做参数。
```xml
<employee>
    <firstName>Freddie</firstName>
</employee>
```

#### `addSetNext()`函数设置对象关系
像我们的例子里，`Office`对象作为`Employee`对象的一个内部属性。`Employee`对象内部有一个`ArrayList`用于储存`Office`对象，然后`addOffice()`函数可以将一个`Office`对象插入这个队列尾部。

这时候下面这行代码，就可以做这件事，在解析到`<office>`次级标签的时候，调用`Employee#addOffice(Office)`函数，把之前创建的`Office`对象作为参数，传递给`addOffice()`函数。这样就可以建立`Employee`对象和`Office`对象间的关系了。
```java
digester.addSetNext("employee/office","addOffice");
```

这里Digester是怎么确定是哪两个`Employee`对象和`Office`对象之间建立联系呢？这里的内部机制是：
> Digester内部维护着一个Stack容器。

当XML文件像下面这个结构的时候，创建完`Employee`对象，会将这个对象压入Stack。创建完`Office`对象的时候，也会将这个对象压入Stack。
```xml
<employee firstName="Freddie" lastName="Mercury">
  <office description="Headquarters">
    <address streetName="Wellington Avenue" streetNumber="223"/>
  </office>
</employee>
```

调用`addSetNext("employee/office","addOffice")`函数的时候，会调用Stack中第二个对象（也就是先压入的元素`Employee`对象）的`addOffice()`函数，然后把Stack中第一个对象（也就是后压入的元素`Office`对象）作为参数传进去。

一个比较重要的点书上没有讲，当执行完所有和`<office>`标签相关的规则之后，遇到`</office>`结束标签的时候，这个`Office`对象会从Stack中弹出。从而保证当一个`Employee`有多个`Office`的时候，`addSetNext()`机制还能正确地工作。

### Digester-Rule-RuleSet 框架
Digester对象的上述这些方法能正确工作背后的原理是建立在`Digester-Rule-RuleSet框架`上的。

#### `Rule`类
当调用`addObjectCreate()`方法，`addCallMethod()`方法，`addSetNext()`方法或其他方法的时候，都会间接调用`Digester`类的`addRule(String, Rule)`函数。该方法将一个`Rule`对象和它匹配的XML标签模式添加到Digester对象的`rules`集合中。

Rule对象最重要的是`start()`和`end()`方法，分别在遇到起始标签和结束标签的时候调用。

实际工作的时候，我们当然可以直接调用Digester类的`addRule()`函数。

#### `RuleSet`类
要向Digester对象中批量添加Rule对象，还可以调用`addRuleSet()`函数，函数签名如下，
```java
public void addRuleSet(RuleSet ruleSet)
```
参数是一个实现了`org.apache.commons.digester.RuleSet`接口的实例。`RuleSet`接口最重要的是一个`addRuleInstances(Digester)`函数，
```java
/**
    Add the set of Rule instances defined in this RuleSet to the specified Digester instance, associating them with our namespace URI (if any). This method should only be called by a Digester instance.

    Parameters:
    digester Digester instance to which the new Rule instances should be added.
*/
    public void addRuleInstances(Digester digester);
```

这个函数以一个`Digester`对象为参数，实现这个方法的时候，可以把刚才所有的和某个标签相关的对`addObjectCreate()`方法或者`addSetNext()`等方法的调用全部封装进去。然后Digester对象在调用`addRuleInstances()`函数的时候，会把自身引用`this`作为参数传递进去。

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
`createWebDigester()`函数的代码如下，过程中需要用到`DTD`文件，DTD文件是用来定义XML文件结构的，可以用来检查XML文件结构的合法性。Context容器对应的`web.xml`编写起来，当然必须符合某些标准，这个标准就定义在DTD文件里。然后`createWebDigester()`函数能找到这些关键文件。
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

### 3个解析过程的例子
例子来源：org.apache.commons项目官方文档 -> <http://commons.apache.org/proper/commons-digester/commons-digester-1.6/docs/api/org/apache/commons/digester/package-summary.html>

#### 例子1：创建一个树状对象结构
首先先定义`Foo`和`Bar`两个类，
```java
package mypackage;
public class Foo {
  public void addBar(Bar bar);
  public Bar findBar(int id);
  public Iterator getBars();
  public String getName();
  public void setName(String name);
}

public mypackage;
public class Bar {
  public int getId();
  public void setId(int id);
  public String getTitle();
  public void setTitle(String title);
}
```

然后下面是一个最简单的`web.xml`文件片段，定义了一个`Foo`对象内部套嵌两个`Bar`对象，
```xml
<foo name="The Parent">
  <bar id="123" title="The First Child"/>
  <bar id="456" title="The Second Child"/>
</foo>
```

然后，我们创建一个`Digester`对象，并且添加一系列能解析上面这个结构的`Rule`，
```java
/** Digester对象 */
Digester digester = new Digester();
digester.setValidating(false);
/** 添加解析Rule */
digester.addObjectCreate("foo", "mypackage.Foo");
digester.addSetProperties("foo");
digester.addObjectCreate("foo/bar", "mypackage.Bar");
digester.addSetProperties("foo/bar");
digester.addSetNext("foo/bar", "addBar", "mypackage.Bar");
/** 实际解析 */
Foo foo = (Foo) digester.parse();
```

实际解析过程如下：
* When the outermost <foo> element is encountered, create a new instance of mypackage.Foo and push it on to the object stack. At the end of the <foo> element, this object will be popped off of the stack.
* Cause properties of the top object on the stack (i.e. the Foo object that was just created and pushed) to be set based on the values of the attributes of this XML element.
* When a nested <bar> element is encountered, create a new instance of mypackage.Bar and push it on to the object stack. **At the end of the <bar> element, this object will be popped off of the stack (i.e. after the remaining rules matching foo/bar are processed)**.
* Cause properties of the top object on the stack (i.e. the Bar object that was just created and pushed) to be set based on the values of the attributes of this XML element. Note that type conversions are automatically performed (such as String to int for the id property), for all converters registered with the ConvertUtils class from commons-beanutils package.
* Cause the addBar method of the next-to-top element on the object stack (which is why this is called the "set next" rule) to be called, passing the element that is on the top of the stack, which must be of type mypackage.Bar. This is the rule that causes the parent/child relationship to be created.

一个比较重要的点书上没有讲，就是第3条加粗的部分，在为第一个`foo/bar`对象执行完全部相关的Rule之后，这个对象会被从Digester内置Stack上弹出。这样保证了当遇到第二个`foo/bar`对象的时候，第二个`foo/bar`仍然是Stack顶的元素，而`foo`还是Stack栈中第二顺位的元素，这样所有Rule才能正常工作。

#### 例子2：将xml标签中的内容作为参数
假设我们有`ServletBean`这个类的定义，
```java
package com.mycompany;
public class ServletBean {
  public void setServletName(String servletName);
  public void setServletClass(String servletClass);
  public void addInitParam(String name, String value);
}
```

然后`web.xml`文件像下面这个样子，
```xml
<web-app>
  ...
  <servlet>
    <servlet-name>action</servlet-name>
    <servlet-class>org.apache.struts.action.ActionServlet<servlet-class>
    <init-param>
      <param-name>application</param-name>
      <param-value>org.apache.struts.example.ApplicationResources<param-value>
    </init-param>
    <init-param>
      <param-name>config</param-name>
      <param-value>/WEB-INF/struts-config.xml<param-value>
    </init-param>
  </servlet>
  ...
</web-app>
```

然后，为`Digester`添加下面这些Rule，
```java
digester.addObjectCreate("web-app/servlet",
                         "com.mycompany.ServletBean");
digester.addCallMethod("web-app/servlet/servlet-name", "setServletName", 0);
digester.addCallMethod("web-app/servlet/servlet-class",
                       "setServletClass", 0);
digester.addCallMethod("web-app/servlet/init-param",
                       "addInitParam", 2);
digester.addCallParam("web-app/servlet/init-param/param-name", 0);
digester.addCallParam("web-app/servlet/init-param/param-value", 1);
```

根据上面这些Rule，具体的解析过程如下，
* <servlet> - A new com.mycompany.ServletBean object is created, and pushed on to the object stack.
* <servlet-name> - The setServletName() method of the top object on the stack (our ServletBean) is called, passing the body content of this element as a single parameter.
* <servlet-class> - The setServletClass() method of the top object on the stack (our ServletBean) is called, passing **the body content of this element as a single parameter.**
* <init-param> - A call to the addInitParam method of the top object on the stack (our ServletBean) is set up, but it is not called yet. The call will be expecting two String parameters, which must be set up by subsequent call parameter rules.
* <param-name> - **The body content of this element is assigned as the first (zero-relative) argument to the call we are setting up.**
* <param-value> - **The body content of this element is assigned as the second (zero-relative) argument to the call we are setting up.**
* </init-param> - The call to addInitParam() that we have set up is now executed, which will cause a new name-value combination to be recorded in our bean.
* <init-param> - The same set of processing rules are fired again, causing a second call to addInitParam() with the second parameter's name and value.
* </servlet> - The element on the top of the object stack (which should be the ServletBean we pushed earlier) is popped off the object stack.

这里的重点就是当执行`addCallMethod()`和`addCallParam()`函数的时候，xml标签内的信息，会被当成参数(上面过程描述中加粗的部分)。此外`addCallMethod()`函数的第3个参数表示需要多少个参数。

#### 例子3：解析Struts的`struts-config.xml`配置文档
`Digester`库最初就是为了健壮地处理解析Struts的配置文件`struts-config.xml`写的。下面是一个简单的例子。

首先创建一个`Digester`实例，
```java
Digester digester = new Digester();
digester.push(this); // Push controller servlet onto the stack
digester.setValidating(true);
```
然后添加一些Rule，
```java
digester.addObjectCreate("struts-config/global-forwards/forward",
                         forwardClass, "className");
digester.addSetProperties("struts-config/global-forwards/forward");
digester.addSetNext("struts-config/global-forwards/forward",
                    "addForward",
                    "org.apache.struts.action.ActionForward");
digester.addSetProperty
  ("struts-config/global-forwards/forward/set-property",
   "property", "value");
```
然后执行解析，
```java
InputStream input =
  getServletContext().getResourceAsStream(config);
...
try {
    digester.parse(input);
    input.close();
} catch (SAXException e) {
    ... deal with the problem ...
}
```

上面这些规则，解析的过程如下：
* A new object instance is created -- the ActionForward instance that will represent this definition. The Java class name defaults to that specified as an initialization parameter (which we have stored in the String variable forwardClass), but can be overridden by using the "className" attribute (if it is present in the XML element we are currently parsing). The new ActionForward instance is pushed onto the stack.
* The properties of the ActionForward instance (at the top of the stack) are configured based on the attributes of the <forward> element.
* Nested occurrences of the <set-property> element cause calls to additional property setter methods to occur. This is required only if you have provided a custom implementation of the ActionForward class with additional properties that are not included in the DTD.
* The addForward() method of the next-to-top object on the stack (i.e. the controller servlet itself) will be called, passing the object at the top of the stack (i.e. the ActionForward instance) as an argument. This causes the global forward to be registered, and as a result of this it will be remembered even after the stack is popped.
* At the end of the <forward> element, the top element (i.e. the ActionForward instance) will be popped off the stack.


### 附录A - 配置文件`web.xml`样例
标准的`web.xml`文件当然不是随便编写的，所用到的元素必须符合一整套规范，具体规范参见 ->
<https://docs.oracle.com/cd/E24329_01/web.1211/e21049/web_xml.htm#WBAPP502>

#### 一个最简单的`web.xml`
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

#### 一个稍复杂一点的`web.xml`
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

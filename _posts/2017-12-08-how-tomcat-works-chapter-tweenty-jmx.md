---
layout: post
title: "How Tomcat Works - Chapter 20 - JMX"
date: 2017-12-08 22:24:52
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["jmx"]
description: >
---

![jmx-1](/images/how-tomcat-works-chapter-tweenty-jmx/jmx-1.png)

### 摘要
> Server中间层实际实现`javax.management.MBeanServer`接口的是`com.sun.jmx.mbeanserver.JmxMBeanServer`类。其中部分工作由`com.sun.jmx.interceptor.DefaultMBeanServerInterceptor`类代为完成。

> 然后MBean这边，对于StandardMBean我们所说的MBean实例实际是`com.sun.jmx.mbeanserver.StandardMBeanSupport`类实例。然后被管理资源实例被封装为它的一个`resource`字段。

### MBean的本质是一个“反射器”
比如被管理的资源是`Car`类实例，它被封装在一个MBean类实例里。有一个`drive()`函数。如果`MBeanServer`像下面这样调用`invoke()`函数，就可以在运行时动态运行这个`Car`的`drive()`函数。
```java
// mBeanServer实例实现了MBeanServer接口
// objectName是ObjectName类实例，可以唯一标识一个MBean。
// 此前和这个MBean绑定的被管理资源就
mBeanServer.invoke(objectName,"drive",null,null);
```

这听上去好像很简单，只需要一个“代理”就可以实现。没必要弄MBean这么复杂。但实际上MBean的意义在于，
> MBean可以选择被管理对象的哪些功能要暴露出来，哪些方法需要对外隐藏。

所以StandardMBean通过定义一个和被管理类同名MBean接口的形式(比如`Car`类的接口必须叫`CarMBean`)，让MBean只能透过`CarMBean`控制`Car`类，就可以隐藏部分`Car`类的函数。

而ModeldMBean是通过`ModelMBeanInfo`对象描述将要暴露给mBean的构造函数，属性，操作，甚至是监听器。

### StandardMBean
本章第一个应用程序`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.StandardAgent`和`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car`类，以及`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.CarMBean`是如何协作的？

目前查到的调用链如下：
1. 实际利用反射通过类的全具名创建Car实例。最终实际执行反射实例化操作的是MBeanInstantiator类，
    * com.sun.jmx.mbeanserver.JmxMBeanServer#createMBean()
    * com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#createMBean()
    * com.sun.jmx.mbeanserver.MBeanInstantiator#instantiate()
2. 并将`Car`实例封装到一个实现了`DynamicMBean`接口的`StandardMBeanSupport`类实例中，
    * com.sun.jmx.mbeanserver.JmxMBeanServer#createMBean()
    * com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerObject()
3. 实现`DynamicMBean`接口的MBean实例最后在封装到`JmxMBeanServer`类实例中（实际是储存在一个`Repository`类实例的字段中）：
    * com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerDynamicMBean()
    * com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#internal_addObject()
    * com.sun.jmx.mbeanserver.Repository#addMBean()
4. 最终在Repository类中，储存MBean实例的数据结构就是一个HashMap：
```java
    /**
     * A Hashtable is used for storing the different domains For each domain,
     * a hashtable contains the instances with canonical key property list
     * string as key and named object aggregated from given object name and
     * mbean instance as value.
     */
    private final Map<String,Map<String,NamedObject>> domainTb;
```

整个过程中最重要的是第2步，它创建一个实现`DynamicMBean`接口的实例，并把被管理资源`Car`类实例封装进去。这个`DynamicMBean`实例就是我们一直说的MBean。

#### 细说`com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerObject()`函数

不论是我们已经有了一个`Car`类实例，然后调用 `com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerMBean()`方法把`Car`资源实例封装到一个MBean实例中，`registerMBean()`函数拿到两个参数：
1. 一个`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car`类实例
2. 这个`Car`对象的`ObjectName`实例用来唯一标识这个实例

在`registerMBean()`方法中，它调用`DefaultMBeanServerInterceptor#getNewMBeanClassName()`函数原封不动地把`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car`这个全具名和`Car`类资源实例和对应的`ObjectName`传给`DefaultMBeanServerInterceptor#registerObject()`方法。

或者是我们直接调用`com.sun.jmx.mbeanserver.JmxMBeanServer#createMBean()`方法创建`Car`类实例之后直接调用`DefaultMBeanServerInterceptor#registerObject()`方法，最终负责将`Car`资源实例封装进一个MBean实例的都是`registerObject()`函数。我们看看它是怎样工作的。

首先在`registerObject()`函数中，调用`com.sun.jmx.mbeanserver.Introspector
#makeDynamicMBean()`函数，并把`Car`类资源对象作为参数传进去，目的是创建一个实现`javax.management.DynamicMBean`接口的MBean包装类实例。**注意！这个实例才是我们一直说的MBean那个实例！这个实例既不是`Car`也不是`CarMBean`，而是另有其人。**
```java
DynamicMBean mbean = Introspector.makeDynamicMBean(object);
```

而在`makeDynamicMBean()`函数里，先调用`Introspector#getStandardMBeanInterface()`函数，再转调`Introspector#findMBeanInterface()`函数找出`Car`类实现的接口`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.CarMBean`。

之后还会调用`Introspector#implementsMBean()`检验是否符合"Car + MBean"这个规定名字。如果一切正常，会最后调用`com.sun.jmx.mbeanserver.StandardMBeanSupport`类的构造函数，创建`DynamicMBean`实例。
```java
if (c != null)
            return new StandardMBeanSupport(mbean, Util.<Class<Object>>cast(c));
```

然后`StandardMBeanSupport`类继承自抽象基类`com.sun.jmx.mbeanserver.MBeanSupport`，它实现了`javax.management.DynamicMBean`接口，实现了部分骨架代码。

所以最终实际工作的两端是：
> Server中间层实际完成`javax.management.MBeanServer`接口工作的类是`com.sun.jmx.mbeanserver.JmxMBeanServer`。其中部分工作由`com.sun.jmx.interceptor.DefaultMBeanServerInterceptor`类代为完成。然后MBean这边，我们所说的MBean实例实际是`com.sun.jmx.mbeanserver.StandardMBeanSupport`类实例。

然后在`StandardMBeanSupport`类实例中，我们的被管理资源`CarMBean`实例的引用会被赋值给一个叫`resource`的字段。

#### 细读`StandardMBean`官方文档
然后我们再读`StandardMBean`类的饿官方文档，其中介绍了两种让`StandardMBean`封装被管理资源（比如`Car`)以及被管理资源接口（比如`CarMBean`）的方法。

An MBean whose management interface is determined by reflection on a Java interface.

This class brings more flexibility to the notion of Management Interface in the use of Standard MBeans. Straightforward use of the patterns for Standard MBeans described in the JMX Specification means that there is a fixed relationship between the implementation class of an MBean and its management interface (i.e., if the implementation class is `Thing`, the management interface must be `ThingMBean`). This class makes it possible to keep the convenience of specifying the management interface with a Java interface, without requiring that there be any naming relationship between the implementation and interface classes.

By making a DynamicMBean out of an MBean, this class makes it possible to select any interface implemented by the MBean as its management interface, provided that it complies with JMX patterns (i.e., attributes defined by getter/setter etc...).

This class also provides hooks that make it possible to supply custom descriptions and names for the MBeanInfo returned by the DynamicMBean interface.

Using this class, an MBean can be created with any implementation class name "Impl" and with a management interface defined (as for current Standard MBeans) by any interface "Intf", in one of two general ways:
1. Using the public constructor StandardMBean(impl,interface):
```java
     MBeanServer mbs;
     ...
     Impl impl = new Impl(...);
     StandardMBean mbean = new StandardMBean(impl, Intf.class, false);
     mbs.registerMBean(mbean, objectName);
```

2. Subclassing StandardMBean:
```java
     public class Impl extends StandardMBean implements Intf {
        public Impl() {
          super(Intf.class, false);
       }
       // implement methods of Intf
     }

     [...]

     MBeanServer mbs;
     ....
     Impl impl = new Impl();
     mbs.registerMBean(impl, objectName);
```

In either case, the class "Impl" must implement the interface "Intf".

Standard MBeans based on the naming relationship between implementation and interface classes are of course still available.

This class may also be used to construct MXBeans. The usage is exactly the same as for Standard MBeans except that in the examples above, the false parameter to the constructor or super(...) invocation is instead true.

根据前面介绍的`StandardMBeanSupport`类的做法，和第一种方法比较接近。

### 模型MBean通过`ModelMBeanInfo`对象描述暴露接口
ModelMBean本质上和StandardMBean一样，也只暴露被管理对象的一部分接口。只是这个接口不是像`CarMBean`这样直接写出来，而是通过`ModelMBeanInfo`对象来描述。下面代码就是构造ModelMBeanInfo对象的过程，
```java
private ModelMBeanInfo createModelMBeanInfo(ObjectName inMbeanObjectName, String inMbeanName) {
    ModelMBeanInfo mBeanInfo = null;
    ModelMBeanAttributeInfo[] attributes = new ModelMBeanAttributeInfo[1];
    ModelMBeanOperationInfo[] operations = new ModelMBeanOperationInfo[3];
    try {
        attributes[0] = new ModelMBeanAttributeInfo("Color", "java,lang.String", "the color.", true, true, false, null);

        operations[0] = new ModelMBeanOperationInfo("drive", "the drive method", null, "void", MBeanOperationInfo.ACTION, null);

        operations[1] = new ModelMBeanOperationInfo("getColor", "get color attribute", null, "java.lang.String", MBeanOperationInfo.ACTION, null);

        Descriptor setColorDesc = new DescriptorSupport(new String[] { "name=setColor", "descriptorType=operation", "class=" + MANAGED_CLASS_NAME, "role=operation"});

        MBeanParameterInfo[] setColorParams = new MBeanParameterInfo[] { (new MBeanParameterInfo("new color", "java.lang.String", "new Color value") )} ;

        operations[2] = new ModelMBeanOperationInfo("setColor", "set Color attribute", setColorParams, "void", MBeanOperationInfo.ACTION, setColorDesc);

        mBeanInfo = new ModelMBeanInfoSupport(MANAGED_CLASS_NAME, null, attributes, null, operations, null);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return mBeanInfo;
}
```

上面这段代码描述的接口，直接写出来就是，
```java
public interface CarMBean {
    public String getColor();
    public void setColor(String color);
    public void drive();
}
```

但ModelMBeanInfo描述能力稍微比`CarMBean`类强一点，比如说`Car`类型有个域`color`，有个`setColor()`函数用来设置这个函数。在`ModelMBeanInfo`对象中有个`Descriptor`类型对象专门描述这个对应关系。所以要设置`color`属性，`RequiredModelMBean`就能找到`setColor()`这个函数签名，然后通过`invoke()`函数调用被管理对象的`setColor()`函数。

下面代码出自`javax.management.modelmbean.RequiredModelMBean`类的`setAttribute(Attribute)`方法。 它通过从`javax.management.modelmbean.ModelMBeanInfo`类描述的暴露的接口中的设置属性的方法名称（比如：对应`color`属性的`setColor()`函数。这在构造`ModelMBeanInfo`对象的时候，通过`Descriptor`对象定义了。）查到color属性的设置方法是`setColor()`以后，就用反射调用这个方法，
```java
1855            /* run method from operations descriptor */
1856            if (attrSetMethod == null) {    // 比如要设置的参数叫color，拿到的attrSetMethod=setColor
                    // code omitted ...
                    // code omitted...
1876            } else {
                    /** 直接调用invoke()函数，执行被管理对象的setColor()函数 */
1877                setResponse = invoke(attrSetMethod,
1878                                 (new Object[] {attrValue}),
1879                                 (new String[] {attrType}) );
1880            }
```

#### `RequiredModelMBean`是默认实现
可以用`javax.management.ModelMBean`接口来表示模型MBean。在JMX中有一个`javax.management.modelmbean.RequiredModelMBean`类是`ModelMBean`接口的默认实现。
```java
RequiredModelMBean modelMBean = null;
try {
  /** 使用RequiredModelMBean类作为ModelMBean接口的实现 */
  modelMBean = new RequiredModelMBean(mBeanInfo);
}
```

#### 通过`Registry`和`ManagedBean`创建`ModelMBean`
具体见下面的示例，
```java
package com.ciaoshen.howtomcatworks.ex20.modelmbeantest2;

import java.io.InputStream;
import java.net.URL;
import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;

import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;

public class ModelAgent {
  private Registry registry;
  private MBeanServer mBeanServer;

  public ModelAgent() {
    registry = createRegistry();
    try {
      mBeanServer = Registry.getServer();
    }
    catch (Throwable t) {
      t.printStackTrace(System.out);
      System.exit(1);
    }
  }

  public MBeanServer getMBeanServer() {
    return mBeanServer;
  }

  public Registry createRegistry() {
    Registry registry = null;
    try {
      URL url = ModelAgent.class.getResource
        ("Users/Wei/github/HowTomcatWorks/solutions/src/com/ciaoshen/howtomcatworks/ex20/modelmbeantest2/car-mbean-descriptor.xml");
      InputStream stream = url.openStream();
      Registry.loadRegistry(stream);
      stream.close();
      registry = Registry.getRegistry();
    }
    catch (Throwable t) {
      System.out.println(t.toString());
    }
    return (registry);
  }

  public ModelMBean createModelMBean(String mBeanName) throws Exception {
    ManagedBean managed = registry.findManagedBean(mBeanName);
    if (managed == null) {
      System.out.println("ManagedBean null");
      return null;
    }
    ModelMBean mbean = managed.createMBean();
    ObjectName objectName = createObjectName();
    return mbean;
  }

  private ObjectName createObjectName() {
    ObjectName objectName = null;
    String domain = mBeanServer.getDefaultDomain();
    try {
      objectName = new ObjectName(domain + ":type=MyCar");
    }
    catch (MalformedObjectNameException e) {
      e.printStackTrace();
    }
    return objectName;
  }


    public static void main(String[] args) {
        ModelAgent agent = new ModelAgent();
        MBeanServer mBeanServer = agent.getMBeanServer();
        Car car = new Car();
        System.out.println("Creating ObjectName");
        ObjectName objectName = agent.createObjectName();
        try {
          ModelMBean modelMBean = agent.createModelMBean("myMBean");
          modelMBean.setManagedResource(car, "ObjectReference");
          mBeanServer.registerMBean(modelMBean, objectName);
        } catch (Exception e) {
          System.out.println(e.toString());
        }
        // manage the bean
        try {
          Attribute attribute = new Attribute("Color", "green");
          mBeanServer.setAttribute(objectName, attribute);
          String color = (String) mBeanServer.getAttribute(objectName, "Color");
          System.out.println("Color:" + color);

          attribute = new Attribute("Color", "blue");
          mBeanServer.setAttribute(objectName, attribute);
          color = (String) mBeanServer.getAttribute(objectName, "Color");
          System.out.println("Color:" + color);
          mBeanServer.invoke(objectName, "drive", null, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
}
```

#### Commons Modeler 库
编写`ModelMBeanInfo`类非常枯燥。为了偷懒可以先把信息写在XML文件里，然后用Commons Moddeler库解析XML文件。
```java
<mbean name="StandardServer" className="org.apache.catalina.mbeans.StandardServerMBean" description="Standard Server Component" domain="Catalina" group="Server" type="org.apache.catalina.core.StandardServer">
    <attribute name="debug" description="The debugging detail level for this component" type="int"/>
    <attribute name="managedResource" description="The managed resource this MBean is associated with" type="java.lang.Object"/>
    <attribute name="port" description="TCP port for shutdown messages" type="int"/>
    <attribute name="shutdown" description="Shutdown password" type="java.lang.String"/>
    <operation name="store" description="Save current state to server.xml file" impact="ACTION" returnType="void"> </operation>
</mbean>
```

### 应用程序
本章应用程序缺少`MyAdminServlet.java`源文件，需要根据书中P340列出的代码补上，然后置于如下路径，
> $CATALINA_HOME/server/webapps/myadmin/MyAdminServlet.java

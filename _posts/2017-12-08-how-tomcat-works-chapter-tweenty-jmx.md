---
layout: post
title: "How Tomcat Works - Chapter 20 - JMX"
date: 2017-12-08 22:24:52
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["jmx"]
description: >
---

### 摘要
> Server中间层实际实现`javax.management.MBeanServer`接口的是`com.sun.jmx.mbeanserver.JmxMBeanServer`类。其中部分工作由`com.sun.jmx.interceptor.DefaultMBeanServerInterceptor`类代为完成。

> 然后MBean这边，对于StandardMBean我们所说的MBean实例实际是`com.sun.jmx.mbeanserver.StandardMBeanSupport`类实例。然后被管理资源实例被封装为它的一个`resource`字段。

### StandardMBean
本章第一个应用程序`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.StandardAgent`和`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car`类，以及`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.CarMBean`是如何协作的？

目前查到的调用链如下：
1. 实际利用反射通过类的全具名创建Car实例。最终实际执行反射实例化操作的是MBeanInstantiator类，
    * com.sun.jmx.mbeanserver.JmxMBeanServer#createMBean()
    * com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#createMBean()
    * com.sun.jmx.mbeanserver.MBeanInstantiator#instantiate()
2. 并将`Car`实例注册到JmxMBeanServer实例中，注册的过程先要创建一个实现`DynamicMBean`接口的实例，把`Car`实例封装进去。
    * com.sun.jmx.mbeanserver.JmxMBeanServer#createMBean()
    * com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerObject()
3. 实现`DynamicMBean`接口的MBean实例最后被储存在`Repository`类实例中：
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

#### 细说`com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerMBean()`函数
`com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerMBean()`方法拿到两个参数：
1. 一个`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car`类实例
2. 这个`Car`对象的`ObjectName`实例用来唯一标识这个实例

在`registerMBean()`方法中，它调用`DefaultMBeanServerInterceptor#getNewMBeanClassName()`函数原封不动地把`com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car`这个全具名和`Car`类资源实例和对应的`ObjectName`传给`DefaultMBeanServerInterceptor#registerObject()`方法。

然后在`registerObject()`函数中，调用`com.sun.jmx.mbeanserver.Introspector
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

### 模型MBean利用反射工作
比如说`Car`类型有个域`color`，有个`setColor()`函数用来设置这个函数。在`ModelMBeanInfo`对象中有个`Descriptor`类型对象专门描述这个对应关系。所以要设置`color`属性，`RequiredModelMBean`就能找到`setColor()`这个函数签名，然后通过`invoke()`函数调用被管理对象的`setColor()`函数。所以，
> RequiredModelMBean类是一个典型的代理模式

```java
/**
 * 下面代码出自javax.management.modelmbean.RequiredModelMBean类的setAttribute(Attribute)方法。
 * 它通过从javax.management.modelmbean.ModelMBeanInfo类描述的暴露的接口中的设置属性的方法名称（比如：对应color属性的setColor()函数。这在构造ModelMBeanInfo对象的时候，通过Descriptor对象定义了。）
 * 查到color属性的设置方法是setColor()以后，
 */
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

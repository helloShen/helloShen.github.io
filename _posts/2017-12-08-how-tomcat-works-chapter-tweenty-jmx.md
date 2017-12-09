---
layout: post
title: "How Tomcat Works - Chapter 20 - JMX"
date: 2017-12-08 22:24:52
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["jmx"]
description: >
---

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

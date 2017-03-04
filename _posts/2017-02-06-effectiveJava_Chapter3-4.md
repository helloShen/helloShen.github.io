---
layout: post
title: "[Effective Java] Note: - Chapter-3-3: Java toString() Considerations"
date: 2017-02-06
author: "Wei SHEN"
categories: ["Java","Effective_Java"]
tags: ["String","Code_Style"]
description: >
  最好给toString()方法返回的字符串定义一种规范格式，并写好注释文档。并且给toString()方法涉及的每一个成员域都提供一个访问方法，以避免toString()方法的返回值因为被客户端程序员解析使用，而变相成为API的一部分。
---

### 格式化，规范化toString()返回的字符串
`toString()`返回的字符串，**应该符合某种特定的规范格式**，而且 **应该在文档中明确表明意图**。格式化的数据，让客户端程序员可以更容易地解析返回的字符串，以得到某些信息。

```java
public class PhoneNumber {
    // remainder omitted

    /**
     * Returns the string representation of the phone number:
     * The string consists of fourteen characters whose format is:
     *     "(XXX) YYY - ZZZZ"
     * where, XXX is the area code;
     *        YYY is the prefix;
     *        ZZZZ is the line number;
     * The exact details of the representation are unspecified and
     * subject to change.
     */
    public String toString() {
        return "(" + areaCode + ") " + prefix + " - " + lineNumber;
    }
}
```

### 避免让用户依赖于解析toString()的返回的字符串，以得到某些数据
但客户端程序员依赖于解析`toString()`返回的字符串的副作用是：**一旦以后更改这种格式，过去很多依赖解析数据的代码将无法工作**。 这会增加系统维护的难度，因为一旦指定了某种格式，以后就很难更改。

#### 补救办法：提供getter访问方法
因此无论是否指定格式，**都应该为`toString()`返回值中包含的所有信息，提供一种编程式的访问途径。比如说getter访问方法**。

```java
public class PhoneNumber {
    private final short areaCode;
    private final short prefix;
    private final short lineNumber;
    public String toString() {
        return "(" + areaCode + ") " + prefix + " - " + lineNumber;
    }
    /**
     * 提供toString()方法中返回的所有信息的getter访问方法。
     */
    public int getAreaCode() {
        return (int)areaCode;
    }
    public int getPrefix() {
        return (int)prefix;
    }
    public int getLineNumber() {
        return (int)lineNumber;
    }
}
```

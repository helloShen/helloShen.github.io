---
layout: post
title: "About Java SecurityManager"
date: 2017-10-22 21:38:47
author: "Wei SHEN"
categories: ["java"]
tags: ["security manager","permission"]
description: >
---

### 前言
简单介绍Java安全管理器框架。看看`SecurityManager`,`Permission`,`ProtectionDomain`,`CodeSource`,`Policy`这些类是怎么一起工作的。

### 权限`Permission`类是安全管理的核心
权限`Permission`封装了特定权限的详细信息。就是由一组字符串定义的。比如下面的的例子，`FilePermission`的实例定义了一条具体权限：在`/tmp/`目录下的读写任何文件的权限。
```java
FilePermission p = new FilePermission("/tmp/*", "read,write");
```

### Permission是怎么工作的？
`Permission`最重要的就是`implies()`函数。是定义权限规则的地方。它要配合`SecurityManager#checkPermission(Permission)`函数工作。官方的解释如下，
> Checks if the specified permission's actions are "implied by" this object's actions.

比如下面的例子，我定义了一个`WordCheckPermission`，然后`badWordSet()`函数里定义了一组不合法的单词集合。比如说`fuck`,`sex`,`bitch`是不合法的。然后在`implies()`函数的伪代码是：
> 如果传进来的Permission的name字段是这些不合法的单词就返回false。

```java
import java.security.*;
import java.util.*;

/**
 * A permission that checks for bad words.
 * @version 1.00 1999-10-23
 * @author Cay Horstmann
 */
public class WordCheckPermission extends Permission {
    /**
     * Constructs a word check permission
     * @param target a comma separated word list
     * @param anAction "insert" or "avoid"
     */
    public WordCheckPermission(String target, String anAction) {
       super(target);
       action = anAction;
    }

    public String getActions() {
       return action;
    }

    public boolean equals(Object other) {
       if (other == null) return false;
       if (!getClass().equals(other.getClass())) {
         return false;
       }
       WordCheckPermission b = (WordCheckPermission) other;
       if (!action.equals(b.action)) {
         return false;
       }
       if (action.equals("insert")) {
         return getName().equals(b.getName());
       }
       else if (action.equals("avoid")) {
         return badWordSet().equals(b.badWordSet());
       } else {
         return false;
       }
    }

    public int hashCode() {
       return getName().hashCode() + action.hashCode();
    }

    /**
     * Permission关键就是这个implies函数:
     *     它定义了参数传进来的Permission对象是否 “符合” 当前Permission对象定义的权限
     */
    public boolean implies(Permission other) {
       if (!(other instanceof WordCheckPermission)) {
           return false;
       }
       WordCheckPermission b = (WordCheckPermission) other;
       if (action.equals("insert")) {
          return b.action.equals("insert") && getName().indexOf(b.getName()) >= 0;
       } else if (action.equals("avoid")) {
          if (b.action.equals("avoid")) {
              return b.badWordSet().containsAll(badWordSet());
          } else if (b.action.equals("insert")) {
             for (String badWord : badWordSet()) {
                if (b.getName().indexOf(badWord) >= 0) { return false; }
             }
             return true;
          } else {
              return false;
          }
       } else {
           return false;
       }
    }

    /**
     * Gets the bad words that this permission rule describes.
     * @return a set of the bad words
     */
    public Set<String> badWordSet() {
       Set<String> set = new HashSet<String>();
       set.addAll(Arrays.asList(getName().split(",")));
       return set;
    }

    private String action;
}
```

然后我再`append()`函数里，真正执行`super.append()`之前，都执行`checkPermission()`检查，只有通过了才添加这个单词。就可以做到敏感词过滤的作用。
```java
public void append(String text) {
   WordCheckPermission p = new WordCheckPermission(text, "insert");
   SecurityManager manager = System.getSecurityManager();
   if (manager != null) manager.checkPermission(p);  // ！关键的权限检查在这里
   super.append(text);
}
```
一般的使用场景是：每个类都有一个`ProtectionDomain`，其中封装了一系列的`CodeSource`和`Permission`集合。通过这一系列的规则定义某个类的安全规则。
```java
ProtectionDomain(CodeSource codesource, PermissionCollection permissions)
```

### 详细内容，参见下面《Java核心技术》对安全管理器的表述
![1](/images/security-manager/1.png)
![2](/images/security-manager/2.png)
![3](/images/security-manager/3.png)
![4](/images/security-manager/4.png)
![5](/images/security-manager/5.png)
![6](/images/security-manager/6.png)
![7](/images/security-manager/7.png)
![8](/images/security-manager/8.png)
![9](/images/security-manager/9.png)
![10](/images/security-manager/10.png)
![11](/images/security-manager/11.png)
![12](/images/security-manager/12.png)
![13](/images/security-manager/13.png)
![14](/images/security-manager/14.png)
![15](/images/security-manager/15.png)
![16](/images/security-manager/16.png)
![17](/images/security-manager/17.png)
![18](/images/security-manager/18.png)

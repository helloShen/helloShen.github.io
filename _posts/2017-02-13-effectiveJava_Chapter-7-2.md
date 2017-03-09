---
layout: post
title: "[Effective Java] Note: - Chapter-7-2: Keep the Method Safe"
date: 2017-02-13
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["code style"]
description: >
  首先，每个方法一开始就最好检查参数的有效性。第二，必要时要进行保护性拷贝，以保证对象的状态不被改变。第三，宁愿返回空数组或者集合也不要返回null。
---

### 一开始就检查参数有效性
一个很重要的 **迅速失败** 原则是：**应该在发生错误后尽快检出错误**。所以在公有方法的开头就要对参数进行类型检查，并抛出适当的异常。常见的参数相关的异常有：
1. **IllegalArgumentException**
2. **IndexOutOfBoundsException**
3. **NullPointerException**

对待未被导出的非API辅助方法，应该使用 **断言(Assertion)**。断言失败将抛出 **`AssertionError`**。

### 让方法尽可能地通用
尽管可以对参数进行有效性检查，但设计方法时还是应该尽可能地通用。能接受所有的参数。

### 必要时进行保护性拷贝
程序设计的一条重要原则是：**假设客户端程序员会尽可能地破坏类的约束条件**。所以原则上，**对象的内部状态不应该被修改**。换句话说，**所有对象最好都是不可变的**。

#### 保护性拷贝惯用法
1. **对构造器的每个可变参数都进行保护性拷贝**。而且 **先拷贝，再检查有效性**。这是为了防止对象在 **“危险阶段”** 从另一个线程被篡改。这叫 **Time Of Check / Time Of Use** （TOCTOU)攻击。而且 **不要用clone()来拷贝**。因为用户提供的有可能是不受信任的子类。

```java
public Period(Date start, Date end) {
    /**
     * 先拷贝，再类型检查
     */
    this.start = new Date(start.getTime()); // 不要用clone()拷贝
    this.end = new Date(end.getTime()); // 不要用clone()拷贝

    if (this.start.compareTo(this.end) > 0) {
        throw new IllegalArgumentException(start + " after " + end);
    }
}
```

2. **提供内部可变对象域引用的时候，要做保护性拷贝**。这时候可以用`clone()`拷贝了。因为对象内部域持有的对象，是可信的。

```java
public Date getStart() {
    return new Date(start.getTime());   // 现在可以用clone()
}
public Date getEnd() {
    return new Date(end.getTime());   // 现在可以用clone()
}
```

#### 注意
1. 警惕数组，数组总是可变的
2. 尽量用不可变对象做类的成员域

### 宁愿返回空数组或集合，也不要返回null
好处很明显，就是客户端程序员不用针对`null`做类型检查了。

#### 惯用法
返回空数组或者空集合的标准化做法是： **每次都返回同一个空对象**。
```java
final class TestArrayCollection {
    private static final List<Object> LIST = new ArrayList<>();
    private static final Object[] ARRAY = new Object[0];
    public TestArrayCollection(List<? extends Object> list) {
        LIST.addAll(list);
    }
    /**
     * 从集合到数组转换的惯用法
     * List#toArray()方法提供的服务的一个通用约定是：除非集合元素的数量超过参数数组限定的长度，否则返回原数组。
     * 所以对于空集合，每次返回的都是同一个静态域对象ARRAY。
     */
    public static Object[] getAsArray() {
        return LIST.toArray(ARRAY);
    }
    public static List<Object> getAsList() {
        if (LIST.isEmpty()) {
            return Collections.emptyList(); // always return the same list
        }
        return new ArrayList<Object>(LIST);
    }
}
```

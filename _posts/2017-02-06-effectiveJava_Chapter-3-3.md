---
layout: post
title: "[Effective Java] Note: - Chapter-3-3: Comparable Interface"
date: 2017-02-06
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["comparable"]
description: >
  Comparable接口和equals()方法的约定很像，一样要老老实实逐域比较。
---

### Comparable接口的通用约定
`Comparable`接口的通用约定和`equals()`方法的很像，
1. 对称性：如果`x.compareTo(y)>0`，那么`y.compareTo(y)<0`。
2. 传递性：如果`x.compareTo(y)>0`，且`y.compareTo(z)>0`，那么必须`x.compareTo(z)>0`。
3. 和`equals()`保持一致性： 如果`x.equals(y)`，那么`x.compareTo(y)=0`最好成立。

对于第三条，不是强制要求，但如果违背的话，必须要仔细考虑可能带来的后果，比如`BigDecimal`类的`compareTo()`和`equals()`方法的结果不一致。如果像下面这样往`HashSet`里插入两个`BigDecimal`。`HashSet`用`equals()`方法判断`1.0`和`1.00`不相等。所以`HashSet`里有两个元素。
```java
// set里会有2个元素。因为HashSet用equals()方法判断元素是否重复。
Set<BigDecimal> hashset = new HashSet<>();
hashset.add(new BigDecimal("1.0"));
hashset.add(new BigDecimal("1.00"));
```
但如果往`TreeSet`里插入两个`BigDecimal`。`TreeSet`用`compareTo()`方法判断`1.0`和`1.00`相等。所以`HashSet`里只有一个元素。
```java
// set里会有2个元素。因为HashSet用equals()方法判断元素是否重复。
Set<BigDecimal> treeset = new TreeSet<>();
treeset.add(new BigDecimal("1.0"));
treeset.add(new BigDecimal("1.00"));
```

### 编写合格的compareTo()方法
诀窍和编写`equals()`方法差不多，
1. 从最关键的域开始逐域比较。
2. 比较的时候：
    1. 对除了`float`和`double`外的基本型：用`>`和`<`比较。
    2. 对`float`和`double`：用`Float.compare()`和`Double.compare()`。
    3. 对其他对象，递归调用他们的`compareTo()`方法。如果没有，就传递一个`Comparator`，然后用`compare()`方法。

下面还是`PhoneNumber`的例子，展示了一个朴素但实用的`compareTo()`方法的实现，
```java
package com.ciaoshen.effectivejava.chapter3;

public class TestComparable {
    public class PhoneNumber implements Comparable<PhoneNumber> { // 实现了和自身类比较的Comparable接口
        private static final int MAX_AREACODE = 999;
        private static final int MAX_PREFIX = 999;
        private static final int MAX_LINENUMBER = 9999;
        private final short areaCode;
        private final short prefix;
        private final short lineNumber;
        private volatile int hashCode;

        public PhoneNumber(int areaCode, int prefix, int lineNumber) {
            rangeCheck(areaCode, MAX_AREACODE, "area code");
            rangeCheck(prefix, MAX_PREFIX, "prefix");
            rangeCheck(lineNumber, MAX_LINENUMBER, "line number");
            this.areaCode = (short)areaCode;
            this.prefix = (short)prefix;
            this.lineNumber = (short)lineNumber;
        }
        public String toString() {
            return "(" + areaCode + ") " + prefix + " - " + lineNumber;
        }
        private void rangeCheck(int num, int max, String name) {
            if (num < 0 || num > max) {
                throw new IllegalArgumentException(name + ": " + num);
            }
        }
        @Override
        public boolean equals(Object o) {
            if (o == this) { return true; } // 等价性检查。相等直接返回，不比较。
            if (! (o instanceof PhoneNumber) ) { return false; } // 不是PhoneNumber的直接返回false
            PhoneNumber pn = (PhoneNumber)o; // 转型必须成功,前面已经检查过了。
            return pn.areaCode == areaCode && pn.prefix == prefix && pn.lineNumber == lineNumber; // 逐个域进行比较。
        }
        @Override
        public int hashCode() {
            // return super.hashCode(); // hashCode from Object violates the general contract of hashCode
            int result = hashCode;
            if (result == 0) {
                int result = 17;
                result = 31 * result + areaCode;
                result = 31 * result + prefix;
                result = 31 * result + lineNumber;
                hashCode = result;
            }
            return hashCode;
        }
        public int compareTo(PhoneNumber pn) { // 朴素但有效。
            if (lineNumber > pn.lineNumber) {
                return 1;
            }
            if (lineNumber < pn.lineNumber) {
                return -1;
            }
            if (prefix > pn.prefix) {
                return 1;
            }
            if (prefix < pn.prefix) {
                return -1;
            }
            if (areaCode > pn.areaCode) {
                return 1;
            }
            if (areaCode < pn.areaCode) {
                return -1;
            }
            if (areaCode == pn.areaCode) {
                return 0;
            }
        }
    }
}
```

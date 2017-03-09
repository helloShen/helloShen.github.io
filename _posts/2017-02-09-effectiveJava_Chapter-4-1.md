---
layout: post
title: "[Effective Java] Note: - Chapter-4-1: Minimize the accessibility of classes and their members"
date: 2017-02-09
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["access control"]
description: >
  记住，让每个类都尽可能的自私。让每个类的成员都只为这个类工作。让每个类只为所在包工作。只在有明确理由的情况下，才提升某些类或成员的可访问等级。最后谨慎选择API。因为一旦公开你的API，就很难再改，它必须永远得到支持。
---

### 基本原则：可访问性最小化
这是一个大原则：
> **每个类和类的成员都不应被外界访问。除非我有明确的理由将它公开。**


### Java访问性的基本概念
1. **顶层（非套嵌）类或接口**：
    1. **default** (缺省值): 包级私有（package-private）
    2. **public** : 公有
2. **次级包括：成员域，成员方法，套嵌类和套嵌接口**：
    1. **private(私有)**: 只有在声明该成员的顶层类内部才可以访问这个成员。
    2. **default(缺省，包级私有)**: 声明该成员的包内部的任何类都可以访问这个成员。是缺省（default）的访问级别。
    3. **protected(受保护的)**：声明该成员的类的子类可以访问这个这个成员，并且声明该成员的包内部的任何类也可以访问这个成员。
    4. **public(公有)**：在任何地方都可以访问该成员。

> **导出的API**: 是指使用库的程序员可以使用的 **类，接口，构造器，成员域或成员方法，以及序列化形式**。

### 访问性设计的良好实践
以下的每一条都极为重要：

> **大部分的次级成员都保持私有。只有少数明确需要为包其他成员提供某种服务的，才去掉private，提高到包级私有访问权限。**

> **顶级类和接口先全部设计成包级私有。除非这个类或接口，或者它的某成员已经被决定为API的一部分。**

> **成为public公有，和成为protected受保护的，都已经是API的一部分。**

> **让任何一部分代码成为API，都需要极为慎重！**

> **因为成为API的那一刻起，意味着在将来，很难对它修改，替换，删除。因为它进入了客户端程序员的代码，就永远必须得到支持。**

> **实例域决不能是公有的。除非它是专门用来暴露常量的静态不可变域。暴露了实例域将导致未来永远无法改变它。解决的办法是提供getter和setter访问方法。**

> **不可变性是能否成为API的一个基本条件。因为暴露不可变域的危害稍微小一些。至少不会因为域值被改变而导致系统崩溃。**

> **警惕数组！警惕普通容器！就算打上final修饰符的数组和普通容器仍然是可变的。解决方案是Collections.unmodifableCollection家族。**

> **警惕Serializable接口！因为它可能导致域被泄露到API中。**

### getter和setter访问方法
如果必须提供某些域的公有访问，那就用 **私有域和公有访问方法替代**。一下代码是简单的演示，

```java
class Point {
    private double x;
    private double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
```

### 不可变性也是可访问性的一部分
就算我们尽可能地缩小类和成员的可访问性，最终API中还是必须向用户暴露很多东西。**不可变性对象可以安全地被分享**。这点很重要。如果我们暴露的东西不能被改变，那我们系统的状态就可以始终保持稳定，而且就算在并发场景下，也可以放心地被共享给多个线程。

#### 所有类都应该设计成不可变的，除非有明确的原因
不可变对象就是实例不能被修改的类。如果我们API暴露的所有实例都不能被修改。因此它们不容易出错，更加安全。

不可变对象另一个优势就是并发场景。因为不可变，因此 **可以被自由地共享**。而且 **永远不需要保护性拷贝**。因为拷贝出来的也时钟等于原始的对象。因此不可变类应该像`String`那样，尽量地控制现有实例。

不可变对象的缺点是每个不同的值，都需要一个单独的对象。书里举了一个有100万位的`BigInteger`的例子。就为了修改其中的一位，都会返回一个全新的对象。效率大受影响。

解决的办法是可以提供一个配套类。比如`StringBuilder`之于`String`。所有的装配工作都在可变的`StringBuilder`上完成，最后再用`toString()`方法生成一个不可变的`String`实例。这个实例一旦产生就再也无法修改。

#### 怎么使类成为不可变类
1. 不提供任何改变对象状态的方法（mutator）。就算必须要有，也可以返回一个全新的对象。
2. 所有域都是final。
3. 保证客户端无法获得其中可变对象域的引用。
4. 保证类不会被扩展。
    1. 可以将整个类加上final修饰符。
    2. 也可以将构造器设为private。仅提供静态工厂方法，生产实例。
5. 所有域都为private。

第一条和第二条，完全不许修改任何域，有点过于严格。真正的底线是：**可以有可以修改的域，和修改的方法，但不能暴露给用户**。比如，类为了省一点计算量可以缓存第一次计算`hashCode()`的结果。但这个缓存域必须对用户不可见。而且`hashCode()`方法的返回值也保持返回同一个结果，**让用户无法察觉**。

最后提一点，就算某个类实在不能做成不可变的，也应该尽量保持每个域的`private final`的属性。而且也绝对不要给每个getter访问方法，配套一个setter修改方法。

### 使一个类可访问性最小化的练习
下面代码是一个只为`com.ciaoshen.effectivejava.chapter4`包提供标准`Map<K,V>`接口服务的类`PackagePrivateLinkedMap`。

其内部所有用`private`关键词修饰的成员都是不应该公开成`API`部分。用`public`关键词修饰的域都是设计时就考虑可以向用户公开的部分。但目前因为整个类是`包级私有`，所以这些`public`成员实际上还没有成为`API`的一部分。一旦整个类加上`public`关键字，这些成员就被公开成了`API`承诺的服务。
```java
package com.ciaoshen.effectivejava.chapter4;
import java.util.*;

/**
 * 目前PackagePrivateLinkedMap的访问级别是包级私有。只为com.ciaoshen.effectivejava.chapter4包提供标准Map接口服务
 * 内部成员的访问级别是：可以向用户暴露标准Map接口API。其他实现细节都为private。
 * 只需要将整个类设为public，即可暴露Map接口API。
 */
class PackagePrivateLinkedMap<K,V> extends AbstractMap<K,V> {
     // 不可以直接面向标准Set<Map.Entry<K,V>>接口工作
     // 因为需要额外的面向Node型的put()方法
     private LinkedSet nodeSet = linkedset();
     private PackagePrivateLinkedMap() {} // 私有化构造器
     // 公有静态工厂方法向用户提供的是Map<K,V>标准接口。
     // 用户只需知道PackagePrivateLinkedMap提供Map<K,V>接口定义的标准服务，不需要知道内部实现细节。
     public static <K,V> Map<K,V> newInstance() {
         return new PackagePrivateLinkedMap<K,V>();
     }
     public Set<Map.Entry<K,V>> entrySet() {
         return nodeSet;
     }
     public V put(K k,V v) {
         return nodeSet.put(Node.newInstance(k,v));
     }
     // 私有内部套嵌类。链表的节点元素。内部套嵌类无法访问外部类的所有私有成员。
     private static class Node<K,V> implements Map.Entry<K,V> {
         private K key;
         private V value;
         private Node<K,V> next;
         private Node() {} // 私有化构造器
         private Node(K k, V v) {
             key = k;
             value = v;
             next = null;
         }
         // 公有静态工厂方法不能只返回Map.Entry<K,V>接口的引用。
         // 因为Map.Entry<K,V>接口功能太弱，不支持自包含链表操作。
         // LinkedSet必须直接面向Node<K,V>实例。
         public static <K,V> Node<K,V> newInstance() {
             return new Node<K,V>();
         }
         public static <K,V> Node<K,V> newInstance(K k, V v) {
             return new Node<K,V>(k,v);
         }
         public K getKey() {
             return key;
         }
         public V getValue() {
             return value;
         }
         public V setValue(V v) {
             V oldValue = value;
             value = v;
             return oldValue;
         }
         public boolean equals(Object o) {
             if (o == this) { return true; }
             if ( ! ( o instanceof Node ) ) {
                 return false;
             }
             @SuppressWarnings({"rawtypes","unchecked"})
             Node<K,V> n = (Node)o;
             return n.key.equals(key);
         }
         public int hashCode() {
             return 31 * 17 + key.hashCode();
         }
         public String toString() {
             return "N[" + key + "," + value + "]";
         }
     }
     // 由PackagePrivateLinkedMap提供LinkedSet的静态工厂方法。
     // 不需要公开为API的一部分
     // 必须返回LinkedSet。因为标准Set<Map.Entry<K,V>>接口不提供put()方法。
     private LinkedSet linkedset() { return new LinkedSet(); }
     // 私有内部类。Node组成的单向链表。
     private class LinkedSet extends AbstractSet<Map.Entry<K,V>> {
         private Node<K,V> head;
         private int size;
         private LinkedSet() { // 内部类不能有静态方法，不能提供静态工厂方法
             head = Node.newInstance();
         }
         public int size() { return size; }
         public Iterator<Map.Entry<K,V>> iterator() {
             return new Iterator<Map.Entry<K,V>>() {
                 private Node<K,V> cursor = head;
                 private Node<K,V> previous = cursor;
                 public boolean hasNext() {
                     return cursor.next != null;
                 }
                 public Map.Entry<K,V> next() {
                     if (cursor.next == null) {
                         throw new NoSuchElementException();
                     }
                     Node<K,V> next = cursor.next;
                     previous = cursor;
                     cursor = next;
                     return next;
                 }
                 public void remove() {
                     if (cursor == previous) {
                         throw new IllegalStateException();
                     }
                     previous.next = cursor.next;
                     cursor.next = null;
                     cursor = previous;
                     size--;
                 }
             };
         }
         public boolean add(Map.Entry<K,V> node) {
             throw new UnsupportedOperationException("add() method of Set interface is too weak!");
         }
         public boolean addAll(Collection<? extends Map.Entry<K,V>> c) {
             throw new UnsupportedOperationException("add() method of Set interface is too weak!");
         }
         // 不是Set接口的一部分，私有化防止被外部访问
         private V put(Node<K,V> node) {
             V result = null;
             Iterator<Map.Entry<K,V>> ite = iterator();
             while (ite.hasNext()) {
                 Map.Entry<K,V> entry = ite.next();
                 if (node.equals(entry)) {
                     ite.remove();
                     result = entry.getValue();
                 }
             }
             node.next = head.next;
             head.next = node;
             size++;
             return result;
         }
     }
     public static void main(String[] args) {
         int size = 10;
         MapTester.newInstance(PackagePrivateLinkedMap.newInstance()).test(size);
     }
 }
 ```

下面这个`MapTester`是作为自己内部测试框架的一部分，所以只能是`包级私有`。永远不应该被公开。
```java
package com.ciaoshen.effectivejava.chapter4;
import java.util.*;

/**
 * 包级私有。仅供包内测试使用。不能公开成API。
 * 包内可以看到的接口是：
 *     1. newInstance(Map<Integer,Character>)： 静态工厂方法
 *     2. test(int)： 用于实际测试
 */
class MapTester {
    private static final int MAX = 128;
    private static final int MIN = 32;
    private static final Random R = new Random();
    private Map<Integer,Character> asciiMap;
    private MapTester(Map<Integer,Character> map) {
        asciiMap = map;
    }
    public static MapTester newInstance(Map<Integer,Character> map) {
        return new MapTester(map);
    }
    public static Map<Integer,Character> fillAsciiMap(Map<Integer,Character> map, int size) {
        for (int i = 0; i < size; i++) {
            int index = R.nextInt(MAX-MIN) + MIN;
            map.put(index,(char)index);
        }
        return map;
    }
    public void test(int size) {
        asciiMap = fillAsciiMap(asciiMap,size);
        System.out.println(asciiMap);
        loopAsciiRange:
        for (int i = 0; i < MAX; i++) { // remove the smallest element
            if (asciiMap.containsKey(i)) {
                asciiMap.remove(i);
                System.out.println("Entry[" + i + "," + (char)i + "] is removed!");
                break loopAsciiRange;
            }
        }
        System.out.println(asciiMap);
    }
}
```

### 将一个类修改成不可变的练习
下面这个`ImmutableLinkedMap`仍然是可变的。它返回一个符合标准`Map`接口的引用。虽然它禁用了所有能够修改内部成员的mutator方法，而且`entrySet()`方法返回的`Set`接口引用也用`Collections.unmodifiableSet()`方法包装过了。但它至少还有两个问题：
1. 包装以后的`Set`虽然是不可变的。但内部的元素`Map.Entry`仍然是可变的。一旦用`iterator()`方法获得了内部元素的引用后，还是可以用`Map.Entry#setValue()`方法修改内部值。
2. `entrySet()`不是唯一暴露内部对象引用的方法，`values()`方法和`keySet()`方法同样暴露键-值对象`K`和`V`的引用。

```java
package com.ciaoshen.effectivejava.chapter4;
import java.util.*;

class ImmutableLinkedMap<K,V> extends AbstractMap<K,V> {
     private LinkedSet nodeSet = linkedset();
     private ImmutableLinkedMap() {}
     public static <K,V> Map<K,V> newInstance(Map<K,V> map) {
         ImmutableLinkedMap<K,V> newMap = new ImmutableLinkedMap<>();
         newMap.nodeSet.putAll(map.entrySet());
         return newMap;
     }
     /**
      * 这里返回的Set用Collections.unmodifiableSet()包装了一下。
      * 但这样并不能保证返回的Set是不可变对象。因为Iterator仍然返回Set内部元素的引用。
      * 而Map.Entry又是可变的。
      */
     public Set<Map.Entry<K,V>> entrySet() {
         return Collections.unmodifiableSet(nodeSet);
     }
     /**
      * 然后禁掉下面这些修改对象或者暴露内部对象引用的方法。
      */
     @Override
     public V get(Object key) {
         throw new UnsupportedOperationException("This map is immutable!");
     }
     @Override
     public V put(K k,V v) {
         throw new UnsupportedOperationException("This map is immutable!");
     }
     @Override
     public void putAll(Map<? extends K, ? extends V> map) {
         throw new UnsupportedOperationException("This map is immutable!");
     }
     @Override
     public V remove(Object key) {
         throw new UnsupportedOperationException("This map is immutable!");
     }
     @Override
     public void clear() {
         throw new UnsupportedOperationException("This map is immutable!");
     }
     private static class Node<K,V> implements Map.Entry<K,V> {
         private K key;
         private V value;
         private Node<K,V> next;
         private Node() {}
         private Node(K k, V v) {
             key = k;
             value = v;
             next = null;
         }
         public static <K,V> Node<K,V> newInstance() {
             return new Node<K,V>();
         }
         public static <K,V> Node<K,V> newInstance(K k, V v) {
             return new Node<K,V>(k,v);
         }
         public K getKey() {
             return key;
         }
         public V getValue() {
             return value;
         }
         public V setValue(V v) {
             V oldValue = value;
             value = v;
             return oldValue;
         }
         public boolean equals(Object o) {
             if (o == this) { return true; }
             if ( ! ( o instanceof Node ) ) {
                 return false;
             }
             @SuppressWarnings({"rawtypes","unchecked"})
             Node<K,V> n = (Node)o;
             return n.key.equals(key);
         }
         public int hashCode() {
             return 31 * 17 + key.hashCode();
         }
         public String toString() {
             return "N[" + key + "," + value + "]";
         }
     }
     // 不对外公开
     private LinkedSet linkedset() { return new LinkedSet(); }
     // LinkedSet仍然是可变的
     private class LinkedSet extends AbstractSet<Map.Entry<K,V>> {
         private Node<K,V> head;
         private int size;
         private LinkedSet() {
             head = Node.newInstance();
         }
         public int size() { return size; }
         public Iterator<Map.Entry<K,V>> iterator() {
             return new Iterator<Map.Entry<K,V>>() {
                 private Node<K,V> cursor = head;
                 private Node<K,V> previous = cursor;
                 public boolean hasNext() {
                     return cursor.next != null;
                 }
                 public Map.Entry<K,V> next() {
                     if (cursor.next == null) {
                         throw new NoSuchElementException();
                     }
                     Node<K,V> next = cursor.next;
                     previous = cursor;
                     cursor = next;
                     return next;
                 }
                 public void remove() {
                     if (cursor == previous) {
                         throw new IllegalStateException();
                     }
                     previous.next = cursor.next;
                     cursor.next = null;
                     cursor = previous;
                     size--;
                 }
             };
         }
         @Override
         public boolean add(Map.Entry<K,V> node) {
             throw new UnsupportedOperationException("add() method of Set interface is too weak!");
         }
         @Override
         public boolean addAll(Collection<? extends Map.Entry<K,V>> c) {
             throw new UnsupportedOperationException("addAll() method is suspanded ,because add() method of Set interface is too weak!");
         }
         /**
          * 不对外公开
          */
         private V put(Node<K,V> node) {
             V result = null;
             Iterator<Map.Entry<K,V>> ite = iterator();
             while (ite.hasNext()) {
                 Map.Entry<K,V> entry = ite.next();
                 if (node.equals(entry)) {
                     ite.remove();
                     result = entry.getValue();
                 }
             }
             node.next = head.next;
             head.next = node;
             size++;
             return result;
         }
         private void putAll(Collection<? extends Map.Entry<K,V>> c) {
             for (Map.Entry<K,V> entry : c) {
                 Node<K,V> node = new Node<>(entry.getKey(),entry.getValue());
                 put(node);
             }
         }
     }
     public static void main(String[] args) {
         int size = 10;
         Map<Integer,Character> asciiMap = new HashMap<>();
         // ERROR
         //MapTester.newInstance(ImmutableLinkedMap.newInstance(asciiMap)).test(size);
         asciiMap = MapTester.fillAsciiMap(asciiMap,size);
         Map<Integer,Character> immutableMap = ImmutableLinkedMap.newInstance(asciiMap);
         System.out.println(immutableMap);
         for (Map.Entry<Integer,Character> entry : immutableMap.entrySet()) {
             // 虽然entrySet返回的Set引用用unmodifiableSet包装过，
             // 但因为内部元素Map.Entry还是可变的，所以setValue()方法还是可以改变ImmutableLInkedMap内部的值。
             entry.setValue((char)100);
         }
         System.out.println("WTF??? " + immutableMap);
     }
 }
 ```

下面的版本又进一步减弱了内部套嵌类`Node`的可变性，但`Map.Entry#getKey()`,`Map.Entry#getValue()`还是会暴露`K`和`V`型对象的引用。而且`Map#values()`和`Map.keySet()`方法也会暴露`K`和`V`型对象的引用。一旦`K`和`V`是可变类型的话，整个Map的可变性还是受到破坏。

所以尽可能把每个类都设计成不可变的比较好。因为任何一个可变对象的引用暴露，都会破坏整个类的不可变性。

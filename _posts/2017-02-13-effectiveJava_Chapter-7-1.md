---
layout: post
title: "[Effective Java] Note: - Chapter-7-1: Design Method Signatures Carefully"
date: 2017-02-13
author: "Wei SHEN"
categories: ["Java","Effective_Java"]
tags: ["Code_Style"]
description: >
  方法签名主要指的是，方法的名称，参数和返回值。虽然返回值的类型不足以用来区别两个方法。主要有以下几点：首先，方法名要风格保持统一。其次，参数控制在4个以内。然后，要控制接口的方法数量。关于方法的参数，尽量使用接口，而不是类。最后记住在用可变长参数的时候，小心基本型。而且尽量不要重载参数数量相同的方法。    
---

### 方法名风格保持一致
这没什么好说的，方法名用首字母小写的驼峰形式：**`methodName()`**。用词注意。

### 方法参数不要太多
实在参数太多的情况下，可以有三个解决办法，
#### 使用静态成员辅助类。将多个参数定义成一个结构体。
比如下面这种情况，
```java
public void playCards(String cardName, int cardColor) {
    // ... ...
}
```
就可以设计一个`Card`类型。
```java
class Card {
    private final String name;
    private final int color;
}
```
然后用`Card`型做参数，
```java
public void playCards(Card card) {
    // ... ...
}
```

#### 用Builder模式
更加复杂的情况可以定义一个Builder模式的辅助类。允许客户端程序员一个一个地设参数。

#### 设计功能正交的子方法
比如，查找子列表`sublist`中的首元素这个方法，需要三个参数，
```java
//查找子列表sublist中的首元素这个方法
public int firstInSublist(int fromIndex, int toIndex, E element) {
    // ... ...
}
```
可以设计成两个正交的子方法来完成同样的任务，先获取子列表，
```java
// 先返回子列表
public List<E> sublist(int fromIndex, int toIndex) {
    // ... ...
}
```
然后再查找，
```java
// 先返回子列表
public int firstIndexOf(E element) {
    // ... ...
}
```

### 参数类型优先使用接口，而不是类
好处是客户端程序员用起来更舒服。如果某方法虽然只用到`Map`接口定义的方法，但参数只接受`HashMap`而不是所有`Map`，用户就必须将他的`TreeMap`转成`HashMap`再用。

### 慎用重载
就算能用重载，也尽量不要用。使用前也要考虑清楚参数。

因为，对 **重载方法(Overloaded method)** 的选择是 **静态的**。换句话说，是编译期做出的决定。没有说好的多态，没有后期绑定。

对于被 **覆盖(Override method)** 的选择才是动态的。

看下面这个例子，对于下面三个重载方法，我们期望的是能有类似多态的属性。
```java
public static String classify(Set<?> set) { return "Set"; }
public static String classify(List<?> list) { return "List"; }
public static String classify(Collection<?> collection) { return "Collection"; }
```
但实际上，下面测试，返回的都是`Collection`。全都调用了第三个重载方法。
```java
    Collection<String> c1 = new HashSet<>();
    Collection<String> c2 = new ArrayList<>();
    Collection<Integer> c3 = new HashMap<String,Integer>().values();
    System.out.println(classify(c1)); // print: Collection
    System.out.println(classify(c2)); // print: Collection
    System.out.println(classify(c3)); // print: Collection
```

#### 保守方案：所有重载方法，参数数量必须不同
有一个参数和两个参数的两个重载方法，编译器永远不会选错。

#### 大胆方案：就算参数个数相同，必须是“不相关（radically different）”类
“不相关类”是指：**两个类都不是对方的子类**。

#### 小心自动装箱类
因为有了自动装箱功能，`int`基本型就和`Object`不是不相关类了。因为`int`会被自动包装成`Integer`类。这会导致意想不到的错误，比如，`List`有两个重载的`remove`方法，分别为，
* `remove(int index)` : 删除特定位置的元素。
* `remove(Object o)` : 删除给定元素。

下面在`list`里插入`[-3,-2,-1,0,1,2]`，然后删除`[0,1,2]`。应该还剩`[-3,-2,-1]`。但结果返回的却是`[-2,-,2]`。
```java
/**
 * 先插入: -3,-2,-1,0,1,2
 * 再删除: 0,1,2
 * 应该剩下: -3,-2,-1
 * 但输出结果是: -2,0,-2
 * 因为List#remove(Object o)和List#remove(int i)两个版本
 * 这里调用的是List#remove(int i)。先删除第0个位置元素，然后删除第1,2位置的元素。
 * 所以返回-2,0,2
 */
public static void listRemoveError() {
    List<Integer> list = new ArrayList<>();
    for (int i = -3; i < 3; i++) {
        list.add(i);
    }
    for (int i = 0; i < 3; i++) {
        list.remove(i);
    }
    System.out.println(list);
}
```

### 慎用可变长参数
基本型数组赋给可变长参数时，会出bug。

#### 可变长参数长度为0时需要强制检查
下面这个函数计算多个`int`型参数的最小值。问题是总是要检查参数长度就很麻烦，也不美观。
```java
static int min(int... args) {
    if (args.length == 0) {
        throw new IllegalArgumentException("Too few arguments!");
    }
    int min = args[0];
    for (int i = 1; i < args.length; i++) {
        if (args[i] < min) {
            min = args[i];
        }
    }
    return min;
}
```
解决方案是，使用两个参数，先传单个的`int`进去，然后再是可变长参数。代码就好多了。
```java
static int min(int first, int... args) {
    int min = first;
    for (int arg : args) {
        if (args[i] < min) {
            min = args[i];
        }
    }
    return min;
}
```

#### 小心基本型数组
`<T> List<T> Arrays.asList(T... args)`方法接受接受可变长参数。但因为它是一个泛型方法，编译器在进行类型推断的时候就容易出错。而且更不好的是，它对基本型非常不友好。
```java
@SafeVarargs
public static <T> List<T> asList(T... a) {
    return new ArrayList<>(a);
}
```
如果我们用`Integer`作为它的参数，一切正常。因为泛型类型参数`T`被正确识别为`Integer`。
```java
/**
 * 如果参数是Integer包装类对象的数组，asList()就会把每个Integer对象都插入List。
 */
public static void printArrayWithAsListV3() {
    System.out.println(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,0}));
}
```
当我们传递基本型数组`int[]`进去的时候，不能正常工作。因为编译器把整个数组`int[]`当成了泛型的类型参数`T`。
```java
/**
 * asList()方法的参数是可变长参数，int[]会被认为是单个数组对象。
 * 所以最终调用的是数组继承自Object的toString()方法，只打印内存地址。
 * public static <T> List<T> asList(T... a) {
 *     return new ArrayList<>(a);
 * }
 */
public static void printArrayWithAsListV2() {
    System.out.println(Arrays.asList(new int[]{1,2,3,4,5,6,7,8,9,0}));
}
```

#### 可变长参数每次都要初始化数组，开销大
如果对性能要求非常苛刻，可以多写几个重载方法，替代可变长参数。比如下面这个例子，只有当参数长度超过3个的时候，才调用有可变长参数的版本。
```java
foo() {}
foo(int a1) {}
foo(int a1, int a2) {}
foo(int a1, int a2, int a3) {}
foo(int a1, int a2, int a3, int... args) {}
```

---
layout: post
title: "Why Can Outer Classes Access Inner Class Private Members?"
date: 2017-04-07 19:26:29
author: "Wei SHEN"
categories: ["java"]
tags: ["inner class","closure"]
description: >
---

### 引子
首先，内部类和外部类能无障碍互相访问是一个事实，
> 外部类能访问内部类的所有成员，包括`private`成员。
> 内部类也能访问外部类的所有成员，包括`private`成员。


### 问题：外部类的派生类无法访问内部类？？
`Thinking in Java - Chapter 10 Inner Class - Exercise 6`的问题如下：
```
Exercise 6: (2) Create an interface with at least one method, in its own package. Create a class in a separate package. Add a protected inner class that implements the interface. In a third package, inherit from your class and, inside a method, return an object of the protected inner class, upcasting to the interface during the return.
```

今天犯了一个有意思的错误。

包`packA`里有个`Outer`类，它包含一个内部类`Inner`。`Outer`类的`inner()`工厂方法，创建并返回`Inner`类实例的引用。一切运行正常。
```java
package com.ciaoshen.packA;

public class Outer {
        protected class Inner {
            public void foo() { System.out.println("Hello Ronald!"); }
        }
        protected Inner inner() {
            return new Inner();
        }
        public static void main(String[] args) {
            new Outer().inner().foo(); // Output: Hello Ronald!
        }
}
```

现在换另一个包`packB`。 `DerivedOuter`继承自`Outer`. `DerivedOuter`调用继承自`Outer`的`inner()`方法。还是一切正常。

```java
package com.ciaoshen.packB;

class DerivedOuter extends Outer {
    public static void main(String[] args) {
        new DerivedOuter().inner().foo(); // Output: Hello Ronald!
    }
}
```

但一旦重写 `DerivedOuter`里的`inner()`方法，虽然只是改了一个权限，方法内容照旧。但出现系统报错：**ERROR: Outer.Inner() has protected access in Outer.Inner.**
```java
package com.ciaoshen.packB;

class DerivedOuter extends Outer {
    @Override
    public Inner inner() { // this is not the inner() of Outer class. BOOM!
        return new Inner();
    }
    public static void main(String[] args) {
        new DerivedOuter().inner().foo(); // ERROR: Outer.Inner() has protected access in Outer.Inner
    }
}
```
`DerivedOuter`不是继承了`Outer`了吗？ **为什么不能调用`Inner`的构造函数？** 况且原先没有重写的时候，一切正常。

**为题就出在“重写”上！** 根据Java的继承规则，派生类不是彻彻底底一个新的类。而是先初始化基类。也就是说 派生类里包含了一个基类。而且派生类和基类的“成员字段”分得清清楚楚，甚至当重名是，同时保留两份同名字段，一份用`super`引用，一份用`this`引用。只有“方法”是共用的。但 方法一旦被“重写”，就不是原来基类的方法。

也就是说，不是像一般想象的那样，`DerivedOuter`继承自`Outer`就包含了`Inner`，就可以对`Inner`做任何操作。 实际情况是，**`DerivedOuter`继承了`Outer`的所有方法，操作`Inner`也是通过调用`Outer`原生的`inner()`方法完成。因为`Inner`是在`Outer`里声明的，只有原生的`Outer`方法对`Inner`拥有一切权限。后面在`DerivedOuter`里加入的新方法，包括重写`Outer`类里的`inner()`，统统无效，无法访问`Inner`的`protected`成员。**

### 总结
这就是为什么`Thinking in Java`书里说：**内部类可以很方便的隐藏实现细节**。

> 只要内部类声明为`private`，除了外部类，没有人能访问它。

> 如果内部类声明为`protected`，只有外部类及其子类，还有同一个包内的类能够访问它。而且也只是能访问它的实例，不能访问它的`protected`或者`package access`或者`private`的成员。

如果返回引用前，再向上转型成某些公开接口的话，用户甚至都无法向下转型成具体类型。

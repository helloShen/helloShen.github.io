---
layout: post
title: "[Effective Java] Note: - Chapter-3-2: Be careful with clone() method"
date: 2017-02-06
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["clone"]
description: >
  Cloneable接口的通用约定很弱，新建对象，然后逐域浅拷贝就能满足。所以Object的clone()方法实现的就是浅拷贝。但浅拷贝只对基本型和不可变对象有效。因为基本型没有引用，直接复制值。不可变对象只读不写也不受影响，而且来就是要控制实例，复制引用正合适。但可变对象如果是浅拷贝就不行。还需要另外重新调整，比如递归调用可变对象的clone()方法。这章第二个关键点，就是对Cloneable接口的继承问题。惯用法是，尽量让Object#clone()方法替我们工作。因此当一个类需要实现clone()方法，理想的情况就是它的所有超类的clone()方法都递归调用了super.clone()方法，最终成功追溯到Object#clone()方法。
---

### Cloneable接口的通用约定非常弱：逐域浅拷贝就能满足
Cloneable的通用约定有三：
1. `x.clone() != x` 为`true`。
2. `x.clone().getClass() == x.getClass()` 为`true`。 但这不是绝对的要求。
3. `x.clone().equals(x)` 为`true`。 但这也不是绝对的要求。

想像下面这个`ABC`类，包含了`A`,`B`,`C`三个类型的域。`copy()`方法创建一个新对象，然后逐域拷贝引用，这是一个标准的`浅拷贝`，但它却满足`Cloneable`接口的通用约定。
```java
public class ABC {
    A a;
    B b;
    C c;
    public ABC(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    public ABC copy(ABC o) { // 这个copy完全执行浅拷贝，但返回对象满足Cloneable接口的约定
        return new ABC(o.a, o.b, o.c);
    }
}
```

### Object类的clone()方法的行为
Object类源码中的注释部分如下：

> First, if the class of this object does not implement the interface {@code Cloneable}, then a code **CloneNotSupportedException** is thrown.
> Otherwise, this method creates a new instance of the class of this object and **initializes all its fields with exactly the contents of the corresponding fields of this object**, as if by assignment; the contents of the fields are not themselves cloned. Thus, this method performs a **"shallow copy"** of this object, not a "deep copy" operation.
> Note that all arrays are considered to implement the interface {@code Cloneable}

总结起来就是三点：
1. 类如果没有实现`Clonable`接口，调用`clone()`方法会抛出`CloneNotSupportedException`异常。
2. 实现了`Clonable`接口的类，`clone()`方法需要执行一个`浅拷贝`。
3. 任何类型的数组都默认实现了`Clonable`接口。

### 尽可能接近深拷贝的clone()方法惯用法
`clone()`方法只做 **`浅拷贝`** 就可以满足`Cloneable`接口的约定。 **`浅拷贝`** 都是复制对象，而不是内容。
1. **基本型** 没有引用，所以不存在 **`浅拷贝`** 的情况，都是 **`深拷贝`**。
2. **不可变对象** 做深拷贝是不应该发生的事，比如`String`和`Enum`，尤其是`Enum`应该绝对禁止拷贝。
3. **可变的对象** 是可以深拷贝的。这需要一个类域中的可变对象都递归实现了`Cloneable`接口。

下面的代码展示了一个类实现`Cloneable`接口的惯用法。
1. 对这个类以及这个类的所有超类递归实现`Cloneable`接口
2. 对这个类中所有的`可变对象`域，递归实现`Cloneable`接口

这样实现的结果，也只能说尽可能地接近`深拷贝`。因为其中的`不可变`域还是`浅拷贝`。但这样的`浅拷贝`并不影响实际的使用。
```java
public class Person implements Cloneable {
    private final String name; // String is not Cloneable
    private final int age;
    private final Sex sex; // Enum is not Cloneable
    private final PhoneNumber telephone;
    // ... some other code here
    @Override
    public Object clone() {
        try {
            return (Person)super.clone();
        } catch (CloneNotSupportedException e) { // cannot happen
            return new RuntimeException(e);
        }
    }
}
```

```java
public class PhoneNumber implements Cloneable {
    private final short areaCode;
    private final short prefix;
    private final short lineNumber;
    @Override
    public Object clone() {
        try {
            return (PhoneNumber)super.clone();
        } catch (CloneNotSupportedException e) { // cannot happen
            return new RuntimeException(e);
        }
    }
}
```

### 第二条惯用法：尽量让Object#clone()替我们工作，递归调用超类super.clone()方法，以保持一条完整的自动构造器调用链
当一个类要实现一个行为良好的`clone()`方法，如果它的所有超类都递归调用了`super.clone()`方法，并最终追溯到最终超级父类`Object`的`clone()`方法，那么`Object`的`clone()`方法总能复制并返回一个正确的类型。因此，这个类也可以调用`super.clone()`方法就能获得一个正确的拷贝对象。

但如果中间哪一个类没有调用`super.clone()`方法，而是使用了自己的构造器返回克隆的实例，那么它的所有子类就都不能获得`Object`的`clone()`方法提供的良好服务。而`Object`的`clone()`方法是行为良好的`clone()`方法的有力保障。所以，**尽量保持自动向上构造器调用链的畅通**。

具体参考下面这个例子，`Person`，`Employee`，`Manager`三个类层层继承。`Employee`比`Person`多了一个表示职位的域`position`，`Manager`又比`Employee`多了一个表示持有公司股票份额的域`stock`。这三个类的`clone()`方法异常简单，都只是向上转达了对`super.clone()`的调用。而最后实际执行工作的`Object#clone()`能够识别出调用对象`mg`运行时的实际类型，并且成功拷贝了`Manager`的所有四个域。将任务交给`Object#clone()`来做，事情就变得简单许多。

```java
package com.ciaoshen.effectivejava.chapter3;

public class TestConstructor {
    private static class Person implements Cloneable {
        protected final String name;
        protected short age;
        public Person(String name, int age) {
            this.name = name;
            this.age = (short)age;
        }
        public String toString() {
            return "Person[" + name + ", " + age + "]";
        }
        public Person clone() {
            try {
                return (Person)super.clone(); // 最后实际执行任务的是Object#clone()方法，它识别出实际类型为Manager，然后逐域拷贝。
            } catch(CloneNotSupportedException e) {
                throw new RuntimeException(e); // never happen
            }
        }
    }
    private static class Employee extends Person implements Cloneable {
        protected String position;
        public Employee(String name, int age, String position) {
            super(name,age);
            this.position = position;
        }
        public String toString() {
            return "Employee[" + name + ", " + age + ", " + position + "]";
        }
        public Employee clone() {
            return (Employee)super.clone();
        }
    }
    private static class Manager extends Employee implements Cloneable {
        protected int stock;
        public Manager(String name, int age, String position, int stock) {
            super(name,age,position);
            this.stock = stock;
        }
        public String toString() {
            return "Manager[" + name + ", " + age + ", " + position + ", " + stock + "]";
        }
        public Manager clone() {
            return (Manager)super.clone();
        }
    }
    public static void main(String[] args) {
        Manager mg = new Manager("Ronald", 30, "Chef de project", 10000);
        Manager mgCopy = mg.clone(); // 最终追溯到Object#clone()
        System.out.println(mgCopy);
    }
}
```

### 提供一个拷贝工厂是个好做法
和静态工厂一样，为复杂对象的拷贝提供一个拷贝工厂也可以让事情变得更简单明了。

### 最后的忠告
1. 其他接口都不应该扩展`Cloneable`接口。
2. 为了继承而实现的类也不应该实现`Cloneable`接口。
3. 最好就是根本不要去用`Cloneable`接口和`clone()`方法。

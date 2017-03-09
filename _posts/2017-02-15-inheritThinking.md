---
layout: post
title: "Composition: Hiding Implementation Details of Base Class from Derived Class"
date: 2017-02-14
author: "Wei SHEN"
categories: ["java","framework","design pattern"]
tags: ["composition","inheritance","encapsulation"]
description: >
---

### 最朴素的继承
下面这个是最朴素的继承方式。基类`Employee`有用来计算工资的两个域，和一个计算工资的函数。子类`Manager`增加了计算工资需要的两个额外的域，然后覆盖了计算工资的函数。就这么简单。
```java
class Employee {
    protected final int baseSalary;
    protected final int years;
    public Employee(int baseSalary, int years) {
        this.baseSalary = baseSalary;
        this.years = years;
    }
    public int getSalary() { // 普通员工工资算法
        return baseSalary + (100 * years);
    }
    // reminder omitted
}
class Manager extends Employee {
    protected final int baseBonus;
    protected final int level;   
    public Manager(int salary, int years, int bonus, int level) {
        super(salary,years);
        baseBonus = bonus;
        this.level = level;
    }
    public int getSalary() { // 管理层的工资算法
        return super.getSalary() + (baseBonus * level);
    }
    // remainder omitted
}
```

### 站在Employee和Manager的角度，分别看到了什么？
站在一个`Employee`的角度看，它需要管理它的2个域`baseSalary`和`years`。

站在一个`Manager`的角度看，它需要管理它全部的4个域`baseSalary`和`years`，以及`baseBonus`和`level`。

这样会不会很累？`Employee`能不能管好它的两个域。然后通过公共接口对外提供对内部域的有限访问。这样`Manager`可以通过`Employee`来管理前两个域，它自己只需要管理后两个域。

### 职责明确的阶梯状的继承
变化其实很简单，我只是私有化了`Employee`中的两个域，以及`Manager`中的两个域。

这样`Manager`不需要实际继承`Employee`的所有域，它只需要通过`getSalary()`接口访问`Employee`对象提供的服务。
```java
class Employee {
    private final int baseSalary;
    private final int years;
    public Employee(int baseSalary, int years) {
        this.baseSalary = baseSalary;
        this.years = years;
    }
    public int getSalary() { // 普通员工工资算法
        return baseSalary + (100 * years);
    }
    // reminder omitted
}
class Manager extends Employee {
    private final int baseBonus;
    private final int level;   
    public Manager(int salary, int years, int bonus, int level) {
        super(salary,years);
        baseBonus = bonus;
        this.level = level;
    }
    public int getSalary() { // 管理层的工资算法
        return super.getSalary() + (baseBonus * level);
    }
    // remainder omitted
}
```

### 以上职责相当明确的继承相当于组合
只需要把上面的两个域`baseSalary`和`years`替换成`Employee`对象，继承就变成了组合。
```java
class Employee {
    private final int baseSalary;
    private final int years;
    public Employee(int baseSalary, int years) {
        this.baseSalary = baseSalary;
        this.years = years;
    }
    public int getSalary() { // 普通员工工资算法
        return baseSalary + (100 * years);
    }
    // reminder omitted
}
class Manager {
    private final Employee employee;
    private final int baseBonus;
    private final int level;   
    public Manager(Employee employee, int bonus, int level) {
        this.employee = employee;
        baseBonus = bonus;
        this.level = level;
    }
    public int getSalary() { // 管理层的工资算法
        return employee.getSalary() + (baseBonus * level);
    }
    // remainder omitted
}
```

### 进化成装饰模式
组合很容易转换成装饰器模式。只需把前一个对象的引用传递给后一个类的构造器。和组合一样，每一层装饰只负责它管理的几个域。这样对数据的封装就比较合理。
```java
interface Employee {
    public int getSalary();
}
class BaseEmployee implements Employee {
    private final int baseSalary;
    private final int years;
    public BaseEmployee(int baseSalary, int years) {
        this.baseSalary = baseSalary;
        this.years = years;
    }
    public int getSalary() { // 普通员工工资算法
        return baseSalary + (100 * years);
    }
}
class TrafficAllowance implements Employee {
    private final Employee employee;
    private final int allowance;
    public TrafficAllowance(Employee employee, int allowance) {
        this.employee = employee;
        this.allowance = allowance;
    }
    public int getSalary() { // 管理层的工资算法
        return employee.getSalary() + allowance;
    }
}
class ManagerBonus implements Employee {
    private final Employee employee;
    private final int baseBonus;
    private final int level;
    public ManagerBonus(Employee employee, int bonus, int level) {
        this.employee = employee;
        baseBonus = bonus;
        this.level = level;
    }
    public int getSalary() { // 管理层的工资算法
        return employee.getSalary() + (baseBonus * level);
    }
}
```

### 总结
总的来说，虽然封装的方式可以千变万化，但 **组合优于继承** 的理念背后的逻辑还是很清楚：
> **组合类面对的是封装好的对象。不需要费心去管理这个对象，只要调用接口提供的服务就可以了**。

> **但继承是侵入式的，它破坏了基类的封装。**。

---
layout: post
title: " A Test Framework Based on Template Pattern and Strategy Pattern"
date: 2017-02-21
author: "Wei SHEN"
categories: ["Java","Framework","Design_Pattern"]
tags: ["Strategy_Pattern","Template_Pattern"]
description: >
---

### 概述
通过策略模式将单个测试策略和整个测试流程解耦。所以单个测试可以被描述成 **对某个容器C执行某单元测试T**。整个流程提供了两个注册表：
1. 待测试容器的注册表
2. 单元测试注册表

利用模板模式，以静态工厂方法让这两个注册表虚位以待，然后实现剩下的骨架实现。下面是具体代码。

### Test.java
策略对象接口。
```java
package com.ciaoshen.thinkinjava.newchapter17;

// 无状态对象模拟函数指针（策略模式）
// 适合用匿名内部类的形式初始化
interface Test<C> {
    long test(C container, int times);
}
```

### Tester.java
调用策略对象的工具类。可以对给定的某个容器，执行所有单元测试。
```java
package com.ciaoshen.thinkinjava.newchapter17;
import java.util.*;

// 包级私有
final class Tester<C> {
    /**
     * 配置成员域
     */
    private static final int[] defaultParams = new int[]{10,5000,100,5000,1000,500,10000,50};
    private int[] paramList = defaultParams; // 参数
    private final C container; // 被测试容器
    private final Map<String,Test<C>> tests; // 测试策略对象注册表
    private final String header; // 标题

    /**
     * 配置函数
     */
    public C initialize(int size) { // 可以测试前配置。目前此功能没有激活。
        return container;
    }
    // 不公开构造器
    // 没有保护性拷贝，因为只是用来自己测试
    private Tester(C container, Map<String,Test<C>> tests) {
        this.container = container;
        this.tests = tests;
        header = container.getClass().getSimpleName();
    }
    // 静态工厂方法入口应该保证：
    //   参数数组至少有一组（2个）参数，而且数组长度为偶数。
    private Tester(C container, Map<String,Test<C>> tests, int[] paramList) {
        this(container,tests);
        assert paramList.length >= 2;
        assert paramList.length % 2 == 0;
        this.paramList = paramList;
    }

    /**
     * 公开静态工厂方法，方便运行测试
     */
    public static <C> void run(C cntnr, Map<String,Test<C>> tests) {
        new Tester<C>(cntnr,tests).timedTest();
    }
    public static <C> void run(C cntnr, Map<String,Test<C>> tests, int[] paramList) throws IllegalArgumentException {
        if (paramList.length < 2) {
            throw new IllegalArgumentException("Need at least 1 pair(test size and loop times) of paramater!");
        }
        if ( (paramList.length % 2) != 0 ) {
            throw new IllegalArgumentException("Parameters should be in pair!");
        }
        new Tester<C>(cntnr,tests,paramList).timedTest();
    }

    /**
     * 实际测试模块。
     * 调用每个Test的test()方法。
     */
     private static final String SIZE_FIELD = "%5d";
     private static final String RESULT_FIELD = "%10.10s: %10d";
     public void timedTest() { // paramList的长度已经检查过
         Formatter f = new Formatter(System.out);
         for (int i = 0; i < paramList.length/2; i++) {
             int size = paramList[i*2];
             int loops = paramList[i*2+1];
             f.format(SIZE_FIELD, size);
             for (Map.Entry<String,Test<C>> test : tests.entrySet()) {
                 C kontainer = initialize(size);
                 f.format(RESULT_FIELD, test.getKey(), eachTest(test.getValue(),kontainer,size,loops));
             }
             f.format("\n");
         }
     }
     // 为了和Test接口匹配
     private long eachTest(Test<C> test, C kontainer, int size, int loops) {
         assert size > 0;
         assert loops > 0;
         long result = 0;
         for (int i = 0; i < loops; i++) {
             result += test.test(kontainer, size);
         }
         return result / loops;
     }
}
```

### AbstractTesterController.java
`Tester`工具类的控制器。使用`模板模式`的骨架实现。

```java
package com.ciaoshen.thinkinjava.newchapter17;
import java.util.*;

// 使用了模板模式的骨架实现
abstract class AbstractTesterController<C> {
    /**
     * 抽象primitive method
     * 以下的骨架实现大部分依赖于testRegistry()方法返回的测试策略对象的注册表。
     */
    public abstract Map<String,Test<C>> testRegistry();
    /**
     * 向用户开放待测试容器类Class对象的注册
     * 所以ListTesterInBook不是不可变类
     */
    private final Set<String> containers = new LinkedHashSet<>(Arrays.asList(new String[]{"java.util.ArrayList","java.util.LinkedList"}));
    public Set<String> containerRegistry() { // 向用户暴露私有域对象的引用。不安全，但测试框架不是API的一部分，仅供包内使用。
        return containers;
    }
    /**
     * 主方法
     * 根据所有注册对象进行测试
     */
    public void run() {
        for (String name : containers) {
            C container = classForName(name);
            System.out.println(">>>>>>>>>>> " + name + " <<<<<<<<<<<");
            Tester.run(container,testRegistry());
        }
    }
    /**
     * 利用反射，根据类型名称，构造容器实例。
     * 前提是容器基本都有无参数的构造函数。
     */
    @SuppressWarnings("unchecked")
    private C classForName(String name) {
        Class<?> klass = null;
        try {
            klass = Class.forName(name); // 获取Class对象
        } catch(ClassNotFoundException e) {
            System.err.println(name + " Class not found.");
            System.exit(1);
        }

        C object = null;
        try {
            object = (C) klass.newInstance(); //用newInstance()构造实例，赋值给接口
        } catch (IllegalAccessException e) {
            System.err.println(klass.getSimpleName() + " Class not accessible.");
            System.exit(1);
        } catch (InstantiationException e) {
            System.err.println(klass.getSimpleName() + " Class not instantiable.");
            System.exit(1);
        }
        return object;
    }
}
```

### ListTesterController.java
实现了骨架抽象类`AbstractTesterController`。
```java
/**
 * ListTester依赖两组数据：
 *     1. 注册一组针对List接口的Test类
 *     2. 注册一组实现List接口的容器类Class对象
 */
final class ListTesterController extends AbstractTesterController<List<String>> {
    /**
     * 实现primitive方法
     */
    public Map<String,Test<List<String>>> testRegistry() {
        Generator<String> gen = StringGenerator.newInstance();
        Map<String,Test<List<String>>> tests = new LinkedHashMap<String,Test<List<String>>>();
        tests.put("Add", new Test<List<String>>() {
            public long test(List<String> list, int size) {
                String str = gen.next();
                long start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    list.add(str);
                }
                long end = System.nanoTime();
                return end - start;
            }
        });
        tests.put("AddAll", new Test<List<String>>() {
            public long test(List<String> list, int size) {
                List<String> tempList = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    tempList.add(gen.next());
                }
                long start = System.nanoTime();
                list.addAll(tempList);
                long end = System.nanoTime();
                return end - start;
            }
        });
        return tests;
    }
    /**
     * 快捷静态方法：可变长参数设置需要测试的容器
     */
    public static void test(String... args) {
        ListTesterController controller = new ListTesterController();
        controller.containerRegistry().addAll(Arrays.asList(args));
        controller.run();
    }
    public static void main(String[] args) {
        ListTesterController.test("java.util.ArrayList","java.util.LinkedList","java.util.Stack","java.util.Vector");
    }
}
```

### Generator.java
数据生成器接口。

```java
package com.ciaoshen.thinkinjava.newchapter17;
import java.util.*;

// 无状态对象模拟函数指针（策略模式）
// 适合用匿名内部类的形式初始化
interface Generator<T> {
    public T next();
}
```

### StringGenerator.java
实现`Generator`接口的字符串生成器。
```java
package com.ciaoshen.thinkinjava.newchapter17;
import java.util.*;

final class StringGenerator implements Generator<String> {
    private static final int DEFAULT_LENGTH = 7;
    private static Generator<String> GEN = null;
    private final char[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final char[] LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private final Random R = new Random();
    private final int STR_LENGTH;
    private StringGenerator(int size) { STR_LENGTH = size; }
    public static Generator<String> newInstance() { // pre-charge Singleton
        if (GEN == null) {
            GEN = new StringGenerator(DEFAULT_LENGTH);
        }
        return GEN;
    }
    public static Generator<String> newInstance(int size) { // the only public factory return Generator interface
        return new StringGenerator(size);
    }
    public String next() {
        StringBuilder sb = new StringBuilder();
        sb.append(UPPER[R.nextInt(UPPER.length)]);
        for (int i = 0; i < STR_LENGTH-1; i++) {
            sb.append(LOWER[R.nextInt(LOWER.length)]);
        }
        return sb.toString();
    }
}
```

---
layout: post
title: "A Test Framework based on Strategy And Builder Pattern"
date: 2017-02-14
author: "Wei SHEN"
categories: ["java","framework","design pattern"]
tags: ["strategy pattern","builder pattern"]
description: >
---

### Test.java
策略对象对应的接口。
```java
package com.ciaoshen.thinkinjava.newchapter17;

// 无状态对象模拟函数指针（策略模式）
// 适合用匿名内部类的形式初始化
interface Test<C> {
    long test(C container, int times);
}
```

### Tester.java
调用`Test`策略对象的`Tester`接口。

```java
package com.ciaoshen.thinkinjava.newchapter17;

interface Tester<C> {
    long runForName(String name, int testSize, int repeatTimes) throws NoSuchElementException;
    void runAll(int testSize, int repeatTimes);
}
```

### Generator.java
用来自动生成测试数据的`Generator`接口。也是一个策略对象。

```java
package com.ciaoshen.thinkinjava.newchapter17;

// 无状态对象模拟函数指针（策略模式）
// 适合用匿名内部类的形式初始化
interface Generator<T> {
    public T next();
}
```

### Builder.java
用来构造`Tester`对象的工厂类的抽象接口。

```java
package com.ciaoshen.thinkinjava.newchapter17;

interface Builder<T> {
    T build();
}
```

### GenericTester.java
Tester是一个负责执行Test策略的工具，它内部维护着一个由一组策略和它们的名字组成的映射Map，
* Tester#runForName(String name, int size, int repeat)：根据提供的名称，从映射中找出相应的策略Test，然后执行规模为size的Test#test()测试，每次都得出耗时，然后重复repeat遍，最后计算出单次实验的平均时间。
* Tester#runAll(int size, int repeat) 对映射表中的每一个Test策略都执行规模为size * repeat的测试。

这里`GenericTester`已经不仅仅是一个骨架实现了，它已经是一个可以使用的泛型类。实现了`Tester`接口定义的所有方法。它需要的是往`TESTS`映射表中注册`Test`策略对象。
```java
/**
 * 测试框架：主要利用了Strategy模式和Builder模式，把测试对象某行为的执行效率的辅助平台和测试逻辑剥离开来。
 *          用户只需要把注意力集中在测试逻辑单元的设计上，而把程序性的运行，计时工作交给框架来负责。
 * 测试框架的骨架是Tester<C>和Test<C>两个接口提供的服务，即：
 *     首先，Test是一个无状态对象，用来模拟函数指针（策略模式）。它的策略由它的 test() 方法定义，
 *         1. Test#test(C c, int n)：执行 C 类型对象的一系列方法 n 次，并返回总时间开销。
 *     然后，Tester是一个负责执行Test策略的工具，它内部维护着一个由一组策略和它们的名字组成的映射Map，
 *         2. Tester#runForName(String name, int size, int repeat)：根据提供的名称，从映射中找出相应的策略Test，
 *             然后执行规模为size的Test#test()测试，每次都得出耗时，然后重复repeat遍，最后计算出单次实验的平均时间。
 *         3. Tester#runAll(int size, int repeat) 对映射表中的每一个Test策略都执行规模为size * repeat的测试。
 *
 * 框架的核心组件：
 *     1. Test.java
 *     2. Tester.java
 *     3. Builder.java
 *     4. Generator.java
 *     4. GenericTester.java
 *     5. StringGenerator.java
 *     6. SimpleListTester.java
 *
 */
package com.ciaoshen.thinkinjava.newchapter17;
import java.util.*;

// 实现这个泛型类的饿子类会是不可变类。
// 所有字段都是private final。也没有提供getter或setter访问方法。没有暴露成员域引用，尤其是可变成员域Map的引用。
// 但其实是有隐患的：因为构造器没有进行保护性拷贝，一旦用户保留了参与构造对象的引用，我们Tester还是有可能受到攻击。
// 所幸，我们用TesterBuilder构造Tester的时候，无论是Generator还是Test都是匿名内部类的形式。因此没有暴露引用。
class GenericTester<C> implements Tester<C> {
    private final Map<String,Test<C>> TESTS;
    private final Generator<C> GEN;
    public GenericTester(TesterBuilder<C> builder) {
        GEN = builder.GEN_TO_BUILD;
        TESTS = Collections.unmodifiableMap(builder.TESTS_TO_BUILD);
    }
    public long runForName(String name, int testSize, int repeat) throws NoSuchElementException {
        Test<C> test = TESTS.get(name);
        if (test == null) { throw new NoSuchElementException("No test called " + name + "!"); }
        long time = 0l;
        for (int i = 0; i < repeat; i++) {
            time = time + test.test(GEN.next(), testSize);
        }
        return time/repeat;
    }
    public void runAll(int testSize, int repeat) {
        Formatter f = new Formatter(System.out);
        for (Map.Entry<String,Test<C>> entry : TESTS.entrySet()) {
            long time = runForName(entry.getKey(), testSize, repeat);
            f.format("%15.15s %10d %15d \n", entry.getKey(), testSize, time);
        }
    }
    // 外围类GenericTester的配套Builder，当然是可变的。
    // 使用Builder模式，首先是想保护Tester，使其保持不可变性。
    // 其次，Tester因为内部可以持有很多个Test对象，构造过程比较复杂。Builder让客户端代码更清晰。
    static class TesterBuilder<T> implements Builder<Tester<T>> {
        private final Generator<T> GEN_TO_BUILD;
        private final Map<String,Test<T>> TESTS_TO_BUILD = new HashMap<>();
        public TesterBuilder(Generator<T> gen) { GEN_TO_BUILD = gen; }
        public void addTest(String name, Test<T> test) {
            TESTS_TO_BUILD.put(name, test);
        }
        public Tester<T> build() {
            return new GenericTester<T>(this);
        }
    }
}
```

### SimpleListTester.java
继承了`GenericTester`骨架实现，往注册表里插入了`Test`策略对象。测试框架可以运行了。
```java
package com.ciaoshen.thinkinjava.newchapter17;
import java.util.*;

// 因为使用匿名内部类的形式初始化Tester，无法保留进入Tester内部对象的引用。
// 所以尽管Tester和TesterBuilder都没有保护性拷贝，我们的SimplemListTester最终还是不可变的。
final class SimpleListTester extends GenericTester<List<String>> {
    public SimpleListTester(TesterBuilder<List<String>> builder) {
        super(builder);
    }
    public static void main(String[] args) {
        TesterBuilder<List<String>> testerBuilder = new SimpleListTester.TesterBuilder<List<String>>(new Generator<List<String>>() {
            public List<String> next() {
                return new ArrayList<String>();
            }
        });
        testerBuilder.addTest("Add", new Test<List<String>>() {
            public long test(List<String> list, int times) {
                assert list.isEmpty();
                Generator<String> gen = StringGenerator.newInstance();
                String str = gen.next();
                long start = System.nanoTime();
                for (int i = 0; i < times; i++) {
                    list.add(str);
                }
                long end = System.nanoTime();
                assert list.size() == times;
                return end - start;
            }
        });
        testerBuilder.addTest("AddAll", new Test<List<String>>() {
            public long test(List<String> list, int times) {
                assert list.isEmpty();
                List<String> tempList = new ArrayList<>();
                Generator<String> gen = StringGenerator.newInstance();
                for (int i = 0; i < times; i++) {
                    tempList.add(gen.next());
                }
                long start = System.nanoTime();
                list.addAll(tempList);
                long end = System.nanoTime();
                assert list.size() == times;
                return end - start;
            }
        });
        Tester<List<String>> myTester = testerBuilder.build();
        myTester.runAll(10000,10);
    }
}
```

### StringGenerator.java
这是一个配套`String`生成器。用来生成测试数据。

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

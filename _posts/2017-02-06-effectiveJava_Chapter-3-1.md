---
layout: post
title: "[Effective Java] Note: - Chapter-3-1: How to code equals() and hashCode() method ?"
date: 2017-02-03
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["hash","comparable"]
description: >
---

### equals()的五大通用约定
`equanls()`反映的是实例的逻辑相等(Logical Equality)。根据Java语言规范，“值相等”的等价性（equivalence relation）隐含五大通用约定：
1. 自反性(reflexive)：任何非null的值，`x.equals(x)`必须返回`true`。
2. 对称性：任何非null的值，`y.equals(x)`为真，那么`x.equals(y)`也必须为真。
3. 传递性：对任何非null的值，如果`x.equals(y)`为真，且`y.equals(z)`为真，那么`x.equals(z)`也必须为真。
4. 一致性：对任何非null的值，多次调用`x.equals(y)`必须返回相同的结果。
5. 非空性：所有null的值，`x.equals(null)`必须为`false`。而且不能抛出`NullPointerException`。

### 跨越父类和子类的比较容易打破对称性和传递性
其中 **对称性** 和 **传递性** 最容易出问题。尤其当试图提供跨越类型的`equals()`服务的时候。比如父类和子类混合比较。一个基本定论是：
> 我们无法再扩展可实例化的类的同时，既增加新的值组件，同时又保留`equals()`约定。

下面两个例子展示了两次失败的尝试。第一个例子，`CaseInsensitiveString`表示一种不区分大小写的`String`。下面它的`equals()`方法希望能兼容和普通`String`的比较。但违反了 **对称性**。
```java
public final class CaseInsensitiveString {
    private final String s;
    // ... some code here
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString) {
            return s.equalsIgnoreCase( ( (CaseInsensitiveString)o ).s );
        }
        if (o instanceof String) {
            return s.equalsIgnoreCase( (String)o ) ;
        }
        return false;
    }
}
```
因为虽然`CaseInsensitiveString`能兼容普通`String`，但`String`却不兼容`CaseInsensitiveString`。这就是现实。
```java
public static void main(String[] args) {
    String s = "Hello";
    CaseInsensitiveString cis = new CaseInsensitiveString("hello");
    boolean positive = cis.equals(s); // 为true。因为CaseInsensitiveString兼容String。
    boolean reverse = s.equals(cis); // 为false。因为String不兼容CaseInsensitiveString。    
}
```

第二个例子，`Point`代表有`x`轴和`y`轴坐标的点。`equals()`的比较很简单，依赖点的坐标。
```java
public class Point {
    private final int x;
    private final int y;
    // some code here ...
    public boolean equals(Object o) {
        if (! instanceof Point) {
            return false;
        }
        Point p = (Point)o;
        return x == p.x && y == p.y;
    }
}
```
当我们扩展这个点，增加一个代表颜色信息的域`color`。
```java
public class ColorPoint extends Point {
    private final Color color;
    // reste of the code ...
}
```
如果想让`ColorPoint`也能和普通`Point`进行比较，一种做法是在比较时忽略`color`域的颜色信息。
```java
public boolean equals(Object o) {
    if (! o instance of Point) {
        return false;
    }
    if (! o instanceof ColorPoint) {
        Point p = (Point)o;
        return p.equals(this);
    }
    ColorPoint cp = (ColorPoint)o;
    return super.equals(cp) && color == cp.color;
}
```
但是上面的代码 **违反了传递性**。 因为,
```java
ColorPoint cp1 = new ColorPoint(1,2,Color.RED);
Point p2 = new Point(1,2);
ColorPoint cp3 = new ColorPoint(1,2,Color.BLUE);

// 三个点虽然都有相同坐标，但两个ColorPoint却有不同的颜色。
boolean ab = cp1.equals(p2); // true
boolean bc = p2.equals(cp3); // true
boolean ac = cp1.equals(cp3); // false
```

### 解决的办法

#### 利用"范式（Canonical Form）"进行某些复杂比较
这里的“范式（Canonical Form）”指的是一种去重的标准映射空间。把复杂的对象映射到这个标准空间里再进行比较。比如，如何对`String`按照字母排序（大小写无关）进行比较。`String.CASE_INSENSITIVE_ORDER`这个`Comparator`的源代码如下，它对一个字符最多做了3次比较：
1. 直接比较.
2. 都变成大写比较: 大写成了一种范式。
3. 都变成小写比较: 小写成了一种范式。


```java
public int compare(String s1, String s2) {
    int n1 = s1.length();
    int n2 = s2.length();
    int min = Math.min(n1, n2);
    for (int i = 0; i < min; i++) {
        char c1 = s1.charAt(i);
        char c2 = s2.charAt(i);
        if (c1 != c2) {
            c1 = Character.toUpperCase(c1);
            c2 = Character.toUpperCase(c2);
            if (c1 != c2) {
                c1 = Character.toLowerCase(c1);
                c2 = Character.toLowerCase(c2);
                if (c1 != c2) {
                    // No overflow because of numeric promotion
                    return c1 - c2;
                }
            }
        }
    }
    return n1 - n2;
}
```

### 关于一致性的问题
忠告是： **不要使`equals()`方法用来于不可靠的资源**。

### 实现高质量的equals方法的诀窍
1. 使用`==`先检查两个比较的是否指向同一个对象。如果是，就剩下大量比较的时间。
2. 用`instanceof`检查参数是否为正确的类型。
3. 把参数转换成正确的类型。因为之前已经用`instanceof`检查过了，所以确保不会报错。
4. 对该类中的每一个关键（significant）域，检查参数中的域是否与改对象中对应的域相等。优先比较最容易出错的域。
    1. 不是`float`和`double`的基本型，用`==`判断。
    2. `float`和`double`用`Float.compare`和`Double.compare`比较。因为`Float.NaN`和`-0.0f`以及`Double`中对应的组件需要特殊处理。实际的实现是，他们让`Float.NaN`比任何`float`值都大，让`-0.0f < 0.0f`。
    3. 对象递归调用他们的`equals()`方法。
5. 写测试验证对称性和传递性。
6. 重写`hashCode()`方法。因为`hashCode()`的约定涉及`equals()`负责的等价性。

### 一个合格的equals方法的例子
```java
public class PhoneNumber {
    private final short areaCode;
    private final short prefix;
    private final short lineNumber;
    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; } // 等价性检查。相等直接返回，不比较。
        if (! (o instanceof PhoneNumber) ) { return false; } // 不是PhoneNumber的直接返回false
        PhoneNumber pn = (PhoneNumber)o; // 转型必须成功,前面已经检查过了。
        return pn.areaCode == areaCode && pn.prefix == prefix && pn.lineNumber == lineNumber; // 逐个域进行比较。
    }
    // some other methods
}
```

### hashCode()方法的约定
覆盖了`equals()`方法之后，必须也覆盖`hashCode()`方法，`equals()`判断相等的两个对象，`hashCode()`方法必须返回相同的散列值。Java规范手册中关于`hashCode()`有三条约定，
1. 一致性：同一个对象多次调用`hashCode()`方法，必须返回相同散列值。
2. 等价性：`equals()`方法判断为“值相等”的两个对象，`hashCode()`必须返回相等的散列值。
3. 等价性的补充：`equals()`方法判断为“不相等”的两个对象，不一定必须有不同的散列值。

### 写hashCode()的诀窍
1. 用`int reault = 17;`做内核。一定要有这个内核！如果这个内核为`0`的话，某些散列值为`0`的域将对最后的散列值结果完全没有影响。加上这个内核，就算某一轮加上的散列值为`0`，这个内核也乘了一次`31`，这就和没有这个`0`的结果不同了。
2. 递归计算每个关键域的散列值。
    1. boolean: 计算`(f ? 1 : 0)`。
    2. byte, char, short或int: 计算`(int)f`。
    3. long: 计算`(int)(f^(f >>> 32))`。就是把`long`型的高位32位和低位32位做`^`操作。
    4. float: 计算`Float.floatToIntBits(f)`。
    5. double: 计算`Double.doubleToLongBits(f)`转成`long`型，然后再`(int)(f^(f >>> 32))`转成`int`。
    6. Object: 递归调用`hashCode()`方法。
    7. array: 每个元素计算一个散列值再组合起来。也可以用`Arrays.hashCode()`方法。
3. 按照公式`result = 31 * result + c`把第2步计算得到的每个散列值`c`合并到`result`中。
4. 返回`result`。

### 散列值的缓存，以及延迟初始化技术
如果类不可变，或者很少改变，散列值计算开销又很大，就可以将计算好的散列值缓存到某个域中。并且最好使用"Lazy Initialize"（延迟初始化）技术，在`hashCode()`方法第一次被调用的时候才初始化。

### 一个合格hashCode方法的演示
```java
public class PhoneNumber {
    private final short areaCode;
    private final short prefix;
    private final short lineNumber;
    private int hashCode; // 散列值缓存
    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; } // 等价性检查。相等直接返回，不比较。
        if (! (o instanceof PhoneNumber) ) { return false; } // 不是PhoneNumber的直接返回false
        PhoneNumber pn = (PhoneNumber)o; // 转型必须成功,前面已经检查过了。
        return pn.areaCode == areaCode && pn.prefix == prefix && pn.lineNumber == lineNumber; // 逐个域进行比较。
    }
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) { // 只有在hashCode()方法第一次被调用才初始化hashCode缓存域
            int result = 17;
            result = 31 * result + areaCode;
            result = 31 * result + prefix;
            result = 31 * result + lineNumber;
            hashCode = result;
        }
        return hashCode;
    }
    // some other methods
}
```

### 完整实现书中的`PhoneNumber`的例子
除了`equals()`和`hashCode()`标准化的实现外，还有配套的测试方法。

```java
public class PhoneNumber {
    private static final int MAX_AREACODE = 999;
    private static final int MAX_PREFIX = 999;
    private static final int MAX_LINENUMBER = 9999;
    private final short areaCode;
    private final short prefix;
    private final short lineNumber;
    private volatile int hashCode; // 散列值缓存，使用延迟初始化技术
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

    /**
     * 测试单元
     */
    private static class TestUnit {
        // 测试equals()的对称性
        private static void symmetry(List<PhoneNumber> list) {
            int length = list.size();
            if (length < 2) {
                throw new IllegalArgumentException("Need more than 2 PhoneNumbers in the list!");
            }
            for (int i = 0; i < length-1; i++) {
                PhoneNumber num1 = list.get(i);
                ListIterator<PhoneNumber> ite = list.listIterator(i+1);
                while (ite.hasNext()) {
                    symmetryTwoNumber(num1,ite.next());
                }
            }
            System.out.println("Pass Symmetry Test!");
        }
        private static void symmetryTwoNumber(PhoneNumber num1, PhoneNumber num2) {
            boolean positive = num1.equals(num2);
            boolean reverse = num2.equals(num1);
            String msg = "[" + num1 + "]" + "  &  " + "[" + num2 + "]";
            if (positive == reverse) {
                System.out.println(msg + " ... OK!");
            } else {
                throw new RuntimeException(msg + " : Violates Symmetry!");
            }
        }
        // 测试equals()的传递性
        private static void transitivity(List<PhoneNumber> list) {
            int length = list.size();
            if (length < 3) {
                throw new IllegalArgumentException("List must have more than 3 phone numbers for transitivity() method!");
            }
            for (int i = 0; i < length-2; i++) {
                PhoneNumber num1 = list.get(i);
                ListIterator<PhoneNumber> iteOne = list.listIterator(i+1);
                for (int j = i+1; j < length-1; j++) {
                    PhoneNumber num2 = list.get(j);
                    for (int k = j+1; k < length; k++) {
                        PhoneNumber num3 = list.get(k);
                        transitiveThreeNumber(num1, num2, num3);
                    }
                }
            }
            System.out.println("Pass Transitivity Test!");
        }
        private static void transitiveThreeNumber(PhoneNumber num1, PhoneNumber num2, PhoneNumber num3) {
            String msg = "[" + num1 + "]" + "  &  " + "[" + num2 + "]" + "  &  " + "[" + num3 + "]";
            transitiveThreeNumberEachCase(num1, num2, num3);
            transitiveThreeNumberEachCase(num1, num3, num2);
            transitiveThreeNumberEachCase(num2, num1, num3);
            transitiveThreeNumberEachCase(num2, num3, num1);
            transitiveThreeNumberEachCase(num3, num1, num2);
            transitiveThreeNumberEachCase(num3, num2, num1);
            System.out.println(msg + " ... OK!");
        }
        private static void transitiveThreeNumberEachCase(PhoneNumber num1, PhoneNumber num2, PhoneNumber num3) {
            boolean oneTwo = num1.equals(num2);
            boolean twoThree = num2.equals(num3);
            boolean oneThree = num1.equals(num3);
            String msg = "[" + num1 + "]" + "  &  " + "[" + num2 + "]" + "  &  " + "[" + num3 + "]";
            if (oneThree != ( oneTwo && twoThree ) ) {
                throw new RuntimeException(msg + " : Violates Transitivity!");
            }
        }
        // 测试equals()的一致性
        private static void consistency(List<PhoneNumber> list) {
            int length = list.size();
            if (length < 2) {
                throw new IllegalArgumentException("List for consistency() method need at least 2 phone numbers!");
            }
            for (int i = 0; i < length-1; i++) {
                PhoneNumber num1 = list.get(i);
                for (int j = i+1; j < length; j++) {
                    PhoneNumber num2 = list.get(j);
                    consistentTwoNumber(num1,num2);
                }
            }
            System.out.println("Pass Consistency Test!");
        }
        private static void consistentTwoNumber(PhoneNumber num1, PhoneNumber num2) {
            String msg = "[" + num1 + "]" + "  &  " + "[" + num2 + "]";
            int repeatTimes = 1000;
            boolean result = num1.equals(num2);
            for (int i = 0; i < repeatTimes; i++) {
                if (num1.equals(num2) != result) {
                    throw new RuntimeException(msg + ": Violates Consistency!");
                }
            }
            System.out.println(msg + " ... OK!");
        }
        // 测试equals()
        private static void testEquals(int size) {
            List<PhoneNumber> phoneNumberList = new ArrayList<PhoneNumber>();
            Random r = new Random();
            for (int i = 0; i < size; i++) {
                phoneNumberList.add(new PhoneNumber(r.nextInt(MAX_AREACODE), r.nextInt(MAX_PREFIX), r.nextInt(MAX_LINENUMBER)));
            }
            symmetry(phoneNumberList);
            transitivity(phoneNumberList);
            consistency(phoneNumberList);
        }
        // 测试hashCode()
        private static void testHashCode() {
            Random r = new Random();
            Map<PhoneNumber,String> yellowPage = new HashMap<>();
            int areaCode = r.nextInt(MAX_AREACODE);
            int prefix = r.nextInt(MAX_PREFIX);
            int lineNumber = r.nextInt(MAX_LINENUMBER);
            String myName = "Shen";
            yellowPage.put(new PhoneNumber(areaCode, prefix, lineNumber), myName);
            PhoneNumber sameNumber = new PhoneNumber(areaCode, prefix, lineNumber);
            String gotName = yellowPage.get(sameNumber);
            if (gotName == null || ! gotName.equals("Shen")) {
                throw new RuntimeException("PhoneNumber cannot work with HashMap!");
            } else {
                System.out.println("Pass hashCode() test!");
            }
        }
    }
    public static void main(String[] args) {
        TestUnit.testEquals(10);
        TestUnit.testHashCode();
    }
}
```

---
layout: post
title: "Leetcode - Algorithm - Water And Jug Problem "
date: 2018-08-24 20:29:39
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "medium"
description: >
---

### 题目
You are given two jugs with capacities x and y litres. There is an infinite amount of water supply available. You need to determine whether it is possible to measure exactly z litres using these two jugs.

If z liters of water is measurable, you must have z liters of water contained within one or both buckets by the end.

Operations allowed:
* Fill any of the jugs completely with water.
* Empty any of the jugs.
* Pour water from one jug into another till the other jug is completely full or the first jug itself is empty.

Example 1: (From the famous "Die Hard" example)
```
Input: x = 3, y = 5, z = 4
Output: True
```
Example 2:
```
Input: x = 2, y = 6, z = 5
Output: False
```

### 贝祖等式
这题本质上是个数学问题：[【贝祖等式】](https://zh.wikipedia.org/wiki/貝祖等式).首先需要把题目转化成一个数学模型。

假设x壶容量为`4`，y壶容量为`6`，我们的目标是`8`，那么就有，
```
x = 4, y = 6, z = 8.
```
完成这个目标需要的操作步骤是，
1. 倒满6升的y壶
2. 6升的y壶向4升的x壶里倒水，直到倒满x壶。此时y壶还剩2升水
3. 把x壶里的4升水倒掉
4. 把y壶里剩余的2升倒进x壶
5. 再重新倒满6升的y壶

这样y壶有6升，x壶有2升，一共是8升。这个过程抽象成数学表达就是：
```
6 - 4 + 6
```
转化更一般化的`ax + by = c`的形式（**线性丢番图方程**），
```
-1 * 4 + 2 * 6 = 8
```
这里`a`和`b`代表的就是x或y壶倒入，倒出的次数。负数表示倒出，正数表示倒入。

到这里，就可以联系上贝祖定理了。贝祖等式说的是，

> 对任何整數a,b和c，关于未知数x和y的线性丢番图方程`ax + by = c`有整数解时，当且仅当c是a和b的最大公约数d的整数倍。

还是拿刚才的例子说就是，
> 4x + 6y = c

6和4的最大公约数为2，也就是只有当c是2的整数倍的时候，x和y才有整数解。例子里`c = 8`，确实是2的整数倍，所以有解。

所以最后通俗地讲，
> 这两桶水倒腾来倒腾去，所有可能的容量和都是这两个桶容量最大公约数的倍数。话句话说，这个最大公约数在这里就是一个不可再分解的 **“最小单位”**。

### 最大公倍数（GCD: Greatest Common Divisor）
这里涉及到最大公倍数的计算。最有名的就是欧几里得的 **“辗转相除法”**。 将这个之前，先要确认一个重要的事实就是：**如果a和b的最大公约数是c，那么a除以b的余数一定也是c的整数倍。**

举个例子，`a = 32, b = 14`。他俩最大公约数是`2`。欧几里得就发现`a % b = 4`，也必定是`2`的倍数。

所以辗转相除法就是说，
1. `a % b = d`
2. 如果`d == 0`，则最大公约数就是b
3. 否则，令`a = b`，`b = d`，重复以上步骤

转换成递归的表述就是，
> gcd(a,b)=gcd(b,a mod b)

#### 代码
```java
class Solution {
    public boolean canMeasureWater(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || z > x + y) {
            return false;
        }
        // assert: x,y,c are all valid input
        if (z == 0) {
            return true;
        }
        // special cases: x == 0 || y == 0 && z > 0
        if (x == 0 || y == 0) {
            return x + y == z;
        }
        // assert: a > 0 && b > 0 && z > 0
        return (z % gcd(x, y)) == 0;
    }
    // 计算a和b的最大公约数
    // assert: a > 0 && b > 0
    private int gcd(int a, int b) {
        int mod = a % b;
        if (mod == 0) { return b; }
        return gcd(b, mod);
    }
}
```

#### 结果
![water-and-jug-problem-1](/images/leetcode/water-and-jug-problem-1.png)

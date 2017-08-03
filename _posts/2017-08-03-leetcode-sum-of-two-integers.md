---
layout: post
title: "Leetcode - Algorithm - Sum Of Two Integers "
date: 2017-08-03 16:10:57
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["integer"]
level: "easy"
description: >
---

### 题目
Calculate the sum of two integers a and b, but you are not allowed to use the operator `+` and `-`.

Example:
Given a = `1` and b = `2`, return `3`.

### 老老实实一位一位切下来，再并进结果
这里虽然`int`涉及2的补码。但实际上恰恰因为2的补码，加法操作变得非常简单，只要一位一位做`XOR`异或混合就行。当然不要忘了进位。

#### 代码
```java
public class Solution {
    public int getSum(int a, int b) {
        int result = 0;
        int carry = 0;
        for (int i = 0; i < 32; i++) {
            // 切出下一位
            int x = a & 1; a >>>= 1;
            int y = b & 1; b >>>= 1;
            // 当前位并到结果中
            int thisByte = (x ^ y ^ carry) << i;
            result |= thisByte;
            // 更新进位状态
            int mix = x & y;
            if (mix == 0) { mix = (x | y) & carry; }
            carry = mix;
        }
        return result;
    }
}
```

#### 结果
![sum-of-two-integers-1](/images/leetcode/sum-of-two-integers-1.png)


### 32位同时位操作
上面的方法把每一位切下来，进行一系列位操作。但一位一位切下来太麻烦，可不可以直接对32位的`a`和`b`做位操作呢？是可以的。
1. 第一步： `carry = a & b`得到所有的进位信息。需要进位的位被置为`1`。
2. 第二步： `a ^= b`把`b`累加到`a`上。不用担心进位，因为需要进位的地方已经被标记出来了。
3. 第三步： `b = carry << 1`，进位都是往前进一位。把进位信息加到`b`上，继续累加。
4. 第四步： 当`b == 0`的时候，终止迭代。

演示一下`2-5`（简化为4位2的补码）.
```
a = +2 = 0010
b = -5 = 1011       // -5 = (~5 + 1)
```
计算进位：
```
carry = a ^ b

0010
1011 &
------
0010        // carry
```
把`b`加到`a`上，
```
a = a ^ b

0010
1011 ^
------
1001        // a
```
`b = (carry << 1)`继续累加，
```
a = 1001
b = (carry << 1) = 0100

计算进位:
1001
0100 &
------
0000        // carry

b累加到a:
1001
0100 ^
------
1101        // 结果
```

`1101`做一个`~(x - 1)`操作，相反数是`0011 = 3`。所以`1101 = -3`。所以得出正确结果`2-5=-3`。


#### 迭代代码
```java
public class Solution {
    public int getSum(int a, int b) {
        while (b != 0) {
            int carry = a & b;
            a ^= b;
            b = (carry << 1);
        }
        return a;
    }
}
```

#### 递归版
```java
public class Solution {
    public int getSum(int a, int b) {
        if (b == 0) { return a; }
        return getSum(a^b,(a&b)<<1);
    }
}
```

#### 结果
![sum-of-two-integers-2](/images/leetcode/sum-of-two-integers-2.png)

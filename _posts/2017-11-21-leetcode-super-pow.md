---
layout: post
title: "Leetcode - Algorithm - Super Pow "
date: 2017-11-21 21:38:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "medium"
description: >
---

### 题目
Your task is to calculate ab mod 1337 where a is a positive integer and b is an extremely large positive integer given in the form of an array.

Example1:
```
a = 2
b = [3]

Result: 8
```

Example2:
```
a = 2
b = [1,0]

Result: 1024
```

### 老老实实一个个累乘（先对1337取余）
这样的好处是可以在`Integer`的取值范围内完成。然后中间得到的`[1000次方，100次方，10次方，1次方]`的中间值可以用一个变量缓存起来，重复利用。

#### 代码
```java
class Solution {
    public int superPow(int a, int[] b) {
        int res = 1;
        int base = a % 1337;
        for (int i = b.length - 1; i >= 0; i--) {
            int powerTen = 1; // 重复利用的中间值，[1000次方，100次方，10次方，1次方]
            if (i == (b.length - 1)) {
                powerTen = base;
            } else {
                for (int j = 0; j < 10; j++) {
                    powerTen = (powerTen * base) % 1337;
                }
            }
            int local = 1;
            for (int j = b[i]; j > 0; j--) {
                local = (local * powerTen) % 1337;
            }
            res = (res * local) % 1337;
            base = powerTen;
        }
        return res;
    }
}
```

#### 结果
![super-pow-1](/images/leetcode/super-pow-1.png)

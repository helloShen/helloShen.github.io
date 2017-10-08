---
layout: post
title: "Leetcode - Algorithm - Integer Break "
date: 2017-10-08 15:58:04
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a positive integer n, break it into the sum of at least two positive integers and maximize the product of those integers. Return the maximum product you can get.

For example, given n = 2, return 1 (2 = 1 + 1); given n = 10, return 36 (10 = 3 + 3 + 4).

Note: You may assume that n is not less than 2 and not larger than 58.
### 自底向上的动态规划
递归式如下，
> T(n) = MAX(
            MAX(2,T(2)) * MAX(n-2),T(n-2)),
            MAX(3,T(3)) * MAX(n-3),T(n-3)),
            MAX(4,T(4)) * MAX(n-4),T(n-4)),
            ... ...
            MAX(n/2,T(n/2)) * MAX(n-n/2,T(n-n/2))
        )

需要注意，`[2,3,4]`3个数字是特例，他们的最大积小于他们本身。所以可以将他们预设为基础解。

#### 代码
```java
class Solution {
    public int integerBreak(int n) {
        int[] memo = new int[Math.max(5,n)];
        memo[0] = 1; memo[1] = 1; memo[2] = 2; memo[3] = 4;
        for (int i = 4; i <= n; i++) {
            for (int j = 1; j <= i/2; j++) {
                memo[i-1] = Math.max(memo[i-1],Math.max(j,memo[j-1]) * Math.max(i-j,memo[i-j-1]));
            }
        }
        return memo[n-1];
    }
}
```

#### 结果
![integer-break-1](/images/leetcode/integer-break-1.png)


### 数学规律
仔细观察，可以发现所有分解出来的最优解都是由`2`和`3`两个因数组成，再具体一点，
> 取尽可能多的3，直到余下2时取一个2，余下4时取两个2.

原理是因为两个数因数分解，总是当两个因数相等的时候，积最大。
* `N`是偶数：`max = (N/2)*(N/2)`
* `N`是奇数：`max = (N-1)/2 *(N+1)/2`

但这就导致只要当`(N/2)*(N/2)>=N`的时候，继续往下分解总能得到更优的解。那这个界限在哪儿呢？就是我们上面发现的`4`，
* `(N/2)*(N/2)>=N`, when N>=4
* `(N-1)/2 *(N+1)/2>=N`, when N>=5

所以分解出来的因数不可能超过`4`，其中`1`又没有意义，所以只有两个可能的因数`[2,3]`。

最后为什么尽可能多地取`3`而不是`2`呢？一个直观的例子就是`6`，`3*3 > 2*2*2`，所以`3`更给力那么一点。唯一`2`比`3`强的地方是`2*2 > 1*3`，所以只需要注意在余数为`4`的时候，取`2*2`即可。

所以最后
#### 代码
```java
class Solution {
    public int integerBreak(int n) {
        if (n == 2) { return 1; }
        if (n == 3) { return 2; }
        int product = 1;
        while (n > 4) {
            product *= 3;
            n -= 3;
        }
        return product * n;
    }
}
```

#### 结果
![integer-break-2](/images/leetcode/integer-break-2.png)

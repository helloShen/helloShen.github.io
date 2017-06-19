---
layout: post
title: "Leetcode - Algorithm - Count Primes "
date: 2017-06-17 23:31:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Count the number of prime numbers less than a non-negative number, n.

### 总体思路
> 素数定义：对于自然数`n`，无法被`[2,n-1]`中任何一个自然数整除，它就是素数。

首先，朴素思路是能写出一个`isPrime()`函数，能够验证任何一个给定自然数是否为素数。

根据素数的定义，先确保一个一个做除法验证可以正确工作。可以在 $$O(n)$$ 时间内完成一个数的素数验证。所以，算法的整体复杂度就是 $$O(n^2)$$。

然后，我的思路是看看验证素数方面，有没有什么数学公式可以套用。结果找到 **费马小定理**。 这样可以把一个数的素数验证的复杂度降低到 $$O(\log_{}{n})$$。算法整体复杂度降到 $$O(n\log_{}{n})$$。

最后，应该看看有没有办法能不用每一个数字都验证是不是素数。办法是有的。可以在找到任何一个素数之后，就把这个素数的倍数全部标为合数。

### 老老实实用除法验证. 复杂度 $$O(n^2)$$

#### 代码
```java
public class Solution {
    public int countPrimes(int n) {
        int count = 0;
        for (int i = 2; i < n; i++) { if (isPrime(n)) { count++; } }
        return count;
    }
    public boolean isPrime(int n) {
        for (int i = 2; i < n; i++) {
            if ((n % i) == 0) { return false; }
        }
        return true;
    }
}
```

可以偷一点懒，因为对于`n`，是不可能整除一个`> n/2`的自然数的。比如`n=101`，只需要验证`n`不能被`[1,50]`之内的数整除就行了。

```java
public class Solution {
    public int countPrimes(int n) {
        int count = 0;
        for (int i = 2; i < n; i++) { if (isPrime(n)) { count++; } }
        return count;
    }
    public boolean isPrime(int n) {
        for (int i = 2; i < n/2; i++) { // 检测一半
            if ((n % i) == 0) { return false; }
        }
        return true;
    }
}
```

#### 结果
超时。
![count-primes-1](/images/leetcode/count-primes-1.png)


### 利用费马小定理验证素数
一个数是素数的一个`必要非充分条件`是：
> 当`n`是素数，对于任意一个正整数`a < n`，都有 `a^{n-1} % n = 1` 成立。

注意这只是一个`必要非充分条件`，也就是说，所有素数都满足这个条件，但反过来满足这个条件的数，只是有很大概率是素数，但也不一定是素数。

但如果多做几次这样的验证，是素数的概率就非常接近1. 尤其`int`值的范围有限，这个算法还是可行的。

这里就涉及到`a ^ p % m`的计算。幂计算的结果非常大。但有一个 **蒙哥马利快速幂模算法** 可以在`int`的范围内快速完成`a ^ p % m`的计算。

**蒙哥马利快速幂模算法** 的数学依据是，
> `(x * y) % z = [(x % z) * (y % z)] % z`

所以，
> $$a^{31} \% 10 = (a^{16} * a^8 * a^4 * a^2 * a^1) \% 10$$
$$= (a^{16} \% 10 * a^8 \% 10 * a^4 \% 10 * a^2 \% 10 * a^1 \% 10) \% 10$$

而这里的每一步都可以根据前一步的结果获得，比如`$$a^16 \% 10 = (a^8 \% 10)^2 % 10$$`。

所以 **蒙哥马利快速幂模算法** 代码如下，
```java
/**
 * 蒙哥马利快速幂模算法
 * calculate (n^p)%m
 */
public int montgomery(int n, int p, int m) {
    int ret = 1;
    long nextRemainder = n % m;
    while (p > 0) {
        if ((p & 1) == 1) { ret = (int)((ret * nextRemainder) % m); }
        nextRemainder = (nextRemainder * nextRemainder) % m;
        p >>>= 1;
    }
    return ret;
}
```

#### 代码
利用费马小定理验证素数的完整代码如下，
```java
/**
 * 用费马小定理验证
 */
public class SolutionV2 {
    private final Set<Integer> primes = new HashSet<>(Arrays.asList(new Integer[]{
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
        73, 79, 83, 89, 97 }));
    public int countPrimes(int n) {
        int count = 0;
        for (int i = 2; i < n; i++) {
            if (isPrime(i)) { count++; }
        }
        return count;
    }
    public boolean isPrime(int n) {
        if (n < 2) { return false; }
        if (n < 100) { return primes.contains(n); }
        for (int prime : primes) {
            if ((n % prime) == 0) { return false; }
        }
        for (int i = 2; i < 12; i++) { // 30次费马测试
            if (!isFermat(n,i)) { return false; }
        }
        return true; // 至此，确定是素数
    }
    /**
     * 依据费马小定理检查: a是一个素数，
     * 如果 [a^(p-1)] % m = 1，则 m 有很大可能是一个素数。
     */
    public boolean isFermat(int p, int a) {
        return (montgomery(a,p-1,p) == 1);
    }
    /**
     * 蒙哥马利快速幂模算法
     * calculate (n^p)%m
     */
    public int montgomery(int n, int p, int m) {
        int ret = 1;
        long nextRemainder = n % m;
        while (p > 0) {
            if ((p & 1) == 1) { ret = (int)((ret * nextRemainder) % m); }
            nextRemainder = (nextRemainder * nextRemainder) % m;
            p >>>= 1;
        }
        return ret;
    }
}
```

#### 结果
超时！
![count-primes-2](/images/leetcode/count-primes-2.png)


### 每找到一个素数，就把它的倍数全标为合数


#### 代码
```java
public class Solution {
    public int countPrimes(int n) {
        int count = 0;
        boolean[] notPrime = new boolean[n];
        for (int i = 2; i < n; i++) {
            if (!notPrime[i]) {
                count++;
                for (int j = i*2; j < n; j += i) {
                    notPrime[j] = true;
                }
            }
        }
        return count;
    }
}
```

可以有个小优化，比如我们验证`17`的时候，
```
2 : 2*2, 2*3, 2*4, 2*5, 2*6, 2*7, 2*8
3 : 3*2, 3*3, 3*4, 3*5
--------------------------------------  // 下面的5和7都不需要验证
5 : 5*2,5*3
7 : 7*2
11: no
13: no
```

```java
public class Solution {
    public int countPrimes(int n) {
        int count = 0;
        boolean[] notPrime = new boolean[n];
        for (int i = 2; i < n; i++) {
            if (!notPrime[i]) {
                count++;
                if (i * i < n) {
                    for (int j = i*2; j < n; j += i) {
                        notPrime[j] = true;
                    }
                }
            }
        }
        return count;
    }
}
```

#### 结果
![count-primes-3](/images/leetcode/count-primes-3.png)

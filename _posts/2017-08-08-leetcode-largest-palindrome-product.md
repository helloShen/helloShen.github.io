---
layout: post
title: "Leetcode - Algorithm - Largest Palindrome Product "
date: 2017-08-08 18:40:05
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 主要收获
> 代码一定要写干净。要确切知道代码执行的每一个细节。一个数字都不能差。

比如这题在确定因式分解上下界范围的时候，要特别小心，连开闭区间都必须搞清楚。
```java
for (int i = max; i >= (int)Math.ceil(Math.sqrt(palindrome)); i--) {
    // some code here ...
}
```
当回文等于`99000099`，上面这个区间为`[9950,9999]`，前后都是闭区间。

当回文等于`121`，上面这个区间为`[11,99]`，前后都是闭区间。

### 题目
Find the largest palindrome made from the product of two n-digit numbers.

Since the result could be very large, you should return the largest palindrome mod 1337.

Example:

Input: 2

Output: 987

Explanation: 99 x 91 = 9009, 9009 % 1337 = 987

Note:

The range of n is [1,8].

### 主要思路
这题有两个关键点，
1. 回文的分布很稀疏。绝大部分的数不是回文。所以与其判断每个数是不是回文，不如主动构造每一个回文来得快。
2. 有了回文，需要做因式分解。怎么减少因式分解的测试空间？测试要抓住一个上界和一个下界，
    1. 上界：$$(10^n)-1$$ 。 比如，`n=3`，`num < 999`。需要两个长度为`3`的数相乘，这是题目规定的。
    2. 下界：$$ceil(sqrt(n))$$。任何数因式分解都应该以它的平方根为界。要么测试[1,ceil(sqrt(n))]，要么测试[ceil(sqrt(n))+1,n]。因为`12=3*4`，也有`12=4*3`，两个对称的解是一回事。所以要么测试`[1,3]`,要么测试`[4,12]`。一般都是选`[1,3]`。但这里我们选另一半`[4,12]`，因为前面题目已经给定了上界。

举个例子，假设`n = 4`，对于回文`99000099`做因式分解，四位数`[1000,9999]`没问题。

```
sqrt(99000099) = 9949.88
ceil(9949.88) = 9950

要么测小的一半: [1,9949]
要么测大的一半: [9950,99000099]

如果测小的一半，测试空间为：[1000,9949]
如果测大的一半，测试空间为：[9950,9999]
```
所以肯定是测试大的一半`[9950,9999]`比较划算。

### 最笨，最直观的方法，做乘法，测试是否是回文

#### 代码
```java
public int largestPalindrome(int n) {
    int max = (int)Math.pow(10,n) - 1;
    int min = max / 10 * 9;
    long result = 0;
    for (long i = max; i >= min; i--) {
        for (long j = max; j >= min; j--) {
            long product = i * j;
            if (product > result && isPalindrome(product)) {
                result = Math.max(result,product);
            }
        }
    }
    return (int)(result % 1337);
}
private boolean isPalindrome(long num) {
    String val = String.valueOf(num);
    int lo = 0, hi = val.length()-1;
    while (lo < hi) {
        if (val.charAt(lo++) != val.charAt(hi--)) { return false; }
    }
    return true;
}
```

这个解法复杂度是很恐怖的。

### 自己生成回文，然后在特定范围内因式分解
做了开头描述的两个关键优化。这里生成回文的方法，是利用了`StringBuilder.reverse()`函数，做拼接。

#### 代码
```java
public class Solution {

    private int max = 0; // ex: n=3; max=999;
    private int half = max;

    public int largestPalindrome(int n) {
        if (n == 1) { return 9; }
        // init
        max = (int)Math.pow(10,n) - 1; half = max;
        hasNextPalindrome = true;
        // iterate each palindrome
        long palindrome = nextPalindrome();
        while (palindrome != 0) {
            // ex: sqrt(9999) = 99.5, so i >= 100
            // ex: sqrt(121) = 11, so i >= 11
            // use ceil() to treat the special case such as sqrt(121) = 11
            for (int i = max; i >= (int)Math.ceil(Math.sqrt(palindrome)); i--) {
                if (palindrome % i == 0) { return (int)(palindrome % 1337); }
            }
            palindrome = nextPalindrome();
        }
        return 0;
    }

    private boolean hasNextPalindrome = true; // ex: true=[999,100]; false=[99,0]

    private long nextPalindrome() {
        long result = 0L;
        if (hasNextPalindrome) {
            String str = String.valueOf(half);
            StringBuilder sb = new StringBuilder(str);
            result = Long.parseLong(str + (sb.reverse().toString()));
            --half;
            if (max / half > 9) { hasNextPalindrome = false; }
        }
        return result;
    }

}
```

#### 结果
![largest-palindrome-product-1](/images/leetcode/largest-palindrome-product-1.png)


### 直接用数组生成回文
还是做了开头描述的两个关键优化，生成回文，以及在特定范围内做因式分解。这里换了一种方法产生回文。

#### 代码
```java
public class Solution {

    private int max = 0; // ex: n=3; max=999;
    private int half = 0;

    public int largestPalindrome(int n) {
        if (n == 1) { return 9; }
        // init
        len = n * 2;
        lo = n - 1; hi = n;
        number = new int[len];
        hasNext = true; // ex: true=[999,100]; false=[99,0]
        Arrays.fill(number,9);
        max = (int)Math.pow(10,n) - 1; half = max;
        // iterate each palindrome
        long palindrome = nextPalindrome();
        while (palindrome != 0) {
            for (int i = max; i >= (int)Math.ceil(Math.sqrt(palindrome)); i--) { // ex: palindrome = 9999, i is empty:[100,99]
                if (palindrome % i == 0) { return (int)(palindrome % 1337); }
            }
            palindrome = nextPalindrome();
        }
        return 0;
    }

    // para for palindrome
    private int len = 0;
    private int lo = 0, hi = 0;
    private int[] number = new int[0];
    private boolean hasNext = false;

    // generate next palindrome
    private long nextPalindrome() {
        // translate next palindrome to int
        long next = 0;
        if (hasNext) {
            for (int i : number) {
                next *= 10;
                next += i;
            }
        }
        // generate next palindrome
        for (int i = lo, j = hi; i >= 0; i--,j++) {
            if (number[i] == 0) {
                number[i] = 9; number[j] = 9;
            } else {
                number[i]--; number[j]--; break;
            }
        }
        if (number[0] == 0) { hasNext = false; }
        return next;
    }
}
```

#### 结果
![largest-palindrome-product-2](/images/leetcode/largest-palindrome-product-2.png)

### 最后生气的时候可以用这个方法泄愤

#### 代码
```java
public class Solution {
        public int largestPalindrome(int n) {
            switch (n) {
                case 1: return 9;
                case 2: return 987;
                case 3: return 123;
                case 4: return 597;
                case 5: return 677;
                case 6: return 1218;
                case 7: return 877;
                case 8: return 475;
                default: return 0;
            }
        }
}
```

#### 结果
![largest-palindrome-product-3](/images/leetcode/largest-palindrome-product-3.png)

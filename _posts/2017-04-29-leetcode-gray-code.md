---
layout: post
title: "Leetcode - Algorithm - Gray Code "
date: 2017-04-29 18:08:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking"]
level: "medium"
description: >
---

### 题目
The gray code is a binary numeral system where two successive values differ in only one bit.

Given a non-negative integer n representing the total number of bits in the code, print the sequence of gray code. A gray code sequence must begin with 0.

For example, given n = `2`, return `[0,1,3,2]`. Its gray code sequence is:
```
00 - 0
01 - 1
11 - 3
10 - 2
```
Note:
For a given n, a gray code sequence is not uniquely defined.

For example, `[0,2,3,1]` is also a valid gray code sequence according to the above definition.

For now, the judge is able to judge based on one instance of gray code sequence. Sorry about that.

### 暴力回溯
暂时没有更好的办法的时候，暴力回溯总是能解决问题。只不过复杂度有点高。$$n^n$$。

#### 代码
```java
public class Solution {
    public List<Integer> grayCode(int n) {
        if (n == 0) { return new ArrayList<Integer>(Arrays.asList(new Integer[]{0})); }
        List<Integer> res = new ArrayList<>();
        recursive(res,n,0);
        return res;
    }
    public boolean recursive(List<Integer> res, int n, int num) {
        if (res.size() == (int)Math.pow(2.0,(double)n)) { return true; }
        if (res.contains(num)) { return false; }
        res.add(num);
        int mask = 1;
        for (int i = 0; i < n; i++) {
            int variant = num ^ mask; // 逐位取补码
            if (recursive(res,n,variant)) { return true; }
            mask = mask << 1;
        }
        res.remove(res.size()-1); // 失败回退
        return false;
    }
}
```

#### 结果
![gray-code-1](/images/leetcode/gray-code-1.png)


### 自底向上的动态规划
观察打印出来的结果，可以发现一个规律，每次`n`加一，**前半部分都不变，后半部分以前半部分为镜像，对称拷贝，然后每次都加上$$2^{n-1}$$**。
```
[0]
[0, 1]
[0, 1, 3, 2]
[0, 1, 3, 2, 6, 7, 5, 4]
[0, 1, 3, 2, 6, 7, 5, 4, 12, 13, 15, 14, 10, 11, 9, 8]
```
把二进制写出来，就更明显了，`n=2`时，
```
0000
0001
0011
0010
```
`n=3`时，以`n=2`的最后一个`0010`对称展开，然后在第`2`位都变成`1`。
```
0000 #   
0001 ##
0011 ###
0010 ####
0110 ####
0111 ###
0101 ##
0100 #
 |
镜像复制，第2位变1
```

所以很容易用`f(n) = do something to f(n-1)`递归完成。

#### 代码
```java
public class Solution {
    public List<Integer> grayCode(int n) {
        if (n == 0) { return new ArrayList<Integer>(Arrays.asList(new Integer[]{0})); }
        List<Integer> res = grayCode(n-1);
        Deque<Integer> mirror = new LinkedList<>();
        int mask = 1 << n-1;
        for (int i : res) {
            mirror.offerFirst(i ^ mask);
        }
        res.addAll(mirror);
        return res;
    }
}
```

#### 结果
银弹！
![gray-code-2](/images/leetcode/gray-code-2.png)

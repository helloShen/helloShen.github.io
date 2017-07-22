---
layout: post
title: "Leetcode - Algorithm - Strobogrammatic Number Two "
date: 2017-07-21 17:55:39
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","recursion"]
level: "medium"
description: >
---

### 题目
A strobogrammatic number is a number that looks the same when rotated 180 degrees (looked at upside down).

Find all strobogrammatic numbers that are of length = n.

For example,
Given n = `2`, return `["11","69","88","96"]`.

### 主要思路
构成对称数的主要数字还是`0`,`1`,`8`,`6`,`9`。如果用迭代，`for`循环套嵌的层数和数字的长度成正比。这种情况就很适合用递归。

### 递归从中间插入
比如`1618191`, 逐渐往中间插入成对的数字。
```
11 -> 1691 -> 161191 -> 1618191
```
这样做的好处是往中间插入的成对的数字组合是有限的。很容易用一个数组提前给出。

从过程来看，这是一个标准的 **回溯算法**，只不过因为字符串是不可变对象，所以每次都给出一个新的字符串对象，而不用每次都擦去部分内容进入下次递归。


#### 代码
```java
public class Solution {
    private String[] head = new String[]{"11","88","69","96"};
    private String[] normal = new String[]{"00","11","88","69","96"};
    private String[] oddMid = new String[]{"0","1","8"};
    public List<String> findStrobogrammatic(int n) {
        List<String> result = new ArrayList<>();
        if (n <= 0) { return result; }
        boolean isOdd = ((n % 2) != 0);
        int half = (n - 1) / 2;
        backtracking(0,half,isOdd,"",result);
        return result;
    }
    public void backtracking(int pos, int half, boolean isOdd, String num, List<String> result) {
        String[] candidates = normal;
        if ((pos == half) && isOdd) {
            candidates = oddMid;
        } else if (pos == 0) {
            candidates = head;
        }
        for (String c : candidates) {
            String newStr = num.substring(0,pos) + c + num.substring(pos);
            if (pos == half) {
                result.add(newStr);
            } else {
                backtracking(pos+1,half,isOdd,newStr,result);
            }
        }
    }
}
```

#### 结果
![strobogrammatic-number-two-1](/images/leetcode/strobogrammatic-number-two-1.png)


### 从一个核心开始逐渐向外扩展（递归版）
从这个角度看，是一个非常标准的递归过程：**递出去，再收归回来。** 递出去的时候是剥洋葱，不到`base case`就不处理。遇到`base case`就返回空心（双核）或者一个单核。归回来的时候，每次都在核心的两边包上一层对称数字。

#### 代码
需要注意的一个细节是要用`LinkedList`而不是`ArrayList`。因为`ArrayList`的`remove()`操作开销很大。
```java
public class Solution {
    public List<String> findStrobogrammatic(int n) {
        return recursion(n,n);
    }
    public List<String> recursion(int size, int maxSize) {
        // base case (don't use ArrayList, because remove() of ArrayList is expansive)
        if (size == 0) { return new LinkedList<String>(Arrays.asList(new String[]{""})); }
        if (size == 1) { return new LinkedList<String>(Arrays.asList(new String[]{"0","1","8"})); }
        // recursion
        List<String> cores = recursion(size-2,maxSize);
        int coreLen = cores.size();
        for (int i = 0; i < coreLen; i++) {
            String core = cores.remove(0);
            if (size != maxSize) { cores.add("0" + core + "0"); }
            cores.add("1" + core + "1");
            cores.add("8" + core + "8");
            cores.add("6" + core + "9");
            cores.add("9" + core + "6");
        }
        return cores;
    }
}
```

#### 结果
![strobogrammatic-number-two-2](/images/leetcode/strobogrammatic-number-two-2.png)


### 翻译成迭代版

#### 代码
```java
public class Solution {
        public List<String> findStrobogrammatic(int n) {
            List<String> result = new LinkedList<>(); // don't use ArrayList, because remove() of ArrayList is expansive
            int coreSize = n % 2;
            String[] core = (coreSize == 0)? new String[]{""} : new String[]{"0","1","8"};
            result.addAll(Arrays.asList(core));
            for (int size = coreSize+2; size <= n; size+=2) {
                int len = result.size();
                for (int i = 0; i < len; i++) {
                    String str = result.remove(0);
                    if (size != n) { result.add("0" + str + "0"); }
                    result.add("1" + str + "1");
                    result.add("8" + str + "8");
                    result.add("6" + str + "9");
                    result.add("9" + str + "6");
                }
            }
            return result;
        }
}
```

#### 结果
![strobogrammatic-number-two-3](/images/leetcode/strobogrammatic-number-two-3.png)

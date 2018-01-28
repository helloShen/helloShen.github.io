---
layout: post
title: "Leetcode - Algorithm - H Index Two "
date: 2018-01-27 19:33:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "medium"
description: >
---

### 题目

##### h-index原题
Given an array of citations (each citation is a non-negative integer) of a researcher, write a function to compute the researcher's h-index.

According to the definition of h-index on Wikipedia: "A scientist has index h if h of his/her N papers have at least h citations each, and the other N − h papers have no more than h citations each."

For example, given citations = [3, 0, 6, 1, 5], which means the researcher has 5 papers in total and each of them had received 3, 0, 6, 1, 5 citations respectively. Since the researcher has 3 papers with at least 3 citations each and the remaining two with no more than 3 citations each, his h-index is 3.

Note: If there are several possible values for h, the maximum one is taken as the h-index.

##### 本题在原题基础上进一步提问
Follow up for H-Index: What if the citations array is sorted in ascending order? Could you optimize your algorithm?

### 朴素解法，$$O(n)$$
假设是乱序的数组:`[6,3,1,0,5]`，算法在<H Index>这题里解释过了，->
<http://www.ciaoshen.com/algorithm/leetcode/2017/12/14/leetcode-h-index.html>

如果数组已经排过序:`[0,1,3,5,6]`，问题会变的更简单，从左往右遍历数组，
```
有1篇文章至少有6个引用
有2篇文章至少有5个引用
有3篇文章至少有3个引用 <- 临界点在这里
有4篇文章至少有1个引用
有5片文章至少有0个引用
```

#### 代码
```java
class Solution {
    public int hIndex(int[] citations) {
        for (int i = 0, j = citations.length; i < citations.length; i++, j--) {
            if (j <= citations[i]) { return j; }
        }
        return 0;
    }
}
```

#### 结果
![h-index-two-1](/images/leetcode/h-index-two-1.png)


### 二分法，$$O(\log_{}{n})$$
再仔细看`[0,1,3,5,6]`这个例子，真的需要依次遍历数组元素吗？其实不需要。

只需要看一个数，就可以确定目标临界点在他的左边还是右边。
```
 1 < 4，所以目标临界点一定在右边   
  |
0,1,3,5,6
  |-----|
  缩小范围
```
```
 5 > 2，所以目标临界点不可能在右边
      |
0,1,3,5,6
|-----|
缩小范围
```

这就是最典型的二分查找的情况。

#### 代码
```java
class Solution {
    public int hIndex(int[] citations) {
        int len = citations.length;
        int lo = 0, hi = len-1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (citations[mid] < (len - mid)) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return len - lo;
    }
}
```

#### 结果
![h-index-two-2](/images/leetcode/h-index-two-2.png)

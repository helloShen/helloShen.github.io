---
layout: post
title: "Leetcode - Algorithm - Pascals Triangle Two "
date: 2017-05-14 19:26:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming"]
level: "easy"
description: >
---

### 题目
Given an index `k`, return the `kth` row of the Pascal's triangle.

For example, given `k = 3`,
Return `[1,3,3,1]`.

Note:
Could you optimize your algorithm to use only `O(k)` extra space?

### 自底向上动态规划
> $$f_{n}(k) = f_{n-1}(k-1) + f_{n-1}(k)$$

需要注意，每次`p[k-1] + p[k]`的得到的结果不能直接写入，需要缓存一下。比如，`[1,3,3,1]`，
```
1,  3,  3,  1
```
当计算出`1+3=4`时，不能用`4`直接替换第一个`3`，因为这个`3`还要用来计算下一步`3+3=6`。需要计算完这一步才把`3`改成`4`。

#### 代码
```java
public class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> res = new ArrayList<>();
        res.add(1);
        for (int i = 0; i < rowIndex; i++) {
            int last = 0;
            for (int j = 1; j < res.size(); j++) {
                int temp = res.get(j-1)+res.get(j);
                if (last > 0) { res.set(j-1,last); }
                last = temp;
            }
            if (last > 0) { res.set(res.size()-1,last); }
            res.add(1);
        }
        return res;
    }
}
```

#### 结果
![pascals-triangle-two-1](/images/leetcode/pascals-triangle-two-1.png)


### 用数组来做，最后返回结果的时候转换成`List`
纯粹为了提高效率。
```
(1)             i = length-1 = 0
1,(1)           i = length-1 = 1
  \|
1, 2, (1)       i = length-1 = 2, j=[1,i-1=1]
  \| \ |
1, 3,  3, (1)   i = length-1 = 3, j=[1,i-1=2]
```

#### 代码
```java
/**
 * i等于最后数组的length-1。
 * j=[1,i-1]。 所以最初[1]和[1,1]的时候，中间j-loop被跳过。
 */
public class Solution {
    public List<Integer> getRow(int rowIndex) {
        int[] p = new int[rowIndex+1];
        for (int i = 0; i <= rowIndex; i++) {
            int memo = 0;
            for (int j = 1; j < i; j++) {
                int temp = p[j-1] + p[j];
                if (memo > 0) { p[j-1] = memo; }
                memo = temp;
            }
            if (memo > 0) { p[i-1] = memo; }
            p[i] = 1;
        }
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < p.length; i++) {
            res.add(p[i]);
        }
        return res;
    }
}
```

#### 结果
![pascals-triangle-two-2](/images/leetcode/pascals-triangle-two-2.png)

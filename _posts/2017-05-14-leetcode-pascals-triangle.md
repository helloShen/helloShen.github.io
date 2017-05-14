---
layout: post
title: "Leetcode - Algorithm - Pascals Triangle "
date: 2017-05-14 18:27:59
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","array"]
level: "easy"
description: >
---

### 题目
Given numRows, generate the first numRows of Pascal's triangle.

For example, given numRows = 5,
Return
```
[
     [1],
    [1,1],
   [1,2,1],
  [1,3,3,1],
 [1,4,6,4,1]
]
```

### 自底向上动态规划
> $$f_{n}(k) = f_{n-1}(k-1) + f_{n-1}(k)$$

#### 递归版
```java
public class Solution {
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (numRows == 0) { return res; }
        res.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{1})));
        recursion(numRows-1,res);
        return res;
    }
    public void recursion(int numRows, List<List<Integer>> res) {
        if (numRows == 0) { return; }
        List<Integer> last = res.get(res.size()-1);
        int size = last.size();
        List<Integer> newList = new ArrayList<>();
        for (int i = 0; i <= size; i++) {
            int one = (i == 0)? 0 : last.get(i-1);
            int two = (i == size)? 0 : last.get(i);
            newList.add(one+two);
        }
        res.add(newList);
        recursion(numRows-1,res);
    }
}
```

#### 迭代版
```java
public class Solution {
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (numRows == 0) { return res; }
        res.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{1})));
        while (--numRows > 0) {
            List<Integer> last = res.get(res.size()-1);
            int size = last.size();
            List<Integer> newList = new ArrayList<>();
            for (int i = 0; i <= size; i++) {
                int one = (i == 0)? 0 : last.get(i-1);
                int two = (i == size)? 0 : last.get(i);
                newList.add(one+two);
            }
            res.add(newList);
        }
        return res;
    }
}
```

#### 结果
![pascals-triangle-1](/images/leetcode/pascals-triangle-1.png)

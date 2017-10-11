---
layout: post
title: "Leetcode - Algorithm - Nested List Weight Sum "
date: 2017-10-11 15:11:53
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given a nested list of integers, return the sum of all integers in the list weighted by their depth.

Each element is either an integer, or a list -- whose elements may also be integers or other lists.

Example 1:
Given the list `[[1,1],2,[1,1]]`, return 10. (four 1's at depth 2, one 2 at depth 1)

Example 2:
Given the list `[1,[4,[6]]]`, return 27. (one 1 at depth 1, one 4 at depth 2, and one 6 at depth 3; 1 + 4*2 + 6*3 = 27)

### 就是一个简单的递归
用一个参数`depth`记录每次递归的深度。

#### 代码
```java
public class Solution {
    public int depthSum(List<NestedInteger> nestedList) {
        return helper(nestedList,1);
    }
    public int helper(List<NestedInteger> nestedList, int depth) {
        int sum = 0;
        for (NestedInteger ni : nestedList) {
            if (ni.isInteger()) {
                sum += ni.getInteger() * depth;
            } else {
                sum += helper(ni.getList(),depth+1);
            }
        }
        return sum;
    }
}
```

#### 结果
![nested-list-weight-sum-1](/images/leetcode/nested-list-weight-sum-1.png)

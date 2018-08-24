---
layout: post
title: "Leetcode - Algorithm - Nested List Weight Sum Two "
date: 2018-08-24 12:09:29
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["breath first search","depth first search"]
level: "medium"
description: >
---

### 题目
Given a nested list of integers, return the sum of all integers in the list weighted by their depth.

Each element is either an integer, or a list -- whose elements may also be integers or other lists.

Different from the previous question where weight is increasing from root to leaf, now the weight is defined from bottom up. i.e., the leaf level integers have weight 1, and the root level integers have the largest weight.

Example 1:
```
Given the list [[1,1],2,[1,1]], return 8. (four 1's at depth 1, one 2 at depth 2)
```

Example 2:
```
Given the list [1,[4,[6]]], return 17. (one 1 at depth 3, one 4 at depth 2, and one 6 at depth 1; 1*3 + 4*2 + 6*1 = 17)
```


### 两次遍历，先得到最大深度，再统筹计算
这题和<Nested Weight Sum 1>的区别是深度的定义是倒过来的，最内层套嵌的深度为1，越往外越大。
![nested-list-weight-sum-two-a](/images/leetcode/nested-list-weight-sum-two-a.png)

这导致一个问题：**每一层的深度不但取决于它自身的位置，还取决于全局最大深度。** 深度计算公式如下，
> depth = max_depth + 1 - height

其中height代表从最外层开始计算的套嵌层数。最外层height记为1。

所以普通的DFS遍历在找到每个“叶节点（isInteger() == true)”的时候还不知道全局最大深度。所以需要遍历两次，第一遍先探出全局最大深度，第二遍再统筹计算。

#### 代码
```java
class Solution {

    public int depthSumInverse(List<NestedInteger> nestedList) {
        if (nestedList == null) { return 0; }
        NestedInteger root = new NestedInteger();
        for (NestedInteger num : nestedList) {
            root.add(num);
        }
        int depth = depth(root);
        return helper(root, depth);
    }

    private int depth(NestedInteger root) {
        // 空节点深度为0
        if (root == null) {
            return 0;
        }
        // 任何不为空的节点起点都是1
        int depth = 1;
        // 如果内部还套嵌了节点，就在所有套嵌节点最大值的基础上再加一
        if (!root.isInteger()) {
            for (NestedInteger sub : root.getList()) {
                if (!sub.isInteger()) {
                    depth = Math.max(depth, depth(sub) + 1);
                }
            }
        }
        return depth;
    }
    // return the [sum, depth] pair of the sub NestedInteger
    private int helper(NestedInteger root, int depth) {
        if (root == null) { return 0; }
        // System.out.println("Now depth = " + depth);
        if (root.isInteger()) {
            return depth * root.getInteger();
        }
        int sum = 0;
        List<NestedInteger> list = root.getList();
        if (list != null) {
            for (NestedInteger num : list) {
                if (num.isInteger()) {
                    // System.out.println("Result + " + num.getInteger());
                    sum += depth * num.getInteger();
                } else {
                    // System.out.println("Sub Integer = " + helper(num, depth - 1));
                    sum += helper(num, depth - 1);
                }
            }
        }
        return sum;
    }
}
```

#### 结果
![nested-list-weight-sum-two-1](/images/leetcode/nested-list-weight-sum-two-1.png)


### BFS先按层求和，最后统一乘以权值
另一种思路是每层不知道实际深度的情况下，只求本层实际数字的和。最后拿到全局深度之后，再统一乘以深度权值。
```
[[2,[1],2],3,[2,[1],2]]

     1           1         -> 本层和 = 2，高度 = 3，深度 = 3 + 1 - 3 = 1
  2     2     2     2      -> 本层和 = 8，高度 = 2，深度 = 3 + 1 - 2 = 2
           3               -> 本层和 = 1，高度 = 1，深度 = 3 + 1 - 1 = 3
```

#### 代码
```java
class Solution {
    public int depthSumInverse(List<NestedInteger> nestedList) {
        if (nestedList == null) { return 0; } // defense
        int depth = 0;
        int sum = 0;
        Map<Integer, Integer> levelSum = new HashMap<>();
        while (!nestedList.isEmpty()) {
            depth++;
            sum = 0;
            int size = nestedList.size();
            for (int i = 0; i < size; i++) {
                NestedInteger node = nestedList.remove(0);
                if (node.isInteger()) {
                    sum += node.getInteger();
                } else {
                    nestedList.addAll(node.getList());
                }
            }
            levelSum.put(depth, sum); // 统计每层未加权的数字和
        }
        depth++;
        int result = 0;
        for (Map.Entry<Integer, Integer> pair : levelSum.entrySet()) {
            result += (depth - pair.getKey()) * pair.getValue();
        }
        return result;
    }
}
```

#### 结果
![nested-list-weight-sum-two-2](/images/leetcode/nested-list-weight-sum-two-2.png)


### 累加法（不求最大深度，不做乘法）
感谢 [【StefanPochmann的累加法】](https://leetcode.com/problems/nested-list-weight-sum-ii/discuss/83641/No-depth-variable-no-multiplication) 。 这个解法棒棒哒。

原理是每一层，都将：**本层数字和** + **下面所有层数字和** 累加一遍。
```
[[2,[1],2],3,[2,[1],2]]

     1           1         -> 本层和 = 2，累积和 = 2 + 9 = 11
  2     2     2     2      -> 本层和 = 8，累积和 = 1 + 8 = 9
           3               -> 本层和 = 1，累计和 = 1
```

#### 代码
```java
class Solution {
    public int depthSumInverse(List<NestedInteger> nestedList) {
        if (nestedList == null) { return 0; }
        int sum = 0;
        int toAdd = 0;
        while (!nestedList.isEmpty()) {
            int size = nestedList.size();
            for (int i = 0; i < size; i++) {
                NestedInteger node = nestedList.remove(0);
                if (node != null) {
                    if (node.isInteger()) {
                        toAdd += node.getInteger();
                    } else {
                        nestedList.addAll(node.getList());
                    }
                }
            }
            sum += toAdd;
        }
        return sum;
    }
}
```

#### 结果
![nested-list-weight-sum-two-3](/images/leetcode/nested-list-weight-sum-two-3.png)

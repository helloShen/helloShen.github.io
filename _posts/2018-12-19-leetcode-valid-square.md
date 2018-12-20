---
layout: post
title: "Leetcode - Algorithm - Valid Square "
date: 2018-12-19 20:12:52
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "medium"
description: >
---

### 题目
Given the coordinates of four points in 2D space, return whether the four points could construct a square.

The coordinate `(x,y)` of a point is represented by an integer array with two integers.

Example:
```
Input: p1 = [0,0], p2 = [1,1], p3 = [1,0], p4 = [0,1]
Output: True
```

Note:
* All the input integers are in the range [-10000, 10000].
* A valid square has four equal sides with positive length and four equal angles (90-degree angles).
* Input points have no order.

### 计算边长，以及对角线长
考虑下面这个正方形，对于点`b`，假设边长`ba = bd = 1`，那么对角线`bc = 根号2`。并且对于任意一个点都是这样。
```
       a
     / | \
    b--+--c
     \ | /
       d
```

#### 代码
根据上面的逻辑，直观地写代码，如下，

```java
class Solution {
    public boolean validSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        int[][] matrix = new int[][]{p1, p2, p3, p4};
        int globalEdgeSquare = -1, globalCrossSquare = -1;
        for (int i = 0; i < 4; i++) {
            int[] point = matrix[i];
            int edgeSquare = -1, crossSquare = -1;
            for (int j = 0; j < 4; j++) {
                if (j == i) continue;
                int len = (int) (Math.pow(point[0] - matrix[j][0], 2) + Math.pow(point[1] - matrix[j][1], 2));
                if (len == 0) return false;
                if (edgeSquare < 0) {
                    edgeSquare = len;
                } else if (edgeSquare < len) {
                    crossSquare = len;
                } else if (edgeSquare > len) {
                    crossSquare = edgeSquare;
                    edgeSquare = len;
                }
            }
            if (crossSquare != 2 * edgeSquare) return false;
            if (globalEdgeSquare < 0) {
                globalEdgeSquare = edgeSquare;
                globalCrossSquare = crossSquare;
            } else {
                if (edgeSquare != globalEdgeSquare || crossSquare != globalCrossSquare) return false;
            }
        }
        return true;
    }
}
```

#### 结果
![valid-square-1](/images/leetcode/valid-square-1.png)


### 用`Set`
想要省事，可以把所有边长以及对角线长扔进一个`Set`，反正最后`Set`里
1. 只能有两个数字，一个边长，一个对角线长
2. 不能有数字为`0`
3. 并且满足对角线长的平方等于边长平方的两倍

#### 代码
```java
class Solution {
    public boolean validSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        Integer[] disArr = new Integer[]{dis(p1, p2), dis(p1, p3), dis(p1, p4), dis(p2, p3), dis(p2, p4), dis(p3, p4)};
        Set<Integer> set = new HashSet<>(Arrays.asList(disArr));
        List<Integer> list = new ArrayList<>(set);
        if (list.contains(0) || list.size() != 2) return false;
        Integer pow1 = list.get(0), pow2 = list.get(1);
        return pow1 == 2 * pow2 || pow2 == 2 * pow1;
    }

    private Integer dis(int[] a, int[] b) {
        return (int) (Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
    }
}
```

#### 结果
![valid-square-2](/images/leetcode/valid-square-2.png)


### 数学上的投机取巧
上面最后一个条件：**对角线平方必须是边长平方的2倍**，其实可以不用计算。因为满足`Set`里只有两个数字，但不是正方形的只有一种可能，就是：**两个等边三角形（60度）拼成的菱形**。

但是这样的菱形在坐标输入都是`Integer`整数的情况下，并不存在，所以可以不用检查第3个条件。

但要注意，每次想要这样投机取巧，必须确保数学上是严密的。

#### 代码
```java
class Solution {
    public boolean validSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        Integer[] disArr = new Integer[]{dis(p1, p2), dis(p1, p3), dis(p1, p4), dis(p2, p3), dis(p2, p4), dis(p3, p4)};
        Set<Integer> set = new HashSet<>(Arrays.asList(disArr));
        List<Integer> list = new ArrayList<>(set);
        return !list.contains(0) && list.size() == 2;
    }

    private Integer dis(int[] a, int[] b) {
        return (int) (Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
    }
}
```

#### 结果
![valid-square-3](/images/leetcode/valid-square-3.png)

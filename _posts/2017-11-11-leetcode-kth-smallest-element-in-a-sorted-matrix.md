---
layout: post
title: "Leetcode - Algorithm - Kth Smallest Element In A Sorted Matrix "
date: 2017-11-11 20:53:45
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers","binary search"]
level: "medium"
description: >
---

### 题目
Given a n x n matrix where each of the rows and columns are sorted in ascending order, find the kth smallest element in the matrix.

Note that it is the kth smallest element in the sorted order, not the kth distinct element.

Example:
```
matrix = [
   [ 1,  5,  9],
   [10, 11, 13],
   [12, 13, 15]
],
k = 8,

return 13.
```

Note:
You may assume k is always valid, `1 ≤ k ≤ n2`.

### 用多个指针
一个一个数字数过去，每一行都需要维护一个指针，表示当前进度。

#### 代码
```java
class Solution {
    /** list of pointer */
    public int kthSmallest(int[][] matrix, int k) {
        if (matrix.length == 0) { return 0; }
        if (matrix.length == 1) { return (k == 1)? matrix[0][0] : 0; }
        int remain = k - 1;
        Pair min = new Pair(0,0);
        int minVal = matrix[0][0];
        List<Pair> list = new LinkedList<>();
        list.add(new Pair(0,1));
        list.add(new Pair(1,0));
        while (!list.isEmpty()) {
            if (remain == 0) { return minVal; }
            min = null;
            minVal = Integer.MAX_VALUE;
            for (Pair p : list) {
                if (matrix[p.x][p.y] <= minVal) {
                    min = p;
                    minVal = matrix[p.x][p.y];
                }
            }
            --remain;
            if (min.y < (matrix.length - 1)) {
                ++min.y;
            } else {
                list.remove(min);
            }
            if (!list.isEmpty()) {
                Pair last = list.get(list.size()-1);
                if ((last.y == 1) && (last.x < (matrix.length - 1))) {
                    list.add(new Pair(min.x+1,0));
                }
            }
        }
        return (remain == 0)? minVal : 0;
    }

    private class Pair {
        int x;
        int y;
        private Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
```

#### 结果
![kth-smallest-element-in-a-sorted-matrix-1](/images/leetcode/kth-smallest-element-in-a-sorted-matrix-1.png)


### 二分查找
普通的二分查找需要整个数组都是有序的。明显这题的数组只是部分有序。但稍微变化一下思路，可以利用数字取值范围进行二分查找。还是开头那个例子，
```
matrix = [
   [ 1,  5,  9],
   [10, 11, 13],
   [12, 13, 15]
],
k = 8,

return 13.
```

先取最小数`1`和最大数`15`，获得中位数`mid = (1+15)/2 = 8`。接着统计`<= 8`的数字的个数，结果只有`2`个。`2 < 8`。说明我们的目标数一定大于`2`。 这时候我们把最小数改为`2+1=3`，重复上面的过程，直到数字取值空间只有一个数字位置。

需要注意两点。第一，统计小于中位数的数字个数时，不需要遍历整个数组（$$O(n^2)$$, n为矩阵的大小），利用每一行和每一列都是有序数组的特性，可以在$$O(n)$$时间里完成。

第二，假如`k=7`，当我们发现有`8`个数字`<= 13`，这个时候不能直接判断`13`太大了，因为有重复的数字存在，在上面的例子里有两个`13`。

#### 代码
```java
class Solution {
    public int kthSmallest(int[][] matrix, int k) {
        int size = matrix.length;
        int lo = matrix[0][0];
        int hi = matrix[size-1][size-1];
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int count = 0;
            int cur = size - 1;
            for (int i = 0; i < size; i++) { // 统计 <= mid 的数字个数
                while (cur >= 0 && matrix[i][cur] > mid) { cur--; }
                count += (cur + 1);
            }
            if (count < k) { // mid 太小
                lo = mid + 1;
            } else { // mid 有可能太大，有可能正好
                // 当 count > k 时，不能直接判断 mid 太大， 因为存在重复的数字
                hi = mid;
            }
        }
        return lo;
    }
}
```

#### 结果
![kth-smallest-element-in-a-sorted-matrix-2](/images/leetcode/kth-smallest-element-in-a-sorted-matrix-2.png)

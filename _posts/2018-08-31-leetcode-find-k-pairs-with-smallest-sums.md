---
layout: post
title: "Leetcode - Algorithm - Find K Pairs With Smallest Sums "
date: 2018-08-31 13:47:59
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","heap"]
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
* You may assume k is always valid, 1 ≤ k ≤ n2.

### 计算所有可能的配对
老老实实计算出每一对和，放进一个Heap里输出。

#### 代码
```java
/**
 * x = min(m*n, k)
 * O(xlogx)
 * 老老实实计算出每一对和，放进一个Heap里输出。
 */
class Solution1 implements Solution {
    public List<int[]> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<int[]> res = new ArrayList<>();
        if (nums1 == null || nums2 == null || k < 0) {
            return res;
        }
        PriorityQueue<int[]> heap = new PriorityQueue<>(new Comparator<int[]>(){
            public int compare(int[] a, int[] b) {
                return a[0] + a[1] - b[0] - b[1];
            }
        });
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                heap.offer(new int[]{nums1[i], nums2[j]});
            }
        }
        for (int i = 0; i < k && !heap.isEmpty(); i++) {
            res.add(heap.poll());
        }
        return res;
    }
}
```

#### 结果
![find-k-pairs-with-smallest-sums-1](/images/leetcode/find-k-pairs-with-smallest-sums-1.png)


### 用`Heap`维护一组指针，O(klog(min(m,n)))
因为`nums1`和`nums2`都是实现排过序的。所以，
> nums1[x] + nums2[y] <= nums1[x] + nums2[y+1]

所以如果`nums1[x] + nums2[y]`都没被选上，就没必要拿`nums1[x] + nums2[y+1]`上去比。所以问题就抽象成一组从nums1到nums2的向量。
![find-k-pairs-with-smallest-sums-a](/images/leetcode/find-k-pairs-with-smallest-sums-a.png)

再简化一点，甚至不需要从一开始就维护所有这些指针。一开始只需要一个指针，然后慢慢加入新指针。因为同理，
> nums1[x] + nums2[0] <= nums1[x+1] + nums2[0]

#### 代码
```java
class Solution {
    public List<int[]> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<int[]> res = new ArrayList<>();
        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) {
            return res;
        }
        // 以较小的数组作为生成向量的主数组
        int[] numsA = (nums1.length <= nums2.length)? nums1 : nums2;
        int[] numsB = (numsA == nums1)? nums2 : nums1;
        PriorityQueue<int[]> heap = new PriorityQueue<>(new Comparator<int[]>(){
            public int compare(int[] a, int[] b) {
                return numsA[a[0]] + numsB[a[1]] - numsA[b[0]] - numsB[b[1]];
            }
        });
        int vectorP = 0;
        heap.offer(new int[]{vectorP, 0});
        while (k-- > 0 && !heap.isEmpty()) {
            int[] next = heap.poll();
            if (numsA == nums1) {
                res.add(new int[]{numsA[next[0]], numsB[next[1]]});
            } else {
                res.add(new int[]{numsB[next[1]], numsA[next[0]]});
            }
            if (next[0] == vectorP && vectorP < numsA.length - 1 && next[1] == 0) { // 添加新向量
                heap.offer(new int[]{++vectorP, 0});
            }
            if (next[1] < numsB.length - 1) { // 弹出的向量向前进一格
                heap.offer(new int[]{next[0], next[1] + 1});
            }
        }
        return res;
    }
}
```

#### 结果
![find-k-pairs-with-smallest-sums-2](/images/leetcode/find-k-pairs-with-smallest-sums-2.png)


### 一种可能的O(k)的解法
Stephanpochmann又给出了一种`O(k)`的解法。基本思想基于`kth smallest element in a sorted matrix`这个子问题，
> 整个和空间是一个二维矩阵。我先找到第k小的和，然后再从这个数一个个往前推。

[O(k) solution](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/discuss/84577/O(k)-solution)

关键点就是他找到了一个在`O(#row)`时间里解决`kth smallest element in a sorted matrix`问题的办法。
[O(n) from paper. Yes, O(#rows).](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/discuss/85170/O(n)-from-paper.-Yes-O(rows).?page=1)

---
layout: post
title: "Leetcode - Algorithm - Range Sum Query Mutable "
date: 2018-08-02 01:48:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["segment tree","tree"]
level: "medium"
description: >
---

### 题目
Given an integer array nums, find the sum of the elements between indices i and j (i ≤ j), inclusive.

The update(i, val) function modifies nums by updating the element at index i to val.

Example:
```
Given nums = [1, 3, 5]

sumRange(0, 2) -> 9
update(1, 2)
sumRange(0, 2) -> 8
```

Note:
1. The array is only modifiable by the update function.
2. You may assume the number of calls to update and sumRange function is distributed evenly.

### 常用手段：提前做好加法
老套路：提前计算累加和，
```
[   1, 2,  3,  4,  5   ]
[   1, 3,  6,  10, 15  ]    // 每一位都是前面所有数字的累加和
```
这样的好处是，每次计算`sum[i,j]`(j >= i)，只需要`O(1)`的时间。
> sum[i,j] = sum[j] - sum[i-1]

缺点是：每次`update()`，需要花`O(n)`的时间更新所有位的累加和。

#### 代码
```java
class NumArray {

        private static int[] numbers = new int[0];
        private static int[] sum = new int[0];
        private static Map<Integer,Integer> diff = new HashMap<>();

        public NumArray(int[] nums) {
            numbers = new int[nums.length];
            sum = new int[nums.length];
            diff.clear();
            if (nums.length == 0) { return; }
            numbers[0] = nums[0];
            sum[0] = nums[0];
            for (int i = 1; i < nums.length; i++) {
                numbers[i] = nums[i];
                sum[i] = sum[i-1] + nums[i];
            }
        }

        public void update(int i, int val) {
            int d = val - numbers[i];
            numbers[i] = val;
            diff.put(i,(diff.containsKey(i))? diff.get(i) + d : d);
        }

        public int sumRange(int i, int j) {
            int res = (i == 0)? sum[j] : sum[j] - sum[i-1];
            for (int k = i; k <= j; k++) {
                if (diff.containsKey(k)) {
                    res += diff.get(k);
                }
            }
            return res;
        }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray obj = new NumArray(nums);
 * obj.update(i,val);
 * int param_2 = obj.sumRange(i,j);
 */
```

#### 结果
![range-sum-query-mutable-1](/images/leetcode/range-sum-query-mutable-1.png)


### 线段树(Segment Tree)
考虑到`sum()`和`update()`函数调用次数比较接近，所以单纯减少`sum()`的复杂度，却增加`update()`的复杂度，这种做法是不平衡的。一种叫“线段树（Segment Tree）”的数据结构可以在`sum()`和`update()`之间做一种妥协，

线段树本质上是一个二叉树，逐步将相邻数字两两相加，和储存在他们的父节点。越往上父节点储存的是越多数字的和。这些父节点需要标出它是哪个范围内叶节点的和。

![range-sum-query-mutable-a](/images/leetcode/range-sum-query-mutable-a.png)

这里每个节点需要维护三组值：
1. 和的作用范围（方便查找）
2. 左右两个子节点
3. 范围内所有数的和

如图所示，我们需要计算`7+9+11+13`的和，因为`9+11`已经预先计算好，所以只需要计算`7+20+13`即可。这就是为什么线段树效率高的原因，因为，
> 不是僵化地求所有数的和，而是步步为营地求取一部分数字的和，做局部优化。

在更新某个元素值的时候，`update()`函数需要更新的节点数和树的深度成正比，复杂度为`O(logn)`。

![range-sum-query-mutable-f](/images/leetcode/range-sum-query-mutable-f.png)

计算某区间和的`rangeSum()`函数，最终访问的节点数量也是和树的深度成正比，复杂度也是`O(logn)`。
![range-sum-query-mutable-g](/images/leetcode/range-sum-query-mutable-g.png)

平衡地相当好。

#### 代码
```java
class NumArray {
        // 创建线段树
        public NumArray(int[] nums) {
            localNums = nums;
            root = creatNode(0,nums.length-1);
        }
        // 更新数字    
        public void update(int i, int val) {
            int diff = val - localNums[i];
            localNums[i] = val;
            updateHelper(root,i,diff);
        }
        // 求和
        public int sumRange(int i, int j) {
            sum = 0;
            sumRangeHelper(root,i,j);
            return sum;
        }

        /**
         * 线段树的数据结构
         */
        private class SegmentTreeNode {
            public int start,end;               // 当前节点的作用范围
            public SegmentTreeNode left,right;  // 左右两个子域
            public int val;                     // 范围内所有元素的和

            public SegmentTreeNode(int val) {
                this.val = val;
            }
        }

        private SegmentTreeNode root;   // 线段树
        private int[] localNums;        // 原始数组

        //二分递归求和
        private SegmentTreeNode creatNode(int start, int end) {
            if (start > end) { return null; }
            SegmentTreeNode root = new SegmentTreeNode(0);
            root.start = start;
            root.end = end;
            if (start == end) {
                root.val = localNums[start];
            } else {
                int mid = start + (end - start) / 2;    // 上位中位数
                root.left = creatNode(start,mid);
                root.right = creatNode(mid+1,end);
                root.val = root.left.val + root.right.val;
            }
            return root;
        }
        //更新某个数的值，也要更新所有覆盖它的域的和
        //递归：递出去到底层叶节点，定位目标节点，然后逐级回归到上层父节点
        private void updateHelper(SegmentTreeNode root, int i, int diff) {
            if (root == null) { return; }
            if (root.start <= i && root.end >= i) {
                root.val += diff;
                updateHelper(root.left,i,diff);
                updateHelper(root.right,i,diff);
            }
        }
        //原理就是尽量使用更大块的计算好的区域元素和，而不是一个个元素去求和
        private int sum;
        private void sumRangeHelper(SegmentTreeNode root, int start, int end) {
            if (root == null) { return; }
            if (start <= root.start && end >= root.end) { // 此节点覆盖的域是求和范围的子集（没必要再往下递归，直接加上这一整块的和）
                sum += root.val;
            } else if (start <= root.end && end >= root.start){ // 我们只需要节点覆盖范围的一部分和，节点累加和不能直接用，需要向下递归到更精细的分区
                sumRangeHelper(root.left,start,end);
                sumRangeHelper(root.right,start,end);
            } // else { 剩下的完全没有交集的，也不需要递归下去 }
        }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray obj = new NumArray(nums);
 * obj.update(i,val);
 * int param_2 = obj.sumRange(i,j);
 */
```

#### 结果
![range-sum-query-mutable-2](/images/leetcode/range-sum-query-mutable-2.png)


### 二叉树数组（Binary Indexed Tree）
以同样的原理，线段树（Segment Tree）可以单纯用数组（二叉树数组【Binary Index Tree】）实现。

二叉树数组其实就是线段树的变种。只不过用数组的下标代替线段树节点中维护的覆盖范围。还是考虑`[1,3,5,7,9,11,13,15]`这么一串数字，
![range-sum-query-mutable-b](/images/leetcode/range-sum-query-mutable-b.png)
在原数组的前面空出一个槽位`bit[0]`，为了后面取下标更容易。然后，绿色部分是直接填写的数字。
* bit[1] = 3
* bit[3] = 5
* bit[5] = 9
* bit[7] = 13

然后`bit[2]`相当于`bit[1]`和`bit[3]`的父节点，`bit[6]`是`bit[5]`和`bit[7]`的父节点，所以，
* bit[2] = bit[1] + nums[1] = 4
* bit[6] = bit[5] + nums[5] = 20

然后`bit[4]`又是`bit[6]`和`bit[2]`的父节点。
* bit[4] = bit[2] + bit[3] + nums[3] = 16

最后`bit[8]`又是`bit[4]`的父节点。
* bit[8] = bit[4] + bit[6] + bit[7] + nums[7] = 64

注意一下`i+(i&-i)`和`i-(i&-i)`这两个变量，

首先`i&-i`是用来切出二进制最后一个`1`位的，
![range-sum-query-mutable-c](/images/leetcode/range-sum-query-mutable-c.png)

切出最后一个`1`位有什么用呢？2的整数倍只有一个`1`位，再加一次`1`位，相当于乘以2。从二叉树数组的角度看，相当于 **沿着左子树向上冒泡找父节点**。
![range-sum-query-mutable-d](/images/leetcode/range-sum-query-mutable-d.png)

如果减掉这个`1`位，以`7`为例：相当于 **沿着右子树向上冒泡找父节点**。
* 7 - 2^0 = 6
* 6 - 2^1 = 4
* 4 - 2^2 = 0
![range-sum-query-mutable-e](/images/leetcode/range-sum-query-mutable-e.png)


#### 代码
```java
/**
 * Binary Indexed Trees (BIT or Fenwick tree):
 * https://www.topcoder.com/community/data-science/data-science-
 * tutorials/binary-indexed-trees/
 *
 * Example: given an array a[0]...a[7], we use a array BIT[9] to
 * represent a tree, where index [2] is the parent of [1] and [3], [6]
 * is the parent of [5] and [7], [4] is the parent of [2] and [6], and
 * [8] is the parent of [4]. I.e.,
 *
 * BIT[] as a binary tree:
 *            ______________*
 *            ______*
 *            __*     __*
 *            *   *   *   *
 * indices: 0 1 2 3 4 5 6 7 8
 *
 * BIT[i] = ([i] is a left child) ? the partial sum from its left most
 * descendant to itself : the partial sum from its parent (exclusive) to
 * itself. (check the range of "__").
 *
 * Eg. BIT[1]=a[0], BIT[2]=a[1]+BIT[1]=a[1]+a[0], BIT[3]=a[2],
 * BIT[4]=a[3]+BIT[3]+BIT[2]=a[3]+a[2]+a[1]+a[0],
 * BIT[6]=a[5]+BIT[5]=a[5]+a[4],
 * BIT[8]=a[7]+BIT[7]+BIT[6]+BIT[4]=a[7]+a[6]+...+a[0], ...
 *
 * Thus, to update a[1]=BIT[2], we shall update BIT[2], BIT[4], BIT[8],
 * i.e., for current [i], the next update [j] is j=i+(i&-i) //double the
 * last 1-bit from [i].
 *
 * Similarly, to get the partial sum up to a[6]=BIT[7], we shall get the
 * sum of BIT[7], BIT[6], BIT[4], i.e., for current [i], the next
 * summand [j] is j=i-(i&-i) // delete the last 1-bit from [i].
 *
 * To obtain the original value of a[7] (corresponding to index [8] of
 * BIT), we have to subtract BIT[7], BIT[6], BIT[4] from BIT[8], i.e.,
 * starting from [idx-1], for current [i], the next subtrahend [j] is
 * j=i-(i&-i), up to j==idx-(idx&-idx) exclusive. (However, a quicker
 * way but using extra space is to store the original array.)
 */
class NumArray {
        private int[] data;
        private int[] bit;

        //创建整个数组，可以看做逐个更新所有数字
        public NumArray(int[] nums) {
            data = new int[nums.length];
            bit = new int[nums.length+2];
            for (int i = 0; i < data.length; i++) {
                update(i,nums[i]);
            }
        }
        //更新单个数字，需要不断地从右子树向上冒泡找到父节点（父节点覆盖更大范围）
        public void update(int i, int val) {
            int diff = val - data[i];
            data[i] = val;
            i++;
            while (i < bit.length) {
                bit[i] += diff;
                i += (i & -i); // double last 1-bit（从右子树向上冒泡找到父节点）
            }
        }

        public int sumRange(int i, int j) {
            return sum(j) - sum(i-1);
        }
        //求从首元素到end位置所有元素的和
        //需要不断从左子树向上冒泡找父节点
        private int sum(int end) {
            int sum = 0;
            int i = end+1;
            while (i > 0) {
                sum += bit[i];
                i -= (i & -i); // remove last 1-bit（从左子树向上冒泡找父节点）
            }
            return sum;
        }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray obj = new NumArray(nums);
 * obj.update(i,val);
 * int param_2 = obj.sumRange(i,j);
 */
```

#### 结果
![range-sum-query-mutable-3](/images/leetcode/range-sum-query-mutable-3.png)

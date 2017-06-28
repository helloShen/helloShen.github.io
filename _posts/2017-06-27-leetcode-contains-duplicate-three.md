---
layout: post
title: "Leetcode - Algorithm - Contains Duplicate Three "
date: 2017-06-27 07:07:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","binary search tree","array"]
level: "medium"
description: >
---

### 题目
Given an array of integers, find out whether there are two distinct indices i and j in the array such that the absolute difference between `nums[i]` and `nums[j]` is at most t and the absolute difference between i and j is at most k.

### 暴力窗口内遍历，复杂度 $$O(n*t)$$
维护一个窗口，每次都和窗口内的所有数字比较。

#### 代码
```java
public class Solution {
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        if (nums.length < 2) { return false; }
        for (int slow = 0, fast = 1; fast < nums.length; fast++) {
            if (fast > k) { slow++; }
            int newNum = nums[fast];
            for (int cur = slow; cur < fast; cur++) {
                long diff = Math.abs((long)nums[cur] - (long)newNum); // use long to avoid overflow
                if (diff <= t) { return true; }
            }
        }
        return false;
    }
}
```

#### 结果
复杂度太高。超时！
![contains-duplicate-three-1](/images/leetcode/contains-duplicate-three-1.png)


### `HashMap Bucket`，复杂度 $$O(n)$$
主要思路：
> 既然找相等的重复数字的时候，可以用一个`HashMap`在 $$O(1)$$ 的时间内完成查找。那能不能把这个查找扩大到一个范围？变成一个范围查找。只要数字落在一个范围内，就可以匹配上。

根据这个思路，是可以解决问题的，不过需要做一点变化。 假设需要找的数字是`137`，`t = 4`。也就是`abs(x - 137) <= 4`。

取`5`为除数，每个数字存入`HashMap`之前，都先除以`5`。
```
HashMap中的每个键值 key 都对应了 5 个数字 （叫一个bucket）：

                                lo                 target                hi
                                 |                   |                   |
        ... ...  |  130,131,132,133,134  |  135,136,137,138,139  |  140,141,142,143,144  |  ... ...
     key = 25           key = 26                key = 27                key = 28            key = 29

                            |                        |                      |
                    current bucket -1           current bucket      current bucket + 1

[lo,hi]中的所有数字都一定在[bucket-1,bucket,bucket+1]左中右3个bucket中。
```

这时候，可以发现，目标数字`137`加减4范围内的数字`[lo,hi]`，不都是和137在一个bucket里。**但他们都一定在`[bucket-1,bucket,bucket+1]`中的一个内`。**

所以只需要检查`[26,27,28]`三个bucket，看里面有没有`[133,141]`范围内的数字。

#### 几个corner case
##### `0`的问题
向`bucket`映射的效果，相当于以`0`为中心，向中间压缩数轴。所有`bucket = 0`中的数字会多出一倍。

```

    ... ... |  -10,-8,-7,-6,-5  |  -4,-3,-2,-1,0,1,2,3,4  |  5,6,7,8,9  |  ... ...
                 key = -1                key = 0              key = 1
```

这这个问题的解决办法是，每个数都先减去`Integer.MIN_VALUE`，然后在`long`的空间上来做。这样相当于把从`Integer.MIN_VALUE`开始的整个`int`空间平移到`0`的位置为起点。

##### 用`t`还是`t+1`做除数？
理论上用`t`和`t+1`都可以。但用`t+1`的好处是：不存在`t=0`的特殊情况。因为`0`不能做除数。所以`t+1`好一些。

#### 代码
```java
public class Solution {
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        if (nums.length < 2) { return false; }
        if (t < 0) { return false; }
        Map<Long,Long> buckets = new HashMap<>();
        Long[] bucketMemo = new Long[nums.length];
        for (int i = 0, j = 0; j < nums.length; j++) {
            Long numL = (long)nums[j] - Integer.MIN_VALUE;
            Long bucket = numL / ((long)t + 1);
            bucketMemo[j] = bucket;
            if (j > k) { buckets.remove(bucketMemo[i++]); }
            // check: bucket-1, this bucket and bucket+1
            if (buckets.get(bucket) != null) { return true; } // this bucket
            Long left = buckets.get(bucket-1);
            if (left != null && (numL - left) <= t) { return true; } // bucket-1
            Long right = buckets.get(bucket+1);
            if (right != null && (right - numL) <= t) { return true; } // bucket+1
            buckets.put(bucket,numL);
        }
        return false;
    }
}
```

#### 结果
![contains-duplicate-three-2](/images/leetcode/contains-duplicate-three-2.png)


### 利用二叉树做范围检查
二叉树搜索一个元素的复杂度是 $$O(\log_{}{n})$$。也同样能在 $$O(\log_{}{n})$$ 时间内找到 **第一个大于或等于某阈值的元素**，`ceiling()`函数。和`ceiling()`函数相反的是`floor()`，可以找出 **第一个小于或等于某阈值的元素**。

这个特性可以用来在 $$O(\log_{}{n})$$ 进行范围搜索。还是刚才`137`的例子。`t=4`，判定为重复的范围是`[133,141]`。
> 如果 ceiling(133) <= 141， 说明[133,141]范围内有元素。

#### 代码
```java
public class Solution {
    private static class TreeNode {
        private long val;
        private TreeNode left;
        private TreeNode right;
        private TreeNode(long n) { val = n; }
    }
    private static class BinarySearchTreeWithDummy {

        private TreeNode dummy = new TreeNode(0); // sentinel

        /** Insert new element into the tree
         *  Return true if new value is added
         *  Return false if this value is already exist
         */
        private boolean add(long n) {
            TreeNode pre = dummy, cur = dummy.right; // head is always the right child of dummy
            boolean fromLeft = false;
            while (cur != null) {
                long val = cur.val;
                if (val > n) { // go left
                    pre = cur; cur = cur.left; fromLeft = true;
                } else if (val < n) { // go right
                    pre = cur; cur = cur.right;
                } else { // value already exist
                    return false;
                }
            }
            TreeNode newNode = new TreeNode(n);
            if (fromLeft) {
                pre.left = newNode;
            } else {
                pre.right = newNode;
            }
            return true;
        }
        /**
         * Return true if the target is removed
         * Return false if target doesn't exist
         */
        private boolean remove(long n) {
            TreeNode pre = dummy, cur = dummy.right;
            boolean fromLeft = false;
            while (cur != null) {
                if (cur.val > n) { // go left
                    pre = cur; cur = cur.left; fromLeft = true;
                } else if (cur.val < n) { // go right
                    pre = cur; cur = cur.right;
                } else { // find target
                    TreeNode tempHead = new TreeNode(0);
                    if (cur.left != null) {
                        tempHead.left = cur.left;
                        tempHead.right = cur.right;
                        cur = cur.left;
                        while (cur.right != null) { cur = cur.right; }
                        cur.right = tempHead.right;
                    } else {
                        tempHead.left = cur.right;
                    }
                    if (fromLeft) {
                        pre.left = tempHead.left;
                    } else {
                        pre.right = tempHead.left;
                    }
                    return true;
                }
            }
            return false;
        }
        /**
         * Return least element greater than the input n
         * Return null if there is no element greater than n
         */
        private Long ceiling(long n) {
            TreeNode lastGreater = null;
            TreeNode cur = dummy.right;
            while (cur != null) {
                if (cur.val > n) { // go left
                    lastGreater = cur; cur = cur.left;
                } else if (cur.val < n) {
                    cur = cur.right;
                } else { // find target
                    return new Long(cur.val);
                }
            }
            return (lastGreater == null)? null : new Long(lastGreater.val);
        }
    }
    /**
     * Solution based on Binary Search Tree
     * Use my BinarySearchTreeWithDummy
     */
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        if (nums.length < 2) { return false; }
        if (t < 0) { return false; }
        BinarySearchTreeWithDummy tree = new BinarySearchTreeWithDummy();
        for (int slow = 0, fast = 0; fast < nums.length; fast++) {
            if (fast > k) { tree.remove((long)nums[slow++]); }
            long floor = (long)nums[fast] - t;
            long ceil = (long)nums[fast] + t;
            Long firstGreaterThanFloor = tree.ceiling(floor);
            if (firstGreaterThanFloor != null && firstGreaterThanFloor <= ceil) { return true; }
            tree.add((long)nums[fast]);
        }
        return false;
    }
}
```

#### 结果
![contains-duplicate-three-3](/images/leetcode/contains-duplicate-three-3.png)


### 用库自带容器`TreeSet`实现
原理是一样的。`TreeSet`自带`ceiling()`函数和`floor()`函数。不需要自己造轮子。

#### 代码
```java
/**
 * Another solution based on Binary Search Tree
 * But use in-build TreeSet Container
 */
public class Solution {
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        if (nums.length < 2) { return false; }
        if (t < 0) { return false; }
        TreeSet<Long> tree = new TreeSet<>();
        for (int slow = 0, fast = 0; fast < nums.length; fast++) {
            if (fast > k) { tree.remove((long)nums[slow++]); }
            // System.out.println("Slow=" + slow + ", Fast=" + fast + ", Tree= " + tree);
            long floor = (long)nums[fast] - t;
            long ceil = (long)nums[fast] + t;
            Long firstGreaterThanFloor = tree.ceiling(floor);
            if (firstGreaterThanFloor != null && firstGreaterThanFloor <= ceil) { return true; }
            tree.add((long)nums[fast]);
        }
        return false;
    }
}
```

#### 结果
效率没有我造的轮子高。毕竟我的轮子只提供`add()`,`remove()`,`ceiling()`几个函数，它还提供其他接口。
![contains-duplicate-three-4](/images/leetcode/contains-duplicate-three-4.png)

---
layout: post
title: "Leetcode - Algorithm - Increasing Triplet Subsequence "
date: 2017-09-08 18:07:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","dynamic programming"]
level: "medium"
description: >
---

### 题目
Given an unsorted array return whether an increasing subsequence of length 3 exists or not in the array.

Formally the function should:
> Return true if there exists i, j, k,  
such that arr[i] < arr[j] < arr[k] given 0 ≤ i < j < k ≤ n-1 else return false.
Your algorithm should run in O(n) time complexity and O(1) space complexity.

Examples:
```bash
Given [1, 2, 3, 4, 5],
return true.
```

```bash
Given [5, 4, 3, 2, 1],
return false.
```

### DFS递归
> `boolean dfs(int pre, int index, int remain)`

这个问题是可以分解成子问题的。上面DSF递归函数的签名表示，**在index及其以后位置，能否找remain个大于pre的数？**

每个问题会分解成3个递归调用：
1. 当`nums[index] > n`：可以选择取`nums[index]`，也可以不取。
2. 当`nums[index] <= n`：不取，直接向下递归。
3. 任何情况下，都可以从当前位置重新开始一个DFS递归。


#### 代码
```java
class Solution {
    private static int[] local = new int[0];
    public boolean increasingTriplet(int[] nums) {
        local = nums;
        return helper(Integer.MAX_VALUE,0,2);
    }
    /* 在index及其以后位置，找remain个大于pre的数 */
    private boolean helper(int pre, int index, int remain) {
        if (remain == 0) { return true; }
        if (index == local.length) { return false; }
        if (local[index] > pre) {
            if (helper(local[index],index+1,remain-1)) { return true; } // 当前数大于pre, 可以算上当前数
        }
        if (helper(pre,index+1,remain)) { return true; }                // 无论当前数大小，都可以不算上当前数
        return helper(local[index],index+1,2);                          // 也可以完全从当前数重新开始
    }
}
```

#### 结果
![increasing-triplet-subsequence-1](/images/leetcode/increasing-triplet-subsequence-1.png)


### 自底向上的动态规划
假设如果子问题`T(n-1)`的结果是`false`的话，加入了新元素`nums[n]`以后，问题就变成，在`nums[n+1] ~ nums[nums.length-1]`范围内找两个大于`nums[n]`的数。

#### 代码
```java
class Solution {
    public boolean increasingTriplet(int[] nums) {
        int first = 0, second = 0, third = 0;
        for (int i = nums.length - 3; i >= 0; i--) {
            first = nums[i];
            for (int j = i + 1; j < nums.length - 1; j++) {
                second = nums[j];
                if (second > first) {
                    for (int k = j + 1; k < nums.length; k++) {
                        third = nums[k];
                        if (third > second) { return true; }
                    }
                }
            }
        }
        return false;
    }
}
```

#### 结果
![increasing-triplet-subsequence-2](/images/leetcode/increasing-triplet-subsequence-2.png)


### 时间复杂度 $$O(n)$$ 的解法
维护两个记录，一个较小的数`small`，一个较大的数`big`。更新两个数的时机是有点反直觉的。无论什么时候，两个数必须遵守的一个断言是：
> small < big


#### 代码
```java
class Solution {
    public boolean increasingTriplet(int[] nums) {
        int small = Integer.MAX_VALUE, big = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int n = nums[i];
            /* Assertion: small < big */
            if (n <= small) {           // if n <= small update small
                small = n;
            } else if (n <= big) {      // if small < n <= big update big
                big = n;
            } else {                    // if small < big < n, result found!
                return true;
            }
        }
        return false;
    }
}
```

#### 结果
![increasing-triplet-subsequence-3](/images/leetcode/increasing-triplet-subsequence-3.png)

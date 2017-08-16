---
layout: post
title: "Leetcode - Algorithm - Shuffle An Array "
date: 2017-08-13 22:23:39
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
```
// Init an array with set 1, 2, and 3.
int[] nums = {1,2,3};
Solution solution = new Solution(nums);

// Shuffle the array [1,2,3] and return its result. Any permutation of [1,2,3] must equally likely to be returned.
solution.shuffle();

// Resets the array back to its original configuration [1,2,3].
solution.reset();

// Returns the random shuffling of array [1,2,3].
solution.shuffle();
```

### 和一个随机位置的元素交换位置
用一个指针遍历数组，然后和随机位置的元素交换位置。

#### 代码
```java
public class Solution {
        private int len;
        private int[] nums;
        private Random r = new Random();
        /* constructor */
        public Solution(int[] nums) {
            this.nums = nums;
            len = nums.length;
        }
        /* return original array */
        public int[] reset() {
            return nums;
        }
        /* return shuffle array */
        public int[] shuffle() {
            if (len < 2) { return nums; }
            int[] shuffle = Arrays.copyOf(nums,len);
            int temp = 0, pos = 0;
            for (int i = len - 1; i > 0; i--) {
                pos = r.nextInt(i+1);
                temp = shuffle[pos];
                shuffle[pos] = shuffle[i];
                shuffle[i] = temp;
            }
            return shuffle;
        }
}
```

#### 结果
![shuffle-an-array-1](/images/leetcode/shuffle-an-array-1.png)

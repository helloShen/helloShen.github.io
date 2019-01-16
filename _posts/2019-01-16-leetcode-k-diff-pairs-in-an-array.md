---
layout: post
title: "Leetcode - Algorithm - K Diff Pairs In An Array "
date: 2019-01-16 15:26:45
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["map"]
level: "easy"
description: >
---

### 题目
Given an array of integers and an integer k, you need to find the number of unique k-diff pairs in the array. Here a k-diff pair is defined as an integer pair (i, j), where i and j are both numbers in the array and their absolute difference is k.

Example 1:
```
Input: [3, 1, 4, 1, 5], k = 2
Output: 2
Explanation: There are two 2-diff pairs in the array, (1, 3) and (3, 5).
Although we have two 1s in the input, we should only return the number of unique pairs.
```

Example 2:
```
Input:[1, 2, 3, 4, 5], k = 1
Output: 4
Explanation: There are four 1-diff pairs in the array, (1, 2), (2, 3), (3, 4) and (4, 5).
```

Example 3:
```
Input: [1, 3, 1, 5, 4], k = 0
Output: 1
Explanation: There is one 0-diff pair in the array, (1, 1).
```

Note:
* The pairs (i, j) and (j, i) count as the same pair.
* The length of the array won't exceed 10,000.
* All the integers in the given input belong to the range: [-1e7, 1e7].

### 用`HashSet`，复杂度`O(n)`
假设`k = 2`，如果数组中有`1`，能凑成对的只有`[-1, 1]`和`[1, 3]`两种情况。所以只需要检查数组中有没有`[1,3]`即可。要做这种查找工作，预先遍历一遍数组，把数组中出现的数字存在一个`HashSet`里是最合适的。

考虑到数组中存在重复数字，并且当`1`查找到配对的`3`，之后`3`又会找到配对的`1`，但这只能算一对。因此需要记录所有数字已有的配对情况。这需要引入两个额外`HashSet`，一个`asSmaller`，一个`asGreater`。当我们找到配对`[1,3]`的时候，会在`asSmaller`里添加`1`， 在`asGreater`里加`3`。
```
asSmaller
     |
    [1, 3]
        |
        asGreater
```

#### 代码
```java
class Solution {
    public int findPairs(int[] nums, int k) {
        if (k < 0) return 0;
        if (k == 0) return findPairsSpecialZero(nums);
        Set<Integer> numSet = new HashSet<>();
        for (int n : nums) numSet.add(n);
        Set<Integer> asSmaller = new HashSet<>();
        Set<Integer> asGreater = new HashSet<>();
        int count = 0;
        for (int n : nums) {
            if (!asSmaller.contains(n) && numSet.contains(n + k)) {
                count++;
                asSmaller.add(n);
                asGreater.add(n + k);
            }
            if (!asGreater.contains(n) && numSet.contains(n - k)) {
                count++;
                asGreater.add(n);
                asSmaller.add(n - k);
            }
        }
        return count;
    }

    private int findPairsSpecialZero(int[] nums) {
        int count = 0;
        Map<Integer, Integer> numFreq = new HashMap<>();
        for (int n : nums) {
            if (!numFreq.containsKey(n)) {
                numFreq.put(n, 1);
            } else {
                int freq = numFreq.get(n);
                if (freq == 1) {
                    count++;
                    numFreq.put(n, numFreq.get(n) + 1);
                }
            }
        }
        return count;
    }
}
```

#### 结果
![k-diff-pairs-in-an-array-1](/images/leetcode/k-diff-pairs-in-an-array-1.png)

---
layout: post
title: "Leetcode - Algorithm - Delete And Earn "
date: 2018-09-24 16:30:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Given an array nums of integers, you can perform operations on the array.

In each operation, you pick any nums[i] and delete it to earn nums[i] points. After, you must delete every element equal to nums[i] - 1 or nums[i] + 1.

You start with 0 points. Return the maximum number of points you can earn by applying such operations.

Example 1:
```
Input: nums = [3, 4, 2]
Output: 6
Explanation:
Delete 4 to earn 4 points, consequently 3 is also deleted.
Then, delete 2 to earn 2 points. 6 total points are earned.
```

Example 2:
```
Input: nums = [2, 2, 3, 3, 3, 4]
Output: 9
Explanation:
Delete 3 to earn 3 points, deleting both 2's and the 4.
Then, delete 3 again to earn 3 points, and 3 again to earn 3 points.
9 total points are earned.
```

Note:
* The length of nums is at most 20000.
* Each element nums[i] is an integer in the range [1, 10000].


### 动态规划
假设有`[4, 2, 9, 2, 3, 7, 7, 3, 3]`，先排序成`[2, 2, 3, 3, 3, 4, 7, 7, 9]`，再同一个数字求和，
```
numTable: [2, 3, 4, 7, 9]
sumTable: [4, 9, 4,14, 9]
```
分两种情况，
1. 第一种取了`2`，不能取`3`，所以递归式，
> f(2) = max[ get2 + f(4), f(3) ]

2. 第二种取了`4`，不影响取下一个`7`，所以递归式为，
> f(4) = get4 + f(7)


#### 自顶向下的动态规划
```java
class Solution {

    public int deleteAndEarn(int[] nums) {
        if (nums.length == 0) { return 0; }
        Map<Integer, Integer> table = new HashMap<>();
        for (int n : nums) {
            if (!table.containsKey(n)) {
                table.put(n, n);
            } else {
                table.put(n, table.get(n) + n);
            }
        }
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(table.entrySet());
        Collections.sort(list, (Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b) -> a.getKey() - b.getKey());
        dpTable = new int[list.size() + 1];
        Arrays.fill(dpTable, -1);
        dpTable[list.size()] = 0;
        dpTable[list.size() - 1] = list.get(list.size() - 1).getValue();
        return dp(list, 0);
    }

    private int[] dpTable;

    private int dp(List<Map.Entry<Integer, Integer>> list, int index) {
        if (dpTable[index] != -1) {
            return dpTable[index];
        }
        int value = list.get(index).getValue();
        int curr = list.get(index).getKey();
        int next = list.get(index + 1).getKey();
        if (next - curr > 1) {
            dpTable[index] = value + dp(list, index + 1);
        } else {
            int take = value + dp(list, index + 2);
            int skip = dp(list, index + 1);
            dpTable[index] = Math.max(take, skip);
        }
        return dpTable[index];
    }

}
```

#### 结果
![delete-and-earn-1](/images/leetcode/delete-and-earn-1.png)

#### 自底向上的动态规划
```java
class Solution {

    public int deleteAndEarn(int[] nums) {
        if (nums.length == 0) { return 0; }
        Arrays.sort(nums);
        List<Integer> key = new ArrayList<>();
        List<Integer> value = new ArrayList<>();
        for (int start = 0, end = start; start < nums.length; start = end) {
            while (end < nums.length && nums[end] == nums[start]) end++;
            key.add(nums[start]);
            value.add(nums[start] * (end - start));
        }
        int size = value.size();
        int[] dpTable = new int[size + 1];
        dpTable[size - 1] = value.get(size - 1);
        for (int i = size - 2; i >= 0; i--) {
            if (key.get(i + 1) - key.get(i) > 1) {
                dpTable[i] = value.get(i) + dpTable[i + 1];
            } else {
                int take = value.get(i) + dpTable[i + 2];
                int skip = dpTable[i + 1];
                dpTable[i] = Math.max(take, skip);
            }
        }
        return dpTable[0];
    }

}
```

#### 结果
![delete-and-earn-2](/images/leetcode/delete-and-earn-2.png)

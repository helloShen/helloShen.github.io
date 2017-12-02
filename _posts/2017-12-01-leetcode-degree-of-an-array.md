---
layout: post
title: "Leetcode - Algorithm - Degree Of An Array "
date: 2017-12-01 19:59:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","hash table"]
level: "easy"
description: >
---

### 题目
Given a non-empty array of non-negative integers nums, the degree of this array is defined as the maximum frequency of any one of its elements.

Your task is to find the smallest possible length of a (contiguous) subarray of nums, that has the same degree as nums.

Example 1:
```
Input: [1, 2, 2, 3, 1]
Output: 2
Explanation:
The input array has a degree of 2 because both elements 1 and 2 appear twice.
Of the subarrays that have the same degree:
[1, 2, 2, 3, 1], [1, 2, 2, 3], [2, 2, 3, 1], [1, 2, 2], [2, 2, 3], [2, 2]
The shortest length is 2. So return 2.
```

Example 2:
```
Input: [1,2,2,3,1,4,2]
Output: 6
Note:
* nums.length will be between 1 and 50,000.
* nums[i] will be an integer between 0 and 49,999.
```

### 统计数字的频率，用HashMap
用3个HashMap，
1. 统计每个数字的频率
2. 统计每个数字第一次出现的位置
3. 统计每个数字最后一次出现的位置

这些信息，只需要遍历一遍数组，即可得到。

#### 代码
```java
class Solution {
    /** one passe */
    public int findShortestSubArray(int[] nums) {
        Map<Integer,Integer> freq = new HashMap<>();
        Map<Integer,Integer> firstIndexOf = new HashMap<>();
        Map<Integer,Integer> lastIndexOf = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            Integer n = nums[i];
            if (freq.containsKey(n)) {
                freq.put(n,freq.get(n) + 1);
                lastIndexOf.put(n,i);
            } else {
                freq.put(n,1);
                firstIndexOf.put(n,i);
                lastIndexOf.put(n,i);
            }
        }
        int maxFreq = 0;
        List<Integer> numsWithMaxFreq = new ArrayList<>();
        for (Map.Entry<Integer,Integer> entry : freq.entrySet()) {
            int f = entry.getValue();
            if (f > maxFreq) {
                maxFreq = f;
                numsWithMaxFreq.clear();
                numsWithMaxFreq.add(entry.getKey());
            } else if (f == maxFreq) {
                numsWithMaxFreq.add(entry.getKey());
            }
        }
        int minLen = Integer.MAX_VALUE;
        for (Integer n : numsWithMaxFreq) {
            minLen = Math.min(minLen, (lastIndexOf.get(n) - firstIndexOf.get(n) + 1));
        }
        return minLen;
    }
}
```

#### 结果
![degree-of-an-array-1](/images/leetcode/degree-of-an-array-1.png)


### 用数组
先找出最大的数字，然后给每个数字一个槽位，创建一个数组。因为题目中保证最大数字是50000所以可以这样做。这方法并不推荐。

#### 代码
```java
class Solution {
    /** 用数组 */
    public int findShortestSubArray(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        int maxNum = 0;
        for (int n : nums){
            maxNum = Math.max(n, maxNum);
        }

        int[] start = new int[maxNum + 1];
        int[] end = new int[maxNum + 1];
        int[] que = new int[maxNum + 1];

        for (int i = 0; i < nums.length; i++){
            if (que[nums[i]] == 0)
                start[nums[i]] = i;
            end[nums[i]] = i;
            que[nums[i]]++;
        }

        int max = 0;
        for (int n : que)
            max = Math.max(max, n);

        List<Integer> maxNums = new ArrayList<>();
        for (int i = 0; i < que.length; i++){
            if (que[i] == max)
                maxNums.add(i);
        }

        int res = nums.length;
        for (int n : maxNums){
            int r = end[n] - start[n] + 1;
            res = Math.min(r, res);
        }

        return res;
    }
}
```

#### 结果
![degree-of-an-array-2](/images/leetcode/degree-of-an-array-2.png)

---
layout: post
title: "Leetcode - Algorithm - Majority Element Two "
date: 2017-07-02 12:35:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given an integer array of size n, find all elements that appear more than `⌊ n/3 ⌋` times. The algorithm should run in linear time and in $$O(1)$$ space.

### 主要思路
遇到这种`majority`的问题，可以考虑一下`Boyer-Moore Majority Vote Algorithm`，以及它的推广。

### 常规做法用`HashMap`记录频率。计算复杂度：$$O(n)$$，空间复杂度 $$O(n)$$
虽然不符合`in linear time and in $$O(1)$$ space`的要求，但还是做一下。

#### 代码
```java
/**
 * 使用额外Map记录出现数字的频率
 * 计算复杂度：O(n)，空间复杂度O(n)
 */
public class Solution {
    public List<Integer> majorityElement(int[] nums) {
        Map<Integer,Integer> map = new HashMap<>();
        for (Integer num : nums) {
            Integer freq = map.get(num);
            freq = (freq == null)? 1 : freq+1;
            map.put(num,freq);
        }
        List<Integer> result = new ArrayList<>();
        Integer target = nums.length / 3;
        for (Map.Entry<Integer,Integer> entry : map.entrySet()) {
            Integer freq = entry.getValue();
            if (freq > target) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
}
```

#### 结果
![majority-element-two-1](/images/leetcode/majority-element-two-1.png)


### Boyer-Moore Majority Vote Algorithm
看到这种找`majority`的题，就可以考虑这个`Boyer-Moore Majority Vote Algorithm`。这里要求出现频率 `freq > ⌊ n/3 ⌋`，不是 `50%`。所以需要做一个变化。

最终，出现次数 `freq > ⌊ n/3 ⌋` 的数字，最多只有`2`个。所以就假设有一个大小为`2`的袋子。这个袋子叫`2 reduced bag`。先把里面只能装`2`个数，以及他们的频率统计。他们被初始化为`nums[0]`。然后遍历数组，
* 如果`nums[i] == number1`，`number1`的频率`count1`加一。
* 如果`nums[i] == number2`，`number2`的频率`count2`加一。
* 如果`nums[i]`不等于两个数中的任何一个，
    * 如果`number1`的统计量`count1 == 0`，用`nums[i]`替换带子中的`number1`。
    * 如果`number2`的统计量`count2 == 0`，用`nums[i]`替换带子中的`number2`。
    * 否则，`count1`和`count2`都减去一。

这里，重要的一个结论是：
> 经过上面这个过程，出现次数 `freq > ⌊ n/3 ⌋` 的数字，必定都在这个袋子中。但袋子中的数字不一定都保证`freq > ⌊ n/3 ⌋`。

所以最后还需要再遍历一遍数组，只统计袋子中`2`个数的频率，如果满足`freq > ⌊ n/3 ⌋`就确定为结果。

这个定理可以推广到`任意K个数`。
> 如此维护的`K Reduced Bag`，出现次数 `freq > ⌊ n/(K+1) ⌋` 的数字，必定都在这个大小为`K`的袋子中。但袋子中的数字不一定都保证`freq > ⌊ n/(K+1) ⌋`。

#### 代码
```java
public class Solution {
    public List<Integer> majorityElement(int[] nums) {
        List<Integer> result = new ArrayList<>();
        if (nums.length == 0) { return result; }
        int num1 = nums[0], num2 = nums[0], count1 = 0, count2 = 0;
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            if (num == num1) {
                count1++;
            } else if (num == num2) {
                count2++;
            } else if (count1 == 0) {
                num1 = num; count1 = 1;
            } else if (count2 == 0) {
                num2 = num; count2 = 1;
            } else {
                count1--; count2--;
            }
        }
        count1 = 0; count2 = 0;
        for (int num : nums) {
            if (num == num1) { count1++; continue; }
            if (num == num2) { count2++; }
        }
        int target = nums.length / 3;
        if (count1 > target) { result.add(num1); }
        if (count2 > target) { result.add(num2); }
        return result;
    }
}
```

#### 结果
![majority-element-two-2](/images/leetcode/majority-element-two-2.png)

---
layout: post
title: "Leetcode - Algorithm - Majority Element "
date: 2017-06-11 02:26:38
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","bit manipulation",]
level: "easy"
description: >
---

### 主要收获
> 和自己约法三章： 当一个小时以后，还是处于完全没有思路的状态，就求助。

### 题目
Given an array of size n, find the majority element. The majority element is the element that appears more than `⌊ n/2 ⌋` times.

You may assume that the array is non-empty and the majority element always exist in the array.

**！注意**：这题的一个重要假设就是 **肯定有一个数出现次数超过一半**，否则下面很多大部分算法都不成立。

### 用`HashMap`记录所有数字的出现频率，复杂度 $$O(n)$$
最笨，但是普适性最好的办法，四种方法中，唯一一个在不肯定出现超过半数的数字是还能工作的方法。

虽然是 $$O(n)$$的复杂度，但容器的使用影响了效率。

#### 代码
```java
public class Solution {
    public int majorityElement(int[] nums) {
        Map<Integer,Integer> freq = new HashMap<>();
        for (int num : nums) {
            Integer times = freq.get(num);
            if (times == null) {
                freq.put(num,1);
            } else {
                freq.put(num,times+1);
            }
        }
        for (Map.Entry<Integer,Integer> entry : freq.entrySet()) {
            if (entry.getValue() > (nums.length / 2)) {
                return entry.getKey();
            }
        }
        return 0;
    }
}
```

#### 结果

![majority-element-1](/images/leetcode/majority-element-1.png)


### Moore-Boyer Majority Voting Algorithm
* 时间复杂度：$$O(n)$$
* 空间复杂度：$$O(1)$$

想了1个多小时，也想不出来怎么在时间复杂度：$$O(n)$$， 以及空间复杂度：$$O(1)$$ 解这道题。

原来这题是有典故的：--> <http://www.cs.utexas.edu/~moore/best-ideas/mjrty/index.html>

Moore-Boyer投票算法思路如下：
1. 先假设第一个数是major，自己投自己一票。
2. 如果下一数和major相同，再给major加一票。
3. 如果下个数和major不同，给major减去一票。
4. 如果当前major票数归零。则再临时推举当前位数字为新任major。
5. 直到遍历到最后一个数字，返回最后的major。

每题都想到这种天才方法太难了，所以还是要学会求助。

#### 代码
```java
public class Solution {
    public int majorityElement(int[] nums) {
        int major = nums[0], count = 1;
        for (int i = 1; i < nums.length; i++) {
            if (count == 0) { // suggest new major
                major = nums[i];
                count++;
            } else { // vote for the current major
                count = (major == nums[i])? count+1 : count-1;
            }
        }
        return major;
    }
}
```

#### 结果
![majority-element-2](/images/leetcode/majority-element-2.png)


### 用`sort()`，复杂度 $$O(n\log_{n})$$
当然也可以先排序，然后取中位数。

#### 代码
```java
public class Solution {
    public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[(nums.length-1)/2];
    }
}
```

#### 结果
$$O(n\log_{n})$$ 复杂度，既然比 $$O(n)$$ 复杂度的还快。只能说明`sort()`库函数优化地好。
![majority-element-3](/images/leetcode/majority-element-3.png)

### Bit Manipulation，复杂度 $$O(n)$$
计算每一位出现`1`的次数。最后超过半数的位挑出来，组成最后的结果。

这里复杂度虽然也是 $$O(n)$$，但至少是`32 * n`，所以复杂度比前两种单纯 $$O(n)$$ 的还是要慢。

#### 代码
```java
public class Solution {
    public int majorityElement(int[] nums) {
        int[] bits = new int[32];
        for (int num : nums) {
            for (int i = 0; i < 32; i++) {
                bits[i] += (num & 1);
                num = num >> 1;
            }
        }
        int ret = 0;
        int half = (nums.length - 1)/ 2;
        for (int i = 0; i < 32; i++) {
            if (bits[i] > half) { ret += (1 << i); }
        }
        return ret;
    }
}
```

#### 结果
![majority-element-4](/images/leetcode/majority-element-4.png)

---
layout: post
title: "Leetcode - Algorithm - Single Number Two "
date: 2017-05-28 21:08:50
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "medium"
description: >
---

### 题目
Given an array of integers, every element appears three times except for one, which appears exactly once. Find that single one.

Note:
Your algorithm should have a linear runtime complexity. Could you implement it without using extra memory?

### 利用`HashMap`，时间复杂度 $$O(n)$$，空间复杂度 $$O(n)$$
用一个`HashMap`储存所有出现过的数字。

#### 代码
针对这题的一种解法，
```java
public class Solution {
    public int singleNumber(int[] nums) {
        Map<Integer,Integer> memo = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer remain = memo.get(nums[i]);
            if (remain == null) {
                memo.put(nums[i],2);
            } else if (remain > 1) {
                memo.replace(nums[i],remain-1);
            } else if (remain == 1) {
                memo.remove(nums[i]);
            }
        }
        int res = 0;
        for (Map.Entry<Integer,Integer> entry : memo.entrySet()) {
            res = entry.getKey();
        }
        return res;
    }
}
```

无论有多少重复，从一堆数字中找到所有不重复数字集合的通用方法，
```java
public class Solution {
    public int singleNumber(int[] nums) {
        Set<Integer> members = new HashSet<>();
        Set<Integer> duplicates = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            Integer num = nums[i];
            if (!duplicates.contains(num)) {
                if (!members.add(num)) {
                    duplicates.add(num);
                }
            }
        }
        members.removeAll(duplicates);
        return (int)members.toArray()[0];
    }
}
```

#### 结果
![single-number-two-1](/images/leetcode/single-number-two-1.png)


### 用`XOR`收集信息，时间复杂度 $$O(n)$$，空间复杂度 $$O(n)$$
利用`XOR`位操作，可以直接把重复出现的数字全部抵消。不需要最后调用`removeAll()`函数。

#### 代码
针对这题的解法。 `Integer`和`int`切换着用，是为了避免频繁地装箱，拆箱。
```java
public class Solution {
    public int singleNumber(int[] nums) {
        Map<Integer,Integer> memo = new HashMap<>();
        int res = 0;
        for (int i = 0; i < nums.length; i++) {
            Integer num = nums[i];
            res ^= nums[i];
            if (memo.get(num) == null) {
                memo.put(num,0);
            } else {
                memo.remove(num);
                res ^= nums[i];
            }
        }
        return res;
    }
}
```

从一堆数字中找出一个不重复数字的通用解法。
```java
public class Solution {
    public int singleNumber(int[] nums) {
        Map<Integer,Integer> members = new HashMap<>();
        Map<Integer,Integer> duplicates = new HashMap<>();
        int res = 0;
        for (int i = 0; i < nums.length; i++) {
            res ^= nums[i];
            int num = nums[i];
            if (!duplicates.containsKey(num)) {
                if (members.put(num,0) != null) {
                    duplicates.put(num,0);
                    res ^= nums[i];
                }
            }
        }
        return res;
    }
}
```

#### 结果
![single-number-two-2](/images/leetcode/single-number-two-2.png)


### 收集每一位上`1`出现的次数
在每一位上，`1`出现的次数，如果不是`3`的倍数，说明落单的那个数字在这些位上是`1`。比如，对于`[5, 3, 4, 2, 4, 3, 2, 3, 2, 1, 5, 4, 5]`，
收集到的`1`出现的次数应该如下，
```
0, 0, 0, 0, 0, 1, 0, 1  // 5
0, 0, 0, 0, 0, 0, 1, 1  // 3
0, 0, 0, 0, 0, 1, 0, 0  // 4
0, 0, 0, 0, 0, 0, 1, 0  // 2
0, 0, 0, 0, 0, 1, 0, 0  // 4
0, 0, 0, 0, 0, 0, 1, 1  // 3
0, 0, 0, 0, 0, 0, 1, 0  // 2
0, 0, 0, 0, 0, 0, 1, 1  // 3
0, 0, 0, 0, 0, 0, 1, 0  // 2
0, 0, 0, 0, 0, 0, 0, 1  // 1
0, 0, 0, 0, 0, 1, 0, 0  // 4
0, 0, 0, 0, 0, 1, 0, 1  // 5
------------------------------------------
0, 0, 0, 0, 0, 6, 6, 7  // 每位上1出现的次数
```
只有最后一位不是`3`的倍数，所以只有`1`落单了。

#### 代码
```java
public class Solution {
    public int singleNumber(int[] nums) {
        int[] bitCount = new int[32];
        for (int num : nums) { // 收集每一位上1的信息
            for (int i = 0, mask = 1; i < 32; i++, num = num >> 1) {
                int eachBit = num & mask;
                if (eachBit == 1) {
                    bitCount[i] = bitCount[i] + 1;
                }
            }
        }
        int res = 0;
        for (int i = 0, mask = 1; i < 32; i++, mask = mask << 1) {
            if (bitCount[i] % 3 != 0) { // 读取每一位上1的信息
                res = res | mask; // 把信息写到答案上
            }
        }
        return res;
    }
}
```

#### 结果
![single-number-two-3](/images/leetcode/single-number-two-3.png)


### 更聪明的办法收集每一位上1出现次数
根据`Single Number`中`XOR`位操作的启发。`XOR`的本质是：**当一个数字第二次出现时，所有信息会抵消。**
```
01010101
01010101 ^
----------
00000000
```

我们的目标是：**能不能设计出第三次出现，所有信息被抵消** 的位操作？
1. 当一个数字出现第一次，会在b中体现它的信息。
2. 当它出现第二次，会在a中体现它的信息。
3. 当它出现第三次，它的信息将彻底消失。

根据这个目标，在每一位上，`fact table`如下：
```
current   incoming  next
a b            c    a b
0 0            0    0 0
0 1            0    0 1
1 0            0    1 0
0 0            1    0 1
0 1            1    1 0
1 0            1    0 0
```

根据上面的`fact table`，设计出的位操作流程为：
> a=~abc+a~b~c;

> b=~a~bc+~ab~c;

#### 代码
```java
public class Solution {
    public int singleNumber(int[] nums) {
        //  1. 当一个数字出现第一次，会在b中体现它的信息。
        //  2. 当它出现第二次，会在a中体现它的信息。
        //  3. 当它出现第三次，它的信息将彻底消失。
        //we need to implement a tree-time counter(base 3) that if a bit appears three time ,it will be zero.
        //#curent  income  ouput
        //# ab      c/c       ab/ab
        //# 00      1/0       01/00
        //# 01      1/0       10/01
        //# 10      1/0       00/10
        // a=~abc+a~b~c;
        // b=~a~bc+~ab~c;
        int a=0;
        int b=0;
        for(int c:nums){
            int ta=(~a&b&c)|(a&~b&~c);
            b=(~a&~b&c)|(~a&b&~c);
            a=ta;
        }
        //b中是只出现一次数字的所有信息。
        return b;

    }
}
```

#### 结果
变态！
![single-number-two-4](/images/leetcode/single-number-two-4.png)

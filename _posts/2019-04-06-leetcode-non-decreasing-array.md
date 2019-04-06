---
layout: post
title: "Leetcode - Algorithm - Non Decreasing Array"
date: 2019-04-06 15:06:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math"]
level: "easy"
description: >
---

### 题目
Given an array with n integers, your task is to check if it could become non-decreasing by modifying at most 1 element.

We define an array is non-decreasing if `array[i] <= array[i + 1]` holds for every `i` (1 <= i < n).

Example 1:
```
Input: [4,2,3]
Output: True
Explanation: You could modify the first 4 to 1 to get a non-decreasing array.
```

Example 2:
```
Input: [4,2,1]
Output: False
Explanation: You can't get a non-decreasing array by modify at most one element.
```

Note: The n belongs to `[1, 10,000]`.

### 基本是个数学问题
首先可以明确的是，第一次出现递减（即`nums[i + 1] < nums[i]`），肯定可以补救，比如，
```
        .
        4   .   
            3    

   .
  -1
```

第二次出现递减的情况，就可以确定无法补救。
```
        .
        4   .   
            3   .
                2     
   .
  -1
```

问题在于这个中间地带，即第一次出现递减之后，接下来的点，就算是递增的，但也有可能是不合法的，比如下面这个情况
```
            .
            4        

       .                      
       0
                    .
    .           .  -1
   -2          -2
---------------------------   
                ^
                第一次递减
```
第二个`-2`是第一次递减的位置，下一个点`-1`虽然相比`-2`是递增了，但这种情况仍然无法只改动一个数字就变成完全递增函数。

所以实际情况是：
> 当出现一次递减之后，对后续点的位置是有要求的。

这要求要分两种情况：

#####  第一种：可以保留当前点，抹掉前置点
假设数据流是`[...,-2,0,4,1,...]`，并且确保`1`是第一个递减点，之前全部递增。对`1`下一个数的要求是只要`>= 1`即可，相当于改动的那个数字是`4`，把`4`改成`[0,1]`之间的任何数字都可以。
```
            .           ^
            4           | 下一个点必须 >= 1（可以把4换成[0,1]之间的一个数）
                        |
                .--------
       .        1             
       0

    .                
   -2            
---------------------------   
                ^
                第一次递减
```

##### 第二种：无法保留当前点，必须删掉当前点
如果第一个递减的点是`-1`，也就是它小于最后一个递增点`4`的前一个点`0`，那对下一个数的要求就更高，它必须`>= 4`，这时候才能把`-1`改成`[4, nextNum]`之间的一个数来保持递增。
```
            .-------------- 下一个点必须 >= 4（必须把-1改成 >= 4 的数字）
            4            


        .                      
        0                
                 .
    .           -1    
    -2            
----------------------------
                 ^
                 第一次递减
```

#### 代码
```java
class Solution {
    public boolean checkPossibility(int[] nums) {
        boolean decreased = false;
        int min = Integer.MIN_VALUE;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < min) return false;
            if (decreased) min = nums[i];
            if (nums[i] < nums[i - 1]) {
                decreased = true;
                if (i == 1 || (i > 1 && nums[i] >= nums[i - 2])) {
                    min = nums[i];
                } else {
                    min = nums[i - 1];
                }
            }
        }
        return true;
    }
}
```

#### 结果
![non-decreasing-array-1](/images/leetcode/non-decreasing-array-1.png)

---
layout: post
title: "Leetcode - Algorithm - Product Of Array Except Self "
date: 2017-07-07 13:28:29
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given an array of n integers where `n > 1`, nums, return an array output such that `output[i]` is equal to the product of all the elements of nums except `nums[i]`.

Solve it without division and in `O(n)`.

For example, given `[1,2,3,4]`, return `[24,12,8,6]`.

Follow up:
Could you solve it with constant space complexity? (Note: The output array does not count as extra space for the purpose of space complexity analysis.)

### 用两个额外辅助数组，记录累乘结果
比如`[1,2,3,4]`，用两个数组`fromLeftTill`和`fromRightTill`记录从左右两边起的累乘结果。注意，累乘的结果是直到`i`位，但不包括`i`位。
```
原始数组：[1,2,3,4]

fromLeftTill = [1,1,2,6]
fromRightTill = [24,12,4,1]
```

#### 代码
```java
public class Solution {
    public int[] productExceptSelf(int[] nums) {
        int[] fromLeftTill = new int[nums.length];
        fromLeftTill[0] = 1;
        for (int i = 1; i < nums.length; i++) {
            fromLeftTill[i] = fromLeftTill[i-1] * nums[i-1];
        }
        int[] fromRightTill = new int[nums.length];
        fromRightTill[nums.length-1] = 1;
        for (int i = nums.length-2; i >= 0; i--) {
            fromRightTill[i] = fromRightTill[i+1] * nums[i+1];
        }
        int[] product = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            product[i] = fromLeftTill[i] * fromRightTill[i];
        }
        return product;
    }
}
```

#### 结果
![product-of-array-except-self-1](/images/leetcode/product-of-array-except-self-1.png)


### 只使用 $$O(1)$$ 额外空间的“拉链法”
还是利用记录累乘结果的思想，但不使用额外空间，直接在`int[] product`上操作，需要来回跑两次。我喜欢叫这个方法为“拉链法”，非常形象。

先从右往左，累计到`i`位的累乘结果，到`i`位，但不包括`i`位。第一遍跑有点像蓄力。
```
product = [24,12,4,1]
```

然后用一个`int productSoFarFromLeft`记录从左边起累乘到`i`位的积，同样到`i`位，但不包括`i`位。
```
productSoFarFromLeft = 1

product[0]  = product[0] * productSoFarFromLeft
            = 24 * 1
            = 24

update productSoFarFromLeft     = productSoFarFromLeft * nums[0]
                                = 1 * 1
                                = 1

经过这轮迭代：
product = [24,12,4,1]
productSoFarFromLeft = 1
```

下一轮，
```
productSoFarFromLeft = 1

product[1]  = product[1] * productSoFarFromLeft
            = 12 * 1
            = 12

update productSoFarFromLeft     = productSoFarFromLeft * nums[1]
                                = 1 * 2
                                = 2

经过这轮迭代：
product = [24,12,4,1]
productSoFarFromLeft = 2
```

下一轮，
```
productSoFarFromLeft = 2

product[2]  = product[2] * productSoFarFromLeft
            = 4 * 2
            = 8

update productSoFarFromLeft     = productSoFarFromLeft * nums[2]
                                = 2 * 3
                                = 6

经过这轮迭代：
product = [24,12,8,1]
productSoFarFromLeft = 6
```

最后一轮，

```
productSoFarFromLeft = 6

product[3]  = product[3] * productSoFarFromLeft
            = 1 * 6
            = 6

update productSoFarFromLeft     = productSoFarFromLeft * nums[3]
                                = 6 * 4
                                = 24

经过这轮迭代：
product = [24,12,8,6]
productSoFarFromLeft = 24
```


#### 代码
```java
public class Solution {
    public int[] productExceptSelf(int[] nums) {
        int[] product = new int[nums.length];
        product[nums.length-1] = 1;
        for (int i = nums.length-2; i >= 0; i--) { // go left
            product[i] = product[i+1] * nums[i+1];
        }
        int productSoFarFromLeft = 1;
        for (int i = 0; i < nums.length-1; i++) { // go back to right
            product[i] *= productSoFarFromLeft;
            productSoFarFromLeft *= nums[i];
        }
        product[nums.length-1] = productSoFarFromLeft;
        return product;
    }
}
```

#### 结果
![product-of-array-except-self-2](/images/leetcode/product-of-array-except-self-2.png)

---
layout: post
title: "Leetcode - Algorithm - One Bit And Two Bits Character "
date: 2017-12-04 17:33:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
We have two special characters. The first character can be represented by one bit 0. The second character can be represented by two bits (10 or 11).

Now given a string represented by several bits. Return whether the last character must be a one-bit character or not. The given string will always end with a zero.

Example 1:
```
Input:
bits = [1, 0, 0]
Output: True
Explanation:
The only way to decode it is two-bit character and one-bit character. So the last character is one-bit character.
```

Example 2:
```
Input:
bits = [1, 1, 1, 0]
Output: False
Explanation:
The only way to decode it is two-bit character and two-bit character. So the last character is NOT one-bit character.
```

Note:
* 1 <= len(bits) <= 1000.
* bits[i] is always 0 or 1.

### 总体思路
就是一个自底向上的动态规划。并且始终只有一个子问题，所以复杂度 $$O(n)$$ 。

#### 递归版代码
```java
class Solution {
    private int[] local = new int[0];
    private int len = 0;

    public boolean isOneBitCharacter(int[] bits) {
        local = bits;
        len = bits.length;
        return dp(0);
    }
    private boolean dp(int index) {
        if (index == len) { return false; }
        if (index == len - 1) { return true; } // assertion: always end with 0
        if (local[index] == 0) {
            return dp(index + 1);
        } else {
            return dp(index + 2);
        }
    }
}
```

#### 结果
![one-bit-and-two-bits-character-1](/images/leetcode/one-bit-and-two-bits-character-1.png)


#### 迭代版代码
```java
class Solution {
    public boolean isOneBitCharacter(int[] bits) {
        int cur = 0;
        while (cur < bits.length - 1) {
            cur = cur + ((bits[cur] == 0)? 1 : 2);
        }
        return cur == bits.length - 1;
    }
}
```

#### 结果
![one-bit-and-two-bits-character-2](/images/leetcode/one-bit-and-two-bits-character-2.png)

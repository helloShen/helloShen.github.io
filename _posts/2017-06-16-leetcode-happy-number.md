---
layout: post
title: "Leetcode - Algorithm - Happy Number "
date: 2017-06-16 22:50:37
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","math"]
level: "easy"
description: >
---

### 题目
Write an algorithm to determine if a number is "happy".

A happy number is a number defined by the following process: Starting with any positive integer, replace the number by the sum of the squares of its digits, and repeat the process until the number equals 1 (where it will stay), or it loops endlessly in a cycle which does not include 1. Those numbers for which this process ends in 1 are happy numbers.

Example: `19` is a happy number
```
1^2 + 9^2 = 82
8^2 + 2^2 = 68
6^2 + 8^2 = 100
1^2 + 0^2 + 0^2 = 1
```

### 用取余`%`切割数字
空间复杂度 $$O(n)$$，`n`是过程终止或者循环的位数。

#### 代码
```java
public class Solution {
    public boolean isHappy(int n) {
        Set<Integer> memo = new HashSet<>();
        while (n != 1) {
            int copy = n;
            int sum = 0;
            while (copy != 0) {
                int remainder = copy % 10;
                copy /= 10;
                sum += remainder * remainder;
            }
            if (!memo.add(sum)) { return false; } // find circle
            n = sum;
        }
        return true;
    }
}
```

#### 结果
![happy-number-1](/images/leetcode/happy-number-1.png)


### `walker`,`runner`追逐法，空间复杂度 $$O(1)$$
遇到这种循环的问题，总是可以用`walker`，`runner`互相追逐解决问题。`walker`每次走一步，`runner`每次走两步。如果循环，两者总会相等。

这样就省去了用`HashSet`储存所有中间值的步骤。

#### 代码
```java
public class Solution {
    public boolean isHappy(int n) {
        int slow = n, fast = n;
        do {
            slow = squareSum(slow);
            fast = squareSum(fast);
            fast = squareSum(fast);
            if (fast == 1) { return true; }
        } while (slow != fast);
        return false; // find circle, but fast != 1
    }
    public int squareSum(int n) {
        int sum = 0;
        while (n != 0) {
            int remainder = n % 10;
            sum += remainder * remainder;
            n /= 10;
        }
        return sum;
    }
}
```

#### 结果
银弹！
![happy-number-2](/images/leetcode/happy-number-2.png)

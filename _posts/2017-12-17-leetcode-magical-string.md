---
layout: post
title: "Leetcode - Algorithm - Magical String "
date: 2017-12-17 16:25:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
A magical string S consists of only '1' and '2' and obeys the following rules:

The string S is magical because concatenating the number of contiguous occurrences of characters '1' and '2' generates the string S itself.

The first few elements of string S is the following: S = "1221121221221121122……"

If we group the consecutive '1's and '2's in S, it will be:
```
1 22 11 2 1 22 1 22 11 2 11 22 ......
```
and the occurrences of '1's or '2's in each group are:
```
1 2	2 1 1 2 1 2 2 1 2 2 ......
```
You can see that the occurrence sequence above is the S itself.

Given an integer N as input, return the number of '1's in the first N number in the magical string S.

Note: N will not exceed 100,000.

Example 1:
```
Input: 6
Output: 3
Explanation: The first 6 elements of magical string S is "12211" and it contains three 1's, so return 3.
```

### 数学性质
首先Magical String可以以`1`开始，也可以从`2`开始。但其实两个序列一样，唯一的区别就是开头的那个`1`。所以一般来讲取比较完整的以`1`开头的序列为Magical String。
```
从2开始：
221121221221121122……

从1开始：
1221121221221121122……
```

然后，这个Magical String之所以是唯一的，就是因为它其实只要先给出开头的`1`，是可以计算然后写出来的。
```
1
只能有一个"1"，所以第2位一定是"2"
1 -> 1 2

第二个数字是"2"，所以必须有两个"2"，而且下一个必须是"1"，
1 2 -> 1 22 1

以此类推
```

由于这个序列是唯一的，所以不需要每次都从头开始，可以用一个数组记录目前为止推断过的序列。然后每次只要计算1的个数就好了。再懒一点连每次计算好的数量也可以用一个数组记录下来。

#### 代码
```java
class Solution {

    private static int[] memo = new int[100002]; // 多两个槽位，因为第一个数在1号槽，而且最后有可能多算一个数字
    private static int slow = 0, fast = 0;
    private static int[] res = new int[100002];
    private static int finish = 0;

    static {
        memo[1] = 1;
        slow = 1;
        fast = 1;
        res[1] = 1;
        finish = 1;
    }

    public int magicalString(int n) {
        if (n > fast) { generate(n); }
        return count(n);
    }
    private int count(int n) {
        if (n <= finish) { return res[n]; }
        for (int i = finish+1, count = res[finish]; i <= n; i++) {
            if (memo[i] == 1) { count++; }
            res[i] = count;
        }
        return res[n];
    }
    private void generate(int n) {
        int remain = n - fast;
        while (remain > 0) {
            int curr = memo[slow++];
            if (curr == 1) {
                int last = memo[fast];
                if (last == 1) {
                    memo[++fast] = 2;
                } else {
                    memo[++fast] = 1;
                }
                remain--;
            } else {
                int last = memo[fast];
                if (last == 1) {
                    memo[++fast] = 1;
                    memo[++fast] = 2;
                } else {
                    memo[++fast] = 2;
                    memo[++fast] = 1;
                }
                remain -= 2;
            }
        }
    }
}
```

#### 结果
![magical-string-1](/images/leetcode/magical-string-1.png)

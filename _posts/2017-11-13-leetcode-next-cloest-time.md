---
layout: post
title: "Leetcode - Algorithm - Next Cloest Time "
date: 2017-11-13 19:46:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "medium"
description: >
---

### 题目
Given a time represented in the format "HH:MM", form the next closest time by reusing the current digits. There is no limit on how many times a digit can be reused.

You may assume the given input string is always valid. For example, "01:34", "12:09" are all valid. "1:34", "12:9" are all invalid.

Example 1:
```
Input: "19:34"
Output: "19:39"
Explanation: The next closest time choosing from digits 1, 9, 3, 4, is 19:39, which occurs 5 minutes later.  It is not 19:33, because this occurs 23 hours and 59 minutes later.
```

Example 2:
```
Input: "23:59"
Output: "22:22"
Explanation: The next closest time choosing from digits 2, 3, 5, 9, is 22:22. It may be assumed that the returned time is next day's time since it is smaller than the input time numerically.
```

### 基本思路
最笨的办法就是让时间自然地流逝，然后检查每一个时间是否是由特定的几个数字构成的。

稍微聪明一点，可以用有限的几个数字构造出可能的组合，然后判断他们是否合法。

但因为时间的写法规则很严格，每一位都有上限，所以最好的办法反而是启发式地去找可以进位的那一位，然后填充余下的部分。

### 启发式解法
核心事实就是每一位都有最高的上限，

1. if the first digit is `0` or `1`: Max = `19:59`
2. if the first digit is `2`: Max = `23:59`

如果我们拿到`14:39`, 先排序 -> `[1,3,4,9]`，然后找到可以进位的那一位，比如: `14:39` -> `14:49`, 然后余下的位都用最小的数字填充: `14:49 -> 14:41`。

#### 代码
```java
public String nextClosestTime(String time) {
    char[] charTime = time.toCharArray();   // [1, 9, :, 3, 4]
    char[] nums = Arrays.copyOf(charTime,charTime.length);
    Arrays.sort(nums);                      // [1, 3, 4, 9, :]
    /** the carry */
    int i = 0;
    outFor:
    for (i = 4; i >= 0; i--) {
        if (i == 2) { continue; } // skip ":" in the middle
        char c = charTime[i];
        int next = 0;
        while (next < 4 && nums[next] <= c) { next++; }
        // maximum legal value
        char max = '9';
        switch (i) {
            case 3: max = '5'; break;
            case 1: if (charTime[0] == '2') { max = '4'; } break;
            case 0: max = '2'; break;
        }
        if (next == 4 || nums[next] > max) { continue outFor; }
        charTime[i] = nums[next];
        break;
    }
    /** fill the remainder with smallest digit */
    for (int j = i + 1; j <= 4; j++) {
        if (j == 2) { continue; }
        charTime[j] = nums[0]; // at least one of [0,1,2]
    }
    return new String(charTime);
}
```

#### 结果
![next-cloest-time-1](/images/leetcode/next-cloest-time-1.png)

---
layout: post
title: "Leetcode - Algorithm - Swap Adjacent In Lr String "
date: 2019-03-01 21:37:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "math"]
level: "medium"
description: >
---

### 题目
In a string composed of 'L', 'R', and 'X' characters, like "RXXLRXRXL", a move consists of either replacing one occurrence of "XL" with "LX", or replacing one occurrence of "RX" with "XR". Given the starting string start and the ending string end, return True if and only if there exists a sequence of moves to transform one string to the other.

Example:
```
Input: start = "RXXLRXRXL", end = "XRLXXRRLX"
Output: True
Explanation:
We can transform start to end following these steps:
RXXLRXRXL ->
XRXLRXRXL ->
XRLXRXRXL ->
XRLXXRRXL ->
XRLXXRRLX
```
Note:
* 1 <= len(start) = len(end) <= 10000.
* Both start and end will only consist of characters in {'L', 'R', 'X'}.

### BFS真的变形
一开始我用了个傻办法，暴力检查所有可能的变形。遇到不相等的变形就记录下来，避免循环。

#### 代码
```java
public boolean canTransform(String start, String end) {
    Set<String> history = new HashSet<>();
    List<String> level = new LinkedList<>();
    level.add(start);
    while (!level.isEmpty()) {
        int size = level.size();
        for (int i = 0; i < size; i++) {
            String str = level.remove(0);
            char[] ca = str.toCharArray();
            if (str.equals(end)) return true;
            history.add(str);
            for (int j = 0; j < ca.length - 1; j++) {
                if (ca[j] == 'R' && ca[j + 1] == 'X') {
                    ca[j] = 'X';
                    ca[j + 1] = 'R';
                    level.add(new String(ca));
                    ca[j] = 'R';
                    ca[j + 1] = 'X';
                } else if (ca[j] == 'X' && ca[j + 1] == 'L') {
                    ca[j] = 'L';
                    ca[j + 1] = 'X';
                    level.add(new String(ca));
                    ca[j] = 'X';
                    ca[j + 1] = 'L';
                }
            }
        }
    }
    return false;
}
```

#### 结果
![swap-adjacent-in-lr-string-1](/images/leetcode/swap-adjacent-in-lr-string-1.png)


### 变形是有规律的
仔细观察可以发现两个基本规则：
1. `R`只可以右移。可以穿越任意多的`X`，但不能穿越`L`和`R`。
2. `L`只可以左移。可以穿越任意多的`X`，但不能穿越`L`和`R`。

直观地说`RXXXRXXXXL`中，
```
首个R的活动范围
|---->|
RXXXXXXRXXXXXXXL
        |<-----|
        末尾L的活动范围
```

根据这个特性可以得出两个推论，
1. 所有变形中`R`和`L`的数量以及相对顺序保持不变。
2. `R`在原始串中的位置只会比变形中对位的`R`更靠右。同理，`L`只会更靠左。

这样我们可以遍历一次字符串，然后统计所有`R`和`L`的位置。就可以知道是否合法。

#### 代码
```java
class Solution {
    public boolean canTransform(String start, String end) {
        if (start.length() != end.length()) return false;
        List<Integer> siL = new LinkedList<>();
        List<Integer> eiL = new LinkedList<>();
        List<Integer> siR = new LinkedList<>();
        List<Integer> eiR = new LinkedList<>();
        StringBuilder compressedStart = new StringBuilder();
        StringBuilder compressedEnd = new StringBuilder();
        for (int i = 0; i < start.length(); i++) {
            char si = start.charAt(i);
            if (si == 'L') {
                siL.add(i);
                compressedStart.append('L');
            } else if (si == 'R') {
                siR.add(i);
                compressedStart.append('R');
            }
            char ei = end.charAt(i);
            if (ei == 'L') {
                eiL.add(i);
                compressedEnd.append('L');
            } else if (ei == 'R') {
                eiR.add(i);
                compressedEnd.append('R');
            }
        }
        if (!compressedStart.toString().equals(compressedEnd.toString())) return false;
        for (int i = 0; i < siL.size(); i++) {
            if (siL.get(i) < eiL.get(i)) return false;
        }
        for (int i = 0; i < siR.size(); i++) {
            if (siR.get(i) > eiR.get(i)) return false;
        }
        return true;
    }
}
```

#### 结果
![swap-adjacent-in-lr-string-2](/images/leetcode/swap-adjacent-in-lr-string-2.png)


### 不需要额外的空间统计
更聪明一点，甚至不需要做统计。只需要两根指针，同步找到下一个对位的`非X`，对比是否同为`L`或`R`，并且比较两者的绝对偏移值即可。

#### 代码
```java
class Solution {
    public boolean canTransform(String start, String end) {
        if (start.length() != end.length()) return false;
        int len = start.length();
        for (int i = 0, j = 0; i < len && j < len; i++,j++) {
            while (i < len && start.charAt(i) == 'X') i++;
            while (j < len && end.charAt(j) == 'X') j++;
            if (i == len && j == len) return true;
            if (i == len || j == len) return false;
            char sc = start.charAt(i), ec = end.charAt(j);
            if (sc != ec || (sc == 'R' && i > j) || (sc == 'L' && i < j)) return false;
        }
        return true;
    }
}
```

#### 结果
![swap-adjacent-in-lr-string-3](/images/leetcode/swap-adjacent-in-lr-string-3.png)

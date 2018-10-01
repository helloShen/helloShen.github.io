---
layout: post
title: "Leetcode - Algorithm - Minimum Ascii Delete Sum "
date: 2018-10-01 02:53:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Given two strings s1, s2, find the lowest ASCII sum of deleted characters to make two strings equal.

Example 1:
```
Input: s1 = "sea", s2 = "eat"
Output: 231
Explanation: Deleting "s" from "sea" adds the ASCII value of "s" (115) to the sum.
Deleting "t" from "eat" adds 116 to the sum.
At the end, both strings are equal, and 115 + 116 = 231 is the minimum sum possible to achieve this.
```

Example 2:
```
Input: s1 = "delete", s2 = "leet"
Output: 403
Explanation: Deleting "dee" from "delete" to turn the string into "let",
adds 100[d]+101[e]+101[e] to the sum.  Deleting "e" from "leet" adds 101[e] to the sum.
At the end, both strings are equal to "let", and the answer is 100+101+101+101 = 403.
If instead we turned both strings into "lee" or "eet", we would get answers of 433 or 417, which are higher.
```

Note:
* 0 < s1.length, s2.length <= 1000.
* All elements of each string will have an ASCII value in [97, 122].


### 暴力回溯算法
这种问题暴力总是能解决问题，但不一定能通过。用一个`HashMap`储存所有子串对应的删除字符和。比对`s1`和`s2`对应的`Map`，找出重合的记录。
![minimum-ascii-delete-sum-b](/images/leetcode/minimum-ascii-delete-sum-b.png)


#### 代码
```java
class Solution {
    public int minimumDeleteSum(String s1, String s2) {
        Map<String, Integer> shorter = new HashMap<>();
        Map<String, Integer> longer = new HashMap<>();
        if (s1.length() >= s2.length()) {
            longer.put(s1, 0);
            shorter.put(s2, 0);
            longer = removeNChar(s1.length() - s2.length(), longer);
        } else {
            longer.put(s2, 0);
            shorter.put(s1, 0);
            longer = removeNChar(s2.length() - s1.length(), longer);
        }
        int min = Math.min(s1.length(), s2.length());
        boolean matched = false;
        int res = Integer.MAX_VALUE;
        for (int i = 0; i <= min; i++) {
            for (Map.Entry<String, Integer> entry : shorter.entrySet()) {
                if (longer.containsKey(entry.getKey())) {
                    res = Math.min(res, entry.getValue() + longer.get(entry.getKey()));
                    matched = true;
                }
            }
            if (matched) return res;
            shorter = removeOneChar(shorter);
            longer = removeOneChar(longer);
        }
        return 0; // never reached
    }
    private Map<String, Integer> removeNChar(int n, Map<String, Integer> table) {
        for (int i = 0; i < n; i++) {
            table = removeOneChar(table);
        }
        return table;
    }
    private Map<String, Integer> removeOneChar(Map<String, Integer> table) {
        Map<String, Integer> newTable = new HashMap<>();
        for (Map.Entry<String, Integer> entry : table.entrySet()) {
            StringBuilder sb = new StringBuilder(entry.getKey());
            int sum = entry.getValue();
            for (int i = 0; i < sb.length(); i++) {
                char c = sb.charAt(i);
                sb.delete(i, i + 1);
                if (!newTable.containsKey(sb.toString())) {
                    newTable.put(sb.toString(), sum + (int) c);
                }
                sb.insert(i, c);
            }
        }
        return newTable;
    }
}
```

#### 结果
![minimum-ascii-delete-sum-1](/images/leetcode/minimum-ascii-delete-sum-1.png)


### 最短编辑距离（动态规划）
用一个二维数组`dp[i + 1][j + 1]`代表`s1`前`i`个字符 和`s2`前`j`个字符实现相同子串`sub-s1 == sub-s2`所需要删除的ASCII编码之和。 最终，`dp[i + 1][j + 1]`就是动态规划得到的结果。

其中第一行空出来，`dp[0][j]`代表`s1`为空的时候，`s2`前`j`个字符对`s1`的编辑距离（确定唯一解，就是全部删除）。同理，第一列也要空出来。

考虑下面这个例子，`s1 = leet`， `s2 = delete`。如下图所示，
![minimum-ascii-delete-sum-a](/images/leetcode/minimum-ascii-delete-sum-a.png)

动态规划`d[i][j]`可能由以下三种子问题推演得到：
1. `dp[i - 1][j] + s1[i]`（图中黄色）：这一列竖直对位下来，前面这个`d`和空字符`_`对位所以被删掉了，现在多来了一个`l`，也要删掉，所以删两个`d + l`。
2. `dp[i][j - 1] + s2[j]`（图中蓝色）：这一行横向对位过来，前面的`l`和空字符`_`对位被删掉，现在又来了一个`d`，所以也删掉，就删两个`l + d`。
3. `dp[i - 1][j - 1] + x`（图中紫色）：又要分两种情况：1）`s1[i] == s2[j]`时，相当于当前两位对上了，都不删除。所以继承左斜上角的子问题解。 2）`s1[i] != s2[j]`时，就是图中的情况，`l != d`，就是在左斜上角的基础上再删除当前来的两位。因为左斜上角子问题都是空字符，所以就删除`l + d`。

以上三种情况取最小值，作为`d[i][j]`最优解。

#### 代码
```java
class Solution {
    public int minimumDeleteSum(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 1; i <= s1.length(); i++) {
            dp[i][0] = dp[i - 1][0] + (int) s1.charAt(i - 1);
        }
        for (int j = 1; j <= s2.length(); j++) {
            dp[0][j] = dp[0][j - 1] + (int) s2.charAt(j - 1);
        }
        for (int i = 1; i <= s1.length(); i++) {
            char cRow = s1.charAt(i - 1);
            for (int j = 1; j <= s2.length(); j++) {
                char cCol = s2.charAt(j - 1);
                int fromUpper = dp[i - 1][j] + cRow;
                int fromLeft = dp[i][j - 1] + cCol;
                dp[i][j] = Math.min(fromUpper, fromLeft);
                int fromBevel = 0;
                if (cRow == cCol) {
                    fromBevel = dp[i - 1][j - 1];
                } else {
                    fromBevel = dp[i - 1][j - 1] + cRow + cCol;
                }
                dp[i][j] = Math.min(dp[i][j], fromBevel);
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
```

#### 结果
![minimum-ascii-delete-sum-2](/images/leetcode/minimum-ascii-delete-sum-2.png)


### 只用两个一维数组
因为上面动态规划的方法是系统性地在填表，每次计算涉及到的表格只有当前行以及前一行。所以只需要两个一维数组即可。如果想最大程度节省空间，可以先选出比较短的那个字符串，以它的长度来构建以为数组。

#### 代码
```java
class Solution {
    public int minimumDeleteSum(String s1, String s2) {
        int[] lineA = new int[s2.length() + 1];
        int[] lineB = new int[s2.length() + 1];
        for (int i = 1; i <= s2.length(); i++) {
            lineA[i] = lineA[i - 1] + s2.charAt(i - 1);
        }
        for (int i = 1; i <= s1.length(); i++) {
            char cRow = s1.charAt(i - 1);
            lineB[0] = lineA[0] + cRow;
            for (int j = 1; j <= s2.length(); j++) {
                char cCol = s2.charAt(j - 1);
                int fromUpper = lineA[j] + cRow;
                int fromLeft = lineB[j - 1] + cCol;
                lineB[j] = Math.min(fromUpper, fromLeft);
                int fromBevel = 0;
                if (cRow == cCol) {
                    fromBevel = lineA[j - 1];
                } else {
                    fromBevel = lineA[j - 1] + cRow + cCol;
                }
                lineB[j] = Math.min(lineB[j], fromBevel);
            }
            int[] lineTemp = lineA;
            lineA = lineB;
            lineB = lineTemp;
        }
        return lineA[s2.length()];
    }
}
```

#### 结果
![minimum-ascii-delete-sum-3](/images/leetcode/minimum-ascii-delete-sum-3.png)

---
layout: post
title: "Leetcode - Algorithm - One Edit Distance "
date: 2017-11-09 21:21:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","string"]
level: "medium"
description: >
---

### 题目
Given two strings S and T, determine if they are both one edit distance apart.

### 字符串的编辑距离
以下3种操作中的任何一种都记为一次编辑，
1. insert: 插入一个字符
2. modify: 修改一个字符
3. delete: 删除一个字符

比如我们有`abcd`，下面3个字符串和`abcd`都只有1个编辑距离，
1. `acd`: 删除了`b`
2. `adcd`: `b`修改成`d`
3. `aabcd`: 在`b`之前插入了`a`

### 动态规划
假设我们有字符串`String s = "abcd"`， `String t = "aabcd"`，先建立下面的二维数组，
```
  |   a b c d     s
 -+--------------
  | 0 1 2 3 4
 a| 1 0 0 0 0
 a| 2 0 0 0 0
 b| 3 0 0 0 0
 c| 4 0 0 0 0
 d| 5 0 0 0 0
  |
 t
```

然后开始逐步填充表格，每个元素`[x,y]`的值，都取决于它周围的3个元素的值，
1. 上点`upper`: [x-1,y]
2. 左点`left`: [x,y-1]
3. 左上角点`corner`: [x-1,y-1]

算法为：
> Val[x,y] = Min([x-1,y]+1,[x,y-1]+1), (t[x-1] == s[y-1])? [x-1,y-1] : [x-1,y-1] + 1

#### 代码
```java
class Solution {
    public boolean isOneEditDistance(String s, String t) {
        int ls = s.length(), lt = t.length();
        if (Math.abs(ls-lt) > 1) { return false; }
        if (s.equals(t)) { return false; }
        int[][] matrix = new int[ls+1][lt+1];
        for (int i = 1; i <= lt; i++) {
            matrix[0][i] = matrix[0][i-1] + 1;
        }
        for (int i = 1; i <= ls; i++) {
            matrix[i][0] = matrix[i-1][0] + 1;
        }
        for (int i = 1; i <= ls; i++) {
            for (int j = 1; j <= lt; j++) {
                matrix[i][j] = Math.min(Math.min(matrix[i-1][j],matrix[i][j-1]) + 1, (s.charAt(i-1) == t.charAt(j-1))? matrix[i-1][j-1] : matrix[i-1][j-1] + 1);
            }
        }
        return (matrix[ls][lt] == 1)? true : false;
    }
}
```

#### 结果
![one-edit-distance-1](/images/leetcode/one-edit-distance-1.png)


### 用一维数组就能完成动态规划

#### 代码
```java
class Solution {

    public boolean isOneEditDistance(String s, String t) {
        int ls = s.length(), lt = t.length();
        if (Math.abs(ls-lt) > 1) { return false; }
        if (s.equals(lt)) { return false; }
        int longer = Math.max(ls,lt);
        int shorter = Math.min(ls,lt);
        if (s.length() < t.length()) { // make sure s is the longer string
            String temp = s;
            s = t;
            t = temp;
        }
        int min = 0;
        int[] memo = new int[longer+1];
        for (int i = 1; i <= longer; i++) {
            memo[i] = memo[i-1] + 1;
        }
        int corner = 0, col = 0;
        for (int i = 1; i <= shorter; i++) {
            corner = i - 1; col = i;
            if (corner > 1 && min > 1) { // 剪枝
                return false;
            }
            min = corner;
            for (int j = 1; j <= longer; j++) {
                int oldVal = memo[j];
                memo[j] = Math.min(Math.min(memo[j],col) + 1, (t.charAt(i-1) == s.charAt(j-1))? corner : corner + 1);
                min = Math.min(min, memo[j]);
                corner = oldVal;
                col = memo[j];
            }
        }
        return (memo[longer] == 1)? true : false;
    }
}
```

#### 结果
![one-edit-distance-1](/images/leetcode/one-edit-distance-1.png)


### 递归动态规划

#### 代码
```java
class Solution {

    private String strS;
    private String strT;
    private int lenS;
    private int lenT;

    public boolean isOneEditDistance(String s, String t) {
        strS = s;
        strT = t;
        lenS = s.length();
        lenT = t.length();
        int diff = lenS - lenT;
        if (diff < -1 || diff >1) { return false; }
        int distance = dp(0,0);
        return distance == 1;
    }

    private int dp(int ps, int pt) {
        if (ps == lenS && pt == lenT) { return 0; }
        int movePs = Integer.MAX_VALUE, movePt = movePs, moveBoth = movePs;
        if (ps < lenS) { movePs = dp(ps+1,pt) + 1; }
        if (pt < lenT) { movePt = dp(ps,pt+1) + 1; }
        if (ps < lenS && pt < lenT) { moveBoth = dp(ps+1,pt+1) + ((strS.charAt(ps) == strT.charAt(pt))? 0 : 1); }
        return Math.min(Math.min(movePs,movePt),moveBoth);
    }

}
```

#### 结果
![one-edit-distance-1](/images/leetcode/one-edit-distance-1.png)

### 反而朴素的启发式方法能通过
之前的方法都是基于动态规划。能完整地计算两个字符串之间的编辑距离。但这道题只需要考虑编辑距离为`1`的情况。所以不用动态规划，朴素地用几个`if-else`逻辑分支就能分析很少的几种可能情况。

#### 代码
```java
class Solution {

    public boolean isOneEditDistance(String s, String t) {
        int lenS = s.length();
        int lenT = t.length();
        int diff = lenS - lenT;
        if (diff < -1 || diff > 1) { return false; }
        for (int i = 0; i < Math.min(lenS,lenT); i++) {
            if (s.charAt(i) != t.charAt(i)) {
                if ((diff == 0) && s.substring(i+1).equals(t.substring(i+1))) { return true; }
                if ((diff == 1) && s.substring(i+1).equals(t.substring(i))) { return true; }
                if ((diff == -1) && s.substring(i).equals(t.substring(i+1))) { return true; }
                return false;
            }
        }
        return diff != 0;
    }

}
```

#### 结果
![one-edit-distance-2](/images/leetcode/one-edit-distance-2.png)

---
layout: post
title: "Leetcode - Algorithm - Palindrome Partitioning "
date: 2017-05-22 01:38:22
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","dynamic programming","backtracking"]
level: "medium"
description: >
---

### 主要收获
对问题的 **抽象** 是设计算法的关键。经常可以想想看能不能抽象成 **一系列策略的组合**。因为策略的组合问题，经常可以用 **回溯算法** 来解决。 而可以用回溯的问题，经常可以用 **动态规划** 来优化。

### 题目
Given a string s, partition s such that every substring of the partition is a palindrome.

Return all possible palindrome partitioning of s.

For example, given s = "aab",
Return

[
  ["aa","b"],
  ["a","a","b"]
]

### 第一种抽象：每两个字符之间可以切割，也可以不切割
假设有`baabbaab`，长度为`8`，中间有`7`个位置可以决定切割还是不切割。
```
b[1]a[2]a[3]b[4]b[5]a[6]a[7]b
```
我可以用一个类似`bitmap`的数据结构，表示我在每个位置的决策组合。所有的策略组合从`0000000`到`1111111`。

最暴力的做法，就是测试每一个策略组合。

#### 代码
```java
public class Solution {
    private class BooleanMap {
        private boolean[] table;
        private int start, end;
        private BooleanMap(int size) {
            table = new boolean[size+1]; // 最高位为符号位，表示是否满了。
            start = 0; end = start+1;
        }
        public boolean hasNext() {
            return start < table.length;
        }
        public int[] next() {
            while (end < table.length && !table[end]) { end++; }
            int[] res = new int[]{ start,end };
            start = end; end++;
            return res;
        }
        public boolean plus() {
            table[table.length-1] = !table[table.length-1];
            int cur = table.length-1;
            while (!table[cur]) { table[cur-1] = !table[cur-1]; cur--; }
            start = 0; end = 1;
            if (table[0]) {
                return false;
            } else {
                return true;
            }
        }
    }
    public List<List<String>> partition(String s) {
        List<List<String>> res = new ArrayList<>();
        if (s == null || s.isEmpty()) { return res; }
        char[] c = s.toCharArray();
        BooleanMap map = new BooleanMap(c.length-1);
        List<String> group = new ArrayList<>();
        outerLoop:
        do {
            group.clear();
            innerLoop:
            while (map.hasNext()) {
                int[] range = map.next();
                if (!isPalindrome(c,range[0],range[1]-1)) { // 有String不是回文
                    continue outerLoop;
                } else {
                    group.add(new String(Arrays.copyOfRange(c,range[0],range[1])));
                }
            }
            res.add(new ArrayList<String>(group));
        } while (map.plus());
        return res;
    }
    public boolean isPalindrome(char[] c, int lo, int hi) {
        while (lo <= hi) {
            if (c[lo] != c[hi]) {
                return false;
            } else {
                lo++; hi--;
            }
        }
        return true;
    }
}
```

#### 结果
![palindrome-partitioning-1](/images/leetcode/palindrome-partitioning-1.png)


### 第二种抽象：递归动态规划
通过观察可以发现，对于字符串，比如`baabbaab`，

* 如果最高位`b`是回文，那么子问题`aabbaab`的所有解，加上一个`b`也都是正确解。
* 如果最高位`ba`是回文，那么子问题`abbaab`的所有解，加上一个`ba`也都是正确解。
* 如果最高位`baa`是回文，那么子问题`bbaab`的所有解，加上一个`baa`也都是正确解。
* 以此类推... ...
* 到最后，如果最高位`baabbaab`是回文，子问题为空，就直接加上`baabbaab`一个解。

这个问题更好的一个抽象是下面这个递归式：
> $$T(n) = T(n-1) + T(n-2) + ... + T(1)$$

可以说，`T(n)`的解，取决于`T(n-1)`，`T(n-2)`...`T(1)`所有子串的解。这种情况 **自底向上的动态规划** 是最好的解决办法。

要解`T(n)`，先递出去，从`T(1)`,`T(2)`,`T(3)`...`T(n)`一路收回来。

#### 代码
```java
public class Solution {
    public List<List<String>> partition(String s) {
        List<List<String>> res = new ArrayList<>();
        if (s == null || s.isEmpty()) { return res; }
        int len = s.length();
        List<List<List<String>>> memo = new ArrayList<>();
        List<List<String>> group = new ArrayList<>();
        group.add(new ArrayList<String>());
        memo.add(0,group);
        dp(s,0,memo);
        return memo.get(0);
    }
    public void dp(String s, int cur, List<List<List<String>>> memo) {
        if (cur == s.length()) { return; }
        dp(s,cur+1,memo);
        List<List<String>> res = new ArrayList<>();
        for (int i = cur+1; i <= s.length(); i++) {
            String str = s.substring(cur,i);
            if (isPalindrome(str)) {
                List<List<String>> last = memo.get(i-cur-1);
                for (List<String> group : last) {
                    List<String> newGroup = new ArrayList<>(group);
                    newGroup.add(0,str);
                    res.add(newGroup);
                }
            }
        }
        memo.add(0,res);
    }
    public boolean isPalindrome(String s) {
        for (int lo = 0, hi = s.length()-1; lo <= hi; lo++,hi--) {
            if (s.charAt(lo) != s.charAt(hi)) { return false; }
        }
        return true;
    }
}
```

#### 结果
已经接近于最优解。
![palindrome-partitioning-2](/images/leetcode/palindrome-partitioning-2.png)


### 动态规划的小小优化
对于字符串`s`，判断某个子串是否是回文，也可以利用一个带备忘录的动态规划，用历史数据简化判断回文串的过程。比如，判断`baabbaab`的子串中`s[4]=b`到`s[7]=b`是否是回文串，只需要判断是否`s[4]==s[7]`，以及查找历史记录`s[5]~s[6]`是否是回文串，就能得出结果。

#### 代码
```java
public class Solution {
    private boolean[][] isPal;
    private List<List<List<String>>> memo;
    public List<List<String>> partition(String s) {
        List<List<String>> res = new ArrayList<>();
        if (s == null || s.isEmpty()) { return res; }
        int len = s.length();
        memo = new ArrayList<>();
        List<List<String>> group = new ArrayList<>();
        group.add(new ArrayList<String>());
        memo.add(0,group);
        isPal = new boolean[len][len];
        dp(s,0);
        return memo.get(0);
    }
    public void dp(String s, int left) {
        if (left == s.length()) { return; }
        dp(s,left+1);
        List<List<String>> res = new ArrayList<>();
        for (int right = left; right < s.length(); right++) {
            String sub = s.substring(left,right+1);
            if (isPalindrome(s,left,right)) {
                isPal[left][right] = true;
                List<List<String>> last = memo.get(right-left);
                for (List<String> group : last) {
                    List<String> newGroup = new ArrayList<>(group);
                    newGroup.add(0,sub);
                    res.add(newGroup);
                }
            }
        }
        memo.add(0,res);
    }
    public boolean isPalindrome(String s, int lo, int hi) { // 也使用了备忘录
        return (s.charAt(lo) == s.charAt(hi) && (hi-lo < 2 || isPal[lo+1][hi-1]));
    }
}
```

#### 结果
确实有效果。
![palindrome-partitioning-3](/images/leetcode/palindrome-partitioning-3.png)

---
layout: post
title: "Leetcode - Algorithm - Longest K Substring "
date: 2017-09-21 00:47:38
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["divide and conquer","heap"]
level: "medium"
description: >
---

### 主要收获 - 分治法
> 递归分治，递归的深度越浅越好。

### 题目
Find the length of the longest substring T of a given string (consists of lowercase letters only) such that every character in T appears no less than k times.

Example 1:
```
Input:
s = "aaabb", k = 3

Output:
3
```

The longest substring is "aaa", as 'a' is repeated 3 times.
Example 2:
```
Input:
s = "ababbc", k = 2

Output:
5
```

The longest substring is "ababb", as 'a' is repeated 2 times and 'b' is repeated 3 times.


### 用`Heap`（`PriorityQueue`来实现）
可以用两个指针，套嵌循环遍历所有的子串，然后维护一个字母频率的`Min-Heap`。只有最小频率`>= k`才满足条件。`Heap`的实现用`PriorityQueue`。

#### 代码
```java
class Solution {
    private int[] letters = new int[0];
    public int longestSubstring(String s, int k) {
        int max = 0, len = s.length();
        for (int i = 0; i <= len - k; i++) {
            letters = new int[26];
            for (int j = i; j < len; j++) {
                letters[s.charAt(j) - 'a']++;
                if (checkMin() >= k) {
                    max = Math.max(max,j - i + 1);
                }
            }
        }
        return max;
    }
    private int checkMin() {
        Integer min = null;
        for (int i = 0; i < 26; i++) {
            if (letters[i] > 0) {
                min = (min == null)? letters[i] : Math.min(min,letters[i]);
            }
        }
        return (min == null)? 0 : min;
    }
}
```

#### 结果
![longest-k-substring-1](/images/leetcode/longest-k-substring-1.png)


### 用数组替代`PriorityQueue`
因为所有的字符都为小写字母，所以可以用一个`int[26]`来替代`Min-Heap`。

#### 代码
```java
class Solution {
    public int longestSubstring(String s, int k) {
        int[] letters = new int[26];
        int max = 0, len = s.length();
        for (int i = 0; i < len; i++) {
            letters[s.charAt(i) - 'a']++;
            if (checkMin(letters) >= k) {
                max = Math.max(max,i + 1);
            }
        }
        for (int i = 0; i < len - k; i++) {
            letters[s.charAt(i) - 'a']--;
            int[] copy = Arrays.copyOf(letters,26);
            if (checkMin(copy) >= k) {
                max = Math.max(max,len - i - 1);
            }
            for (int j = len - 1; j > i; j--) {
                copy[s.charAt(j) - 'a']--;
                if (checkMin(copy) >= k) {
                    max = Math.max(max,j - i - 1);
                }
            }
        }
        return max;
    }
    private int checkMin(int[] letters) {
        Integer min = null;
        for (int i = 0; i < 26; i++) {
            if (letters[i] > 0) {
                min = (min == null)? letters[i] : Math.min(min,letters[i]);
            }
        }
        return (min == null)? 0 : min;
    }
}
```

#### 结果
![longest-k-substring-2](/images/leetcode/longest-k-substring-2.png)


### 分治法（二分递归）
这题可以用分治法，基于下面这个事实：
> 如果有些字母在整个字符串范围的总频率小于k，则可以以这些字符为分界，分割成更小的子问题。如果不存在这样的字母，则整个字符串都满足条件。

**注意！** 这个版本的分治是 **二分的**，即一旦找到一个分割数，就向下分割成两个子问题，分别向下递归。这样做的缺点是，递归深度会比较深，需要多次重复统计字符频率。效率较低。

和二分分治作为对比的是最后给出的 **多项分治** 的解法，即每一层有`n`个分割数，就分割成`n+1`个子问题。减少递归深度和字符频率统计次数。

#### 代码
```java
class Solution {
    private static int[] letters = new int[26];
    private static char[] str = new char[0];
    private static int limit = 0;
    public int longestSubstring(String s, int k) {
        str = s.toCharArray();
        limit = k;
        return recursion(0,s.length());
    }
    /** (start,end] */
    private int recursion(int start, int end) {
        if (end - start < limit) { return 0; }
        Arrays.fill(letters,0);
        for (int i = start; i < end; i++) {
            letters[str[i]-'a']++;
        }
        for (int i = start; i < end; i++) {
            int freq = letters[str[i]-'a'];
            if (freq > 0 && freq < limit) {
                return Math.max(recursion(start,i), recursion(i+1,end));
            }
        }
        return end - start;
    }
}
```

#### 结果
![longest-k-substring-3](/images/leetcode/longest-k-substring-3.png)

### 多项分治
在测试数据规模不大的情况下，比二分分治快了20倍。

#### 代码
```java
class Solution {
    private char[] str = new char[0];
    private int limit = 0;
    public int longestSubstring(String s, int k) {
        str = s.toCharArray();
        limit = k;
        return recursion(0,s.length());
    }
    /** (start,end] */
    private int recursion(int start, int end) {
        if (end - start < limit) { return 0; }
        int[] letters = new int[26];
        for (int i = start; i < end; i++) {
            letters[str[i] - 'a']++;
        }
        int max = 0;
        int lo = start, hi = start;
        while (hi < end) {
            int freq = letters[str[hi]-'a'];
            if (freq > 0 && freq < limit) {
                max = Math.max(max,recursion(lo,hi));
                lo = hi + 1;
            }
            ++hi;
        }
        if (lo < end && lo != start) {
            max = Math.max(max,recursion(lo,end));
        } else if (lo < end){
            max = end - start;
        }
        return max;
    }
}
```

#### 结果
![longest-k-substring-4](/images/leetcode/longest-k-substring-4.png)

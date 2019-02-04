---
layout: post
title: "Leetcode - Algorithm - Find And Replace In String "
date: 2019-02-03 21:08:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "array", "sort"]
level: "medium"
description: >
---

### 题目
To some string S, we will perform some replacement operations that replace groups of letters with new ones (not necessarily the same size).

Each replacement operation has 3 parameters: a starting index i, a source word x and a target word y.  The rule is that if x starts at position i in the original string S, then we will replace that occurrence of x with y.  If not, we do nothing.

For example, if we have S = "abcd" and we have some replacement operation i = 2, x = "cd", y = "ffff", then because "cd" starts at position 2 in the original string S, we will replace it with "ffff".

Using another example on S = "abcd", if we have both the replacement operation i = 0, x = "ab", y = "eee", as well as another replacement operation i = 2, x = "ec", y = "ffff", this second operation does nothing because in the original string S[2] = 'c', which doesn't match x[0] = 'e'.

All these operations occur simultaneously.  It's guaranteed that there won't be any overlap in replacement: for example, S = "abc", indexes = [0, 1], sources = ["ab","bc"] is not a valid test case.

Example 1:
```
Input: S = "abcd", indexes = [0,2], sources = ["a","cd"], targets = ["eee","ffff"]
Output: "eeebffff"
Explanation: "a" starts at index 0 in S, so it's replaced by "eee".
"cd" starts at index 2 in S, so it's replaced by "ffff".
```

Example 2:
```
Input: S = "abcd", indexes = [0,2], sources = ["ab","ec"], targets = ["eee","ffff"]
Output: "eeecd"
Explanation: "ab" starts at index 0 in S, so it's replaced by "eee".
"ec" doesn't starts at index 2 in the original S, so we do nothing.
```

Notes:
* 0 <= indexes.length = sources.length = targets.length <= 100
* 0 < indexes[i] < S.length <= 1000
* All characters in given inputs are lowercase letters.

### 关键在于下标会变
这题主要需要处理的问题是题目给出的下标对应的是原字符串的，在部分字符替换之后，需要对下标进行修正。因此，只需记录每次替换后导致的偏移值的改变即可。以给出的例子`abcd`为例，
```
初始化偏移值修改数组：
[1001, 1001, 1001, 1001]

当`a`替换为`eee`，所有在下标`0`之后的字符，向后偏移了`2`个位置，因此，偏移数组变为，
[2, 1001, 1001, 1001]

所以下一个目标串`cd`的下标应该从`abcd`中的`2`调整成`eeebcd`中的`4`。

当`cd`替换成`ffff`，所有下标在`2`之后的字符，又向后偏移了`2`个位置，偏移数组变为，
[2, 1001, 2, 1001]
```

#### 代码
```java
class Solution {
    public String findReplaceString(String S, int[] indexes, String[] sources, String[] targets) {
        StringBuilder sb = new StringBuilder(S);
        int[] offsets = new int[S.length()];
        Arrays.fill(offsets, 1001);
        for (int i = 0; i < indexes.length; i++) {
            int offset = 0;
            for (int idx = 0; idx < offsets.length; idx++) {
                if (offsets[idx] < 1001 && indexes[i] > idx) offset += offsets[idx];
            }
            StringBuilder newSb = replaceEachString(sb, indexes[i] + offset, sources[i], targets[i]);
            if (newSb != null) {
                sb = newSb;
                int newOffset = targets[i].length() - sources[i].length();
                offsets[indexes[i]] = newOffset;
            }
        }
        return new String(sb);
    }

    // return index of the end of target in original string, return -1 if not valid
    private int check(StringBuilder orig, int begin, String target) {
        int p = begin;
        for (int i = 0; i < target.length(); i++, p++) {
            if (p >= orig.length() || orig.charAt(p) != target.charAt(i)) return -1;
        }
        return p;
    }

    private StringBuilder replaceEachString(StringBuilder sb, int begin, String source, String target) {
        int end = check(sb, begin, source);
        if (end >= 0) {
            StringBuilder res = new StringBuilder();
            res.append(sb.substring(0, begin));
            res.append(target);
            res.append(sb.substring(end));
            return res;
        }
        return null;
    }
}
```

#### 结果
![find-and-replace-in-string-1](/images/leetcode/find-and-replace-in-string-1.png)


### 从左到右替换字符串
我们需要记录所有字符的偏移量，因为替换的位置是跳跃的。但是当按从左到右的顺序替换字符串，所有偏移量都会积累下来，因此我们只需要记录最终的偏移累计值即可。代码和之前的方法只有微小的差别。

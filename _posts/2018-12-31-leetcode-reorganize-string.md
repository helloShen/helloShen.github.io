---
layout: post
title: "Leetcode - Algorithm - Reorganize String "
date: 2018-12-31 23:33:18
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "math", "priority queue"]
level: "medium"
description: >
---

### 题目
Given a string S, check if the letters can be rearranged so that two characters that are adjacent to each other are not the same.

If possible, output any possible result.  If not possible, return the empty string.

Example 1:
```
Input: S = "aab"
Output: "aba"
```

Example 2:
```
Input: S = "aaab"
Output: ""
```

Note:
* S will consist of lowercase letters and have length in range [1, 500].


### 系统性地解决问题
首先从数学的角度讲，如果字母`x`无法被间隔开，需要满足一个条件，就是其他字母数量不够，
> `numOther < numX - 1`

举个例子，假设有5个`a`。把他们都间隔开，至少需要4个其他字母，是什么字母不重要。
```
a a a a a
 ^ ^ ^ ^
 b b b b
```

所以我们就需要统计所有字母的频率，然后就可以判断是否可行。

如果是可行的，那么我们就从频率最大的字母开始填充。
```
先填5个a
| a |   | a |   | a |   | a |   | a |

再填4个b
| a | b | a | b | a | b | a | b | a |
```

#### 代码
```java
class Solution {
    public String reorganizeString(String S) {
        int size = S.length();
        if (size < 2) return S;
        int[][] counts = new int[26][2];
        for (int i = 0; i < 26; i++) {
            counts[i][1] = i + 'a';
        }
        char[] cs = S.toCharArray();
        for (char c : cs) {
            counts[c - 'a'][0]++;
        }
        Arrays.sort(counts, (int[] a, int[] b) -> b[0] - a[0]);
        int max = counts[0][0];
        if (max > (size + 1) / 2) return "";
        char[] arr = new char[size];
        int idx = 0;
        char c = (char) counts[idx][1];
        int count = counts[idx][0];
        boolean repeat = false;
        for (int i = 0; i < size; i += 2) {
            if (count == 0) {
                c = (char) counts[++idx][1];
                count = counts[idx][0];
            }
            arr[i] = c;
            count--;
            if (!repeat && i + 2 >= size) {
                i = -1;
                repeat = true;
            }
        }
        return new String(arr);
    }
}
```

#### 结果
![reorganize-string-1](/images/leetcode/reorganize-string-1.png)


### 交换法
假设有`aaaaabbbb`，看到开头两个`a`，就往后找第一个`b`与之交换，
```
  swap
 |   |
aaaaabbbb

变成：
abaaaabbb
```

可能出现最后有多余字母的情况，比如`cxawaaa`，但也不能马上就断定是多余的。应该尝试把剩下的字母尽量插入之前的字符串。
```
    后面再也找不到不是a的字母
     |
cxawaaa

多余2个a，可以插在这两个位置：

 c x a w a
^ ^
a a
```

如果还是找不到位置给多余的字母，我们才认定是无法完成的任务。

#### 代码
```java
class Solution {
    public String reorganizeString(String S) {
        int size = S.length();
        if (size < 2) return S;
        char[] arr = S.toCharArray();
        for (int pre = 0, curr = 1; curr < size; pre++, curr++) {
            if (arr[pre] == arr[curr]) {
                int idx = curr + 1;
                while (idx < size && arr[idx] == arr[curr]) idx++;
                if (idx == size) {
                    char[] filled = fill(Arrays.copyOfRange(arr, 0, pre), arr[pre], size - pre);
                    return (filled == null)? "" : new String(filled);
                }
                arr[curr] = arr[idx];
                arr[idx] = arr[pre];
            }
        }
        return new String(arr);
    }

    private char[] fill(char[] arr, char c, int count) {
        char[] res = new char[arr.length + count];
        int i = 0;
        int idx = 0;
        for (; i < arr.length && count > 0; i++) {
            if (arr[i] != c) {
                res[idx++] = c;
                res[idx++] = arr[i];
                count--;
            } else {
                res[idx++] = c;
                if (i + 1 < arr.length) {
                    res[idx++] = arr[++i];
                }
            }
        }
        if (idx < res.length && count > 0) {
            res[idx++] = c;
            count--;
        }
        if (count > 0) return null;
        while (i < arr.length) {
            res[idx++] = arr[i++];
        }
        return res;
    }
}
```

#### 结果
![reorganize-string-2](/images/leetcode/reorganize-string-2.png)

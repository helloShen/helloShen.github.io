---
layout: post
title: "Leetcode - Algorithm - Partition Labels "
date: 2019-03-29 12:04:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
A string S of lowercase letters is given. We want to partition this string into as many parts as possible so that each letter appears in at most one part, and return a list of integers representing the size of these parts.

Example 1:
```
Input: S = "ababcbacadefegdehijhklij"
Output: [9,7,8]
Explanation:
The partition is "ababcbaca", "defegde", "hijhklij".
This is a partition so that each letter appears in at most one part.
A partition like "ababcbacadefegde", "hijhklij" is incorrect, because it splits S into less parts.
```

Note:
* S will have length in range `[1, 500]`.
* S will consist of lowercase letters ('a' to 'z') only.

### 关键在于找出每个字母的出现范围
这题关键在于怎么看待 **“每个字母最多出现在一个分组中”** 这件事。

还是以`ababcbacadefegdehijhklij`为例，只看字母`a`的话，三个`a`的位置如下所示，
```
a a   a
| |   |  
ababcbacadefegdehijhklij
       |
|------|
       |
       最早从这里切
```
如果要切一刀，且要让所有的`a`都在同一组的话，我们最早只能从最后一个`a`的下一个元素`c`开始切。后面就可以随意切。所以可以总结出规律：每个字母出现的域不可以被切断。下图画出了每个字母各自的出现域。这个遍历一次字符串很好统计。
![partition-labels-figure-1](/images/leetcode/partition-labels-figure-1.png)

然后只要用一个指针，开始遍历数组。检查每个字母最后一次出现的位置。在次位置（记做`scope`）之前都不可切割。如图所示，直到最后一个`a`的位置，所有字母的域都不会超出这个`scope`，所以我们可以在最后一个`a`这里切一刀。以此类推。

#### 代码
```java
class Solution {
    public List<Integer> partitionLabels(String S) {
        List<Integer> res = new ArrayList<>();
        int[] table = parse(S);
        int p = 0, size = S.length();
        while (p < size) {
            int start = p, scope = p;
            while (p < size && p <= scope) {
                int newScope = table[S.charAt(p) - 'a'];
                if (newScope > scope) scope = newScope;
                p++;
            }
            res.add(p - start);
        }
        return res;
    }

    private int[] parse(String S) {
        int[] table = new int[26];
        for (int i = 0; i < S.length(); i++) {
            char c = S.charAt(i);
            table[c - 'a'] = i;
        }
        return table;
    }
}
```

#### 结果
![partition-labels-1](/images/leetcode/partition-labels-1.png)

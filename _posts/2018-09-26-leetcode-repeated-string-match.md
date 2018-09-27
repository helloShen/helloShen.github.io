---
layout: post
title: "Leetcode - Algorithm - Repeated String Match "
date: 2018-09-26 21:47:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
Given two strings A and B, find the minimum number of times A has to be repeated such that B is a substring of it. If no such solution, return -1.

For example, with A = "abcd" and B = "cdabcdab".

Return 3, because by repeating A three times (“abcdabcdabcd”), B is a substring of it; and B is not a substring of A repeated two times ("abcdabcd").

Note:
* The length of A and B will be between 1 and 10000.

### 循环指针比对
这是一种直观的解法。

#### 代码
```java
class Solution {
    public int repeatedStringMatch(String A, String B) {
        int la = A.length(), lb = B.length(); // length A & length B
        if (lb == 0) return 1;
        if (la == 0) return 0;
        int p = 0;
        char hb = B.charAt(0); // head B
        int from = 0;
        while (p < la) {
            p = A.indexOf(hb, from);
            if (p == -1) break;
            int pb = 0; // pointer at head of B
            int repeat = 1;
            for (int pa = p; pb < lb; pa = (pa + 1) % la, pb++) {
                if (A.charAt(pa) != B.charAt(pb)) break;
                if ((pa + 1 == la) && (pb + 1 != lb)) {
                    repeat++;
                }
            }
            if (pb == lb) return repeat;
            from = p + 1;
        }
        return -1;
    }
}
```

#### 结果
![repeated-string-match-1](/images/leetcode/repeated-string-match-1.png)


### 数学观察
首先，`A`至少不能比`B`短。所以第一步，先把`A`扩展到至少和`B`一样长。

如果还不能匹配，比如下面这种情况，
```
A = "ab"
B = "babababa"

扩展后 AA = "abababab"
```
这时候只需要在`AA`后面再复制一次`A`，能匹配就匹配上了，不能匹配就是永远匹配不上的。例子里是匹配成功了。
```
ababababab
 ||||||||
 babababa   <- 匹配成功
```
再来个例子，
```
A = "aaac"
B = "acaaaca"
```
`A`扩充一倍比`B`要长，但还差一点点，
```
AA = "aaacaaac"
B = "acaaaca"
```
再扩充一次，就匹配上了，
```
AAA = "aaacaaacaaac"
         |||||||
B =     "acaaaca"   <- 匹配成功
```

以上只是通过例子的观察得到的，并不是严格的数学证明。

### 代码
```java
public int repeatedStringMatch(String A, String B) {
    if (B.length() == 0) return 1;
    if (A.length() == 0) return -1;
    StringBuilder sb = new StringBuilder(A);
    int k = 1;
    while (sb.length() < B.length()) {
        sb.append(A);
        k++;
    }
    if (sb.toString().indexOf(B) != -1) return k;
    if (sb.append(A).toString().indexOf(B) != -1) return k + 1;
    return -1;
}
```

#### 结果
![repeated-string-match-2](/images/leetcode/repeated-string-match-2.png)

#### 用`lastIndexOf()`非常高效。原因尚不明确。
```java
public int repeatedStringMatch(String A, String B) {
    if (B.length() == 0) return 1;
    if (A.length() == 0) return -1;
    StringBuilder sb = new StringBuilder(A);
    int k = 1;
    while (sb.length() < B.length()) {
        sb.append(A);
        k++;
    }
    if (sb.toString().lastIndexOf(B) != -1) return k;
    if (sb.append(A).toString().lastIndexOf(B) != -1) return k + 1;
    return -1;
}
```

#### 结果
![repeated-string-match-3](/images/leetcode/repeated-string-match-3.png)

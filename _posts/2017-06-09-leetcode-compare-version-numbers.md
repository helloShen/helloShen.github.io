---
layout: post
title: "Leetcode - Algorithm - Compare Version Numbers "
date: 2017-06-09 18:23:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "medium"
description: >
---

### 题目
Compare two version numbers version1 and version2.
**If version1 > version2 return 1, if version1 < version2 return -1, otherwise return 0.**

You may assume that the version strings are non-empty and contain only digits and the . character.
The . character does not represent a decimal point and is used to separate number sequences.
For instance, 2.5 is not "two and a half" or "half way to version three", it is the fifth second-level revision of the second first-level revision.

Here is an example of version numbers ordering:
```
0.1 < 1.1 < 1.2 < 13.37
```
Credits:
Special thanks to @ts for adding this problem and creating all test cases.

### 用`split()`函数
这里的切割只是简单的找`.`，当正则表达式不复杂的情况下，可以放心用`split()`函数，效率不低。

需要特别额外注意的是长度不同时的一些边角情况，
1. `1.1.1` > `1.1`
2. `1.1` == `1.1.0`


#### 代码
```java
public class Solution {
    public int compareVersion(String version1, String version2) {
        String[] strs1 = version1.split("\\.");
        String[] strs2 = version2.split("\\.");
        int cur = 0;
        while (cur < strs1.length && cur < strs2.length) {
            int val1 = Integer.parseInt(strs1[cur]);
            int val2 = Integer.parseInt(strs2[cur]);
            if (val1 < val2) {
                return -1;
            } else if (val1 > val2) {
                return 1;
            } else { // val1 == val2
                cur++;
            }
        }
        int cur1 = cur;
        if (cur1 < strs1.length) {
            while (cur1 < strs1.length) {
                int val1 = Integer.parseInt(strs1[cur1]);
                if (val1 > 0) { return 1; } // 1.1.1 VS 1.1
                cur1++;
            }
            return 0; // 1.1.0.0 VS 1.1
        }
        int cur2 = cur;
        if (cur2 < strs2.length) {
            while (cur2 < strs2.length) {
                int val2 = Integer.parseInt(strs2[cur2]);
                if (val2 > 0) { return -1; } // 1.1 VS 1.1.1
                cur2++;
            }
            return 0; // 1.1 VS 1.1.0.0
        }
        return 0; // 1.1 VS 1.1
    }
}
```

#### 结果
![compare-version-numbers-1](/images/leetcode/compare-version-numbers-1.png)


### 还是之前的算法，只是简化一下代码
较短的版本号，不够用`0`补足。

#### 代码
```java
public class Solution {
    public int compareVersion(String version1, String version2) {
        String[] strs1 = version1.split("\\.");
        String[] strs2 = version2.split("\\.");
        int len = Math.max(strs1.length,strs2.length);
        for (int i = 0; i < len; i++) {
            int val1 = (i < strs1.length)? Integer.parseInt(strs1[i]) : 0;
            int val2 = (i < strs2.length)? Integer.parseInt(strs2[i]) : 0;
            if (val1 < val2) {
                return -1;
            } else if (val1 > val2) {
                return 1;
            }
        }
        return 0;
    }
}
```

#### 结果
![compare-version-numbers-2](/images/leetcode/compare-version-numbers-2.png)

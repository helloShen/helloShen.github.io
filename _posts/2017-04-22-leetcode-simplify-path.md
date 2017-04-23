---
layout: post
title: "Leetcode - Algorithm - Simplify Path "
date: 2017-04-22 00:31:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","string"]
level: "medium"
description: >
---

### 主要收获 1 - 解析元素
与其乱七八糟混在一起讨论，不如一上来就把元素解析清楚。就像分析`xml`或者`html`一样，块以及`DOM`都解析清楚，就清楚了。

### 主要收获 2 - 复杂问题拆分成简单的小问题来解决
比如如果`绝对路径`和`相对路径`混在一起讨论很复杂的话，可以把两者分开讨论，各个击破。


### 题目
Given an absolute path for a file (Unix-style), simplify it.

For example,
```
path = "/home/", => "/home"
path = "/a/./b/../../c/", => "/c"
```

Corner Cases:
Did you consider the case where path = `/../`?
In this case, you should return `/`.
Another corner case is the path might contain multiple slashes `/` together, such as `/home//foo/`.
In this case, you should ignore redundant slashes and return `/home/foo`.

### 暴力定规则
注意！这里讨论的只是绝对路径`Absolute Path`。前面必须有个斜杠`/`表示根目录。如果考虑相对路径的话，情况要复杂地多。

具体分为以下几个步骤：
1. 根据斜杠`/`切割成一个个独立的目录。比如`/a/./b/../../c/`切成`[a],[.],[b],[..],[..],[c]`。
2. 开一个`Stack`（用`Deque`）。一个个往里放。
    * 遇到`.`跳过。
    * 遇到`..`不但跳过，前面如果有元素了，还要吐一个出来。
3. 最后给每个元素前面补偿一个`/`。
4. 如果最后结果为空，直接返回`/`。

这里根据斜杠切割很重要，这样就不是乱七八糟一起分析，而是把各个元素提取出来了，问题就简单很多。

第二，如果没有规定是绝对路径还是相对路径，也是把两种情况分开讨论比较好，要学会简化问题。

#### 代码
```java
public class Solution {
    public String simplifyPath(String path) {
        Deque<String> stack = new LinkedList<String>();
        String[] segments = path.split("/");
        Set<String> skip = new HashSet<>(Arrays.asList(new String[]{"",".",".."}));
        Set<String> pop = new HashSet<>(Arrays.asList(new String[]{".."}));
        for (String str : segments) {
            if (pop.contains(str) && !stack.isEmpty()) { stack.removeFirst(); continue; }
            if (!skip.contains(str)) { stack.offerFirst(str); }
        }
        String res = "";
        while (!stack.isEmpty()) { res += ("/" + stack.pollLast()); }
        return (res == "")? "/":res;
    }
}
```

#### 结果
用了`Deque`和`Set`可能比直接用数组慢一点，但代码可读性也很重要。
![simplify-path-1](/images/leetcode/simplify-path-1.png)

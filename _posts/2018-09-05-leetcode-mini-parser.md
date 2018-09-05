---
layout: post
title: "Leetcode - Algorithm - Mini Parser "
date: 2018-09-05 14:27:14
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "stack"]
level: "medium"
description: >
---

### 题目
Given a nested list of integers represented as a string, implement a parser to deserialize it.

Each element is either an integer, or a list -- whose elements may also be integers or other lists.

Note: You may assume that the string is well-formed:
* String is non-empty.
* String does not contain white spaces.
* String contains only digits `0-9`, `[`, `-` , `,`, `]`.

Example 1:
```
Given s = "324",

You should return a NestedInteger object which contains a single integer 324.
```

Example 2:
```
Given s = "[123,[456,[789]]]",

Return a NestedInteger object containing a nested list with 2 elements:
1. An integer containing value 123.
2. A nested list containing two elements:
    i.  An integer containing value 456.
    ii. A nested list with one element:
         a. An integer containing value 789.
```

### 遍历字符串，用`Stack`缓存每一层元素
规则如下，
1. 遇到`[`: 开一个新的List型的NestedInteger，并把上一层的NestedInteger压入Stack。
2. 遇到`]`: 如果当前在在读数字，将数字添加到List里。最后从Stack弹出上一层NestedList。
3. 遇到`,`: 如果当前在读数字，将数字添加到List里。没有（前面是`]`，就什么也不做）。
4. 遇到`-`: 开始读数字，正负号设为`-1`。
5. 遇到`[0~9]`: 开始读数字。


#### 代码
```java
public NestedInteger deserialize(String s) {
    int sign = 1;
    Integer num = null;
    Deque<NestedInteger> stack = new LinkedList<>();
    NestedInteger dummy = new NestedInteger();
    NestedInteger curr = dummy;
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c == '[') {
            NestedInteger newNI = new NestedInteger();
            curr.add(newNI);
            stack.push(curr);
            curr = newNI;
        } else if (c == ']' || c == ',') {
            if (num != null) {
                curr.add(new NestedInteger(num * sign));
                num = null;
                sign = 1;
            }
            if (c == ']') {
                curr = stack.pop();
            }
        } else if (c =='-') {
            num = 0;
            sign = -1;
        } else { // is digit
            if (num == null) {
                num = 0;
            }
            num = num * 10 + (c - '0');
        }
    }
    if (num != null) { // dump
        curr.add(new NestedInteger(num * sign));
    }
    return dummy.getList().get(0);
}
```

#### 结果
![mini-parser-1](/images/leetcode/mini-parser-1.png)

---
layout: post
title: "Leetcode - Algorithm - Evaluate Reverse Polish Notation "
date: 2017-06-06 01:35:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack"]
level: "medium"
description: >
---

### 题目
Evaluate the value of an arithmetic expression in Reverse Polish Notation.

Valid operators are `+, -, *, /.` Each operand may be an integer or another expression.

Some examples:
```
  ["2", "1", "+", "3", "*"] -> ((2 + 1) * 3) -> 9
  ["4", "13", "5", "/", "+"] -> (4 + (13 / 5)) -> 6
```

### 用`Stack`缓存数字
原理很简单，用一个`Stack`缓存多有读到过的数字，遇到`+-*/`计算符号，就从`Stack`里读出最后压入的两个数进行计算，然后再将结果压入`Stack`.

这题需要注意`String`间的比较不要用`==`，而是`equals()`比较好。 因为不同版本的Java可能对字符串常量池的处理不同，有的不保证两个值相等的字符串就是同一个对象。用`equals()`可以避免这个麻烦。

另一个需要注意的是`switch`语法，对`String`用`switch`是Java 7才引入的特性，如果leetcode用的Java版本过低，会导致无法通过。

#### 代码
这是 **带防御** 的版本。
```java
public class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new LinkedList<>();
        for (String token : tokens) {
            try {
                stack.offerFirst(Integer.parseInt(token));
            } catch (NumberFormatException e) {
                if (isOperator(token) && stack.size() >= 2) {
                    int b = stack.pollFirst();
                    int a = stack.pollFirst();
                    stack.offerFirst(calculate(a,b,token));
                } else { // 格式不对
                    return 0;
                }
            }
        }
        int size = stack.size();
        if (size == 0 || size > 1) { return 0; } // stack必须只能有结果一个值
        return stack.pollFirst();
    }
    private int calculate(int a, int b, String token) {
        if (token.equals("+")) {
            return a + b;
        } else if (token.equals("-")) {
            return a - b;
        } else if (token.equals("*")) {
            return a * b;
        } else if (token.equals("/")) {
            return a / b;
        } else {
            return 0;
        }
    }
    private boolean isOperator(String s) {
        return (s.equals("+")) || (s.equals("-")) || (s.equals("*")) || (s.equals("/"));
    }
}
```

#### 结果
![evaluate-reverse-polish-notation-1](/images/leetcode/evaluate-reverse-polish-notation-1.png)


### 防御性编程，尤其是`try-catch`影响了效率
假设`String[]`中的元素格式，数量，顺序都合法，去掉所有的格式检查。

#### 代码
下面是 **不带防御** 的版本。
```java
public class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new LinkedList<>();
        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                int b = stack.pollFirst();
                int a = stack.pollFirst();
                stack.offerFirst(calculate(a,b,token));
            } else {
                stack.offerFirst(Integer.parseInt(token));
            }
        }
        return stack.pollFirst();
    }
    public int calculate(int a, int b, String token) {
        if (token.equals("+")) {
            return a + b;
        } else if (token.equals("-")) {
            return a - b;
        } else if (token.equals("*")) {
            return a * b;
        } else if (token.equals("/")) {
            return a / b;
        } else {
            return 0;
        }
    }
}
```

#### 结果
银弹！
![evaluate-reverse-polish-notation-2](/images/leetcode/evaluate-reverse-polish-notation-2.png)

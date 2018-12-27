---
layout: post
title: "Leetcode - Algorithm - Valid Parenthesis String "
date: 2018-12-26 21:57:22
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "stack", "backtracking"]
level: "medium"
description: >
---

### 题目
Given a string containing only three types of characters: `(`, `)` and `*`, write a function to check whether this string is valid. We define the validity of a string by these rules:

* Any left parenthesis '(' must have a corresponding right parenthesis `)`.
* Any right parenthesis ')' must have a corresponding left parenthesis `(`.
* Left parenthesis '(' must go before the corresponding right parenthesis `)`.
* `*` could be treated as a single right parenthesis `)` or a single left parenthesis `(` or an empty string.
* An empty string is also valid.

Example 1:
```
Input: "()"
Output: True
Example 2:
Input: "(*)"
Output: True
Example 3:
Input: "(*))"
Output: True
```

Note:
* The string size will be in the range [1, 100].

### 工程解析法
通过两步，对问题逐步解析。

#### 第一步，贪婪消除成对的`(`和`)`
第一步，我们尝试将问题尽可能的简化。优先消除所有成对的`(`和`)`。因为`(`和`)`是确定的，`*`是通用的。而且，可以使用贪婪的策略，只要前面有`(`,后面看到一个`)`就马上配对消除。

有的人可能担心贪心策略不适用于这个问题，担心提前将应该和后面匹配的括号提前匹配掉了。实际上不需要有这个顾虑。看一个简单的例子，只有一个左括号`(`，
```
|
(***)))         ->      ***))
```

无论这个左括号和任意右括号匹配，抵消之后的结果始终是剩下3个`*`以及2个`)`。

看下面比较复杂的例子，
```
*()(())*()(()()((()(()()*)(*(())((((((((()*)(()(*)

贪婪消除成对()之后得到：

**(((*(*(((((((*(*
```

第一步之后，字符串中剩下的左括号`(`后面不可能还有右括号`)`，剩下的右括号`)`之前不可能还有左括号`(`。换句话说。比如，
```
所有的右括号`)`都在左括号`(`之前，

)))(((

其中可以夹杂任意的星号*
**))*)*(**((
```

#### 第二步，处理剩下的`)`和`(`
剩下的`(`和`)`都没有确定的成对的括号与之匹配。只能和通配符`*`匹配。`)`需要在它之前的`*`，`(`需要在它之后的`*`。
```
之前有2个星号，可以抵消
  ||
  || 之前有一个星号，可以抵消
  || |
**))*)*(**((
       |  ||
       |  他俩后面没有星号，无法抵消，所以是不合法的
       |
       之后有星号，可以抵消
```

#### 代码
```java
class Solution {
    public boolean checkValidString(String s) {
        List<Character> list = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) list.add(s.charAt(i));
        eliminateParenthesis(list);
        return finishingCheck(list);
    }

    private void eliminateParenthesis(List<Character> list) {
        Deque<Integer> stack = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            char c = list.get(i);
            if (c == '(') {
                stack.push(i);
            } else if (c == ')' && !stack.isEmpty()) {
                list.remove(i);
                int pair = stack.pop();
                list.remove(pair);
                i -= 2;
            }
        }
    }

    private boolean finishingCheck(List<Character> list) {
        // check leading ')'
        int stack = 0;
        int firstLeftParenthesis = -1;
        for (int i = 0; i < list.size(); i++) {
            char c = list.get(i);
            if (c == '*') {
                stack++;
            } else if (c == ')') {
                if (stack == 0) {
                    return false;
                } else {
                    stack--;
                }
            } else { // c == '('
                firstLeftParenthesis = i;
                break;
            }
        }
        // check following '('
        if (firstLeftParenthesis != -1) {
            stack = 0;
            for (int i = firstLeftParenthesis; i < list.size(); i++) {
                char c = list.get(i);
                if (c == '(') {
                    stack++;
                } else if (c == '*' && stack > 0) {
                    stack--;
                }
            }
            if (stack > 0) return false;
        }
        return true;
    }
}
```

#### 结果
![valid-parenthesis-string-1](/images/leetcode/valid-parenthesis-string-1.png)


### 大自然的暴力递归法
从新思考这个问题，如果没有`*`通配符，会是很简单的问题。不管例子多复杂，只要保证两条，
1. 遍历字符串的过程中，右括号`)`的数量总是不能大于`(`左括号的。因为这就代表多出来右括号`)`没有足够的左括号`(`与之匹配。
2. 最终左右括号`(`和`)`的数量必须配平。

引进`*`只后，带来的唯一不同就是`*`既可以当左括号`(`也可以当右括号`)`用。那么我们就在递归的时候，加上这些分支。

#### 代码
```java
class Solution {
    public boolean checkValidString(String s) {
        return check(s, 0, 0);
    }

    private boolean check(String s, int start, int count) {
        if (start == s.length()) return count == 0;
        if (count < 0) return false;
        char c = s.charAt(start);
        if (c == '(') {
            return check(s, start + 1, count + 1);
        } else if (c == ')') {
            return check(s, start + 1, count - 1);
        } else { // c == '*'
            return check(s, start + 1, count + 1) || check(s, start + 1, count - 1) || check(s, start + 1, count);
        }
    }
}
```

#### 结果
代码看上去很简洁，但复杂度是很高的。
![valid-parenthesis-string-2](/images/leetcode/valid-parenthesis-string-2.png)

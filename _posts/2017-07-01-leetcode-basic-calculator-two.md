---
layout: post
title: "Leetcode - Algorithm - Basic Calculator Two "
date: 2017-07-01 14:33:34
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","string"]
level: "medium"
description: >
---

### 题目
Implement a basic calculator to evaluate a simple expression string.

The expression string contains only non-negative integers, `+`, `-`, `*`, `/` operators and empty spaces . The integer division should truncate toward zero.

You may assume that the given expression is always valid.

Some examples:
```
"3+2*2" = 7
" 3/2 " = 1
" 3+5 / 2 " = 5
```

Note: Do not use the `eval` built-in library function.

### 主要收获 - 具象的重要性
> 一个复杂的过程，如果中间一些步骤能和一些比较具体的事物联系起来，往往能起到化繁为简的作用。

比如这题中，最经典的具象：**栈**。本来数字符号的操作顺序用正常队列的思维思考就比较混乱。如果把这个容器想象成`LIFO`栈，问题就简单很多。

再比如，本来符号的处理有点乱，现在把符号问题具象成一个 **状态机**。每读到一个`符号`，就改变一次状态机的状态。每读到一个`数字`，就是根据状态机当前的状态，做相应的动作。然后用`switch`语法模拟一个状态机，代码就很明晰。


### 总体思路
遇到这种语法解析问题，`Stack`是基本思想。

### 利用`Stack`
初步的思路只有一条，乘法和除法`*`,`/`因为有较高优先级，需要即时处理，压入栈的直接就是计算好的结果。加法和减法`+`,`-`要等乘除法都做完（栈中保留的就是中间结果），最后把栈中的数字统一相加或相减。当然减法可以转换成加一个负数。

最后，思路抽象成下面三条规则：
1. 遇到`+,-,*,/`符号，就记录下来，作为`当前符号`。
2. 遇到`[0-9]`数字，就把连续的数字读出来，然后根据`当前符号`，做相应的动作。
    * 当前符号为`+`: 直接把数字压入栈
    * 当前符号为`-`: 栈中压入数字的相反数。
    * 当前符号为`*`: 弹出栈顶数字，和当前数字相乘，把结果压到栈顶。
    * 当前符号为`/`: 弹出栈顶数字，除以当前数字，把结果压到栈顶。
3. 遇到空格` `，什么都不做，跳过。

最后，把留在栈中的所有数字相加，得出结果。

#### 代码
```java
public class Solution {
    public int calculate(String s) {
        char[] chars = s.toCharArray();
        Deque<Long> stack = new LinkedList<>();
        int cur = 0;
        char oprt = '+';
        while (cur < chars.length) {
            switch (chars[cur]) {
                case ' ': cur++; break;
                case '+': cur++; break;
                case '-': oprt = '-'; cur++; break;
                case '*': oprt = '*'; cur++; break;
                case '/': oprt = '/'; cur++; break;
                default: // [0-9]
                    int[] pair = parseNum(chars,cur);
                    cur = pair[1];
                    switch (oprt) {
                        case '-': stack.push(0 - (long)pair[0]); break;
                        case '*': stack.push(stack.pop() * pair[0]); break;
                        case '/': stack.push(stack.pop() / pair[0]); break;
                        default: stack.push((long)pair[0]); break; // +
                    }
                    oprt = '+';
            }
        }
        long sum = 0;
        for (long n : stack) { sum += n; }
        return (int)sum;
    }
    private int[] parseNum(char[] chars, int cur) {
        int[] result = new int[2];
        int num = 0;
        while (cur < chars.length && chars[cur] >= '0' && chars[cur] <= '9') {
            num = num * 10 + (chars[cur++] - '0');
        }
        result[0] = num; result[1] = cur;
        return result;
    }
}
```

#### 结果
![basic-calculator-two-1](/images/leetcode/basic-calculator-two-1.png)

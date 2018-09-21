---
layout: post
title: "Leetcode - Algorithm - Remove K Digits "
date: 2018-09-20 19:39:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","array"]
level: "medium"
description: >
---

### 题目
Given a non-negative integer num represented as a string, remove k digits from the number so that the new number is the smallest possible.

Note:
* The length of num is less than 10002 and will be ≥ k.
* The given num does not contain any leading zero.

Example 1:
```
Input: num = "1432219", k = 3
Output: "1219"
Explanation: Remove the three digits 4, 3, and 2 to form the new number 1219 which is the smallest.
```

Example 2:
```
Input: num = "10200", k = 1
Output: "200"
Explanation: Remove the leading 1 and the number is 200. Note that the output must not contain leading zeroes.
```

Example 3:
```
Input: num = "10", k = 2
Output: "0"
Explanation: Remove all the digits from the number and it is left with nothing which is 0.
```


### 直观解：找第一个递减的数字
以`1432219`为例，做以下观察，
```
1432219

去掉"1": 432219
去掉"4": 132219 <-- 最小
去掉"3": 142219
去掉"2": 143219
去掉"1": 143229
去掉"9": 143221
```
高位的比较大的数字应该去掉，但好像也不是直接去掉最高位。再做以下观察，
```
123456789

去掉"1": 23456789
去掉"2": 13456789
去掉"3": 12456789
...
去掉"8": 12345679
去掉"9": 12345678
```
高位较小的数字留着反而好。

最后的规律是：
> 保留高位的递增序列，找到第一个递减位，删掉前面一位数字。

比如`1432219`，
```
1432219     (k = 3)
1 -> 4      递增
14 -> 3     递减，去掉4

132219      (k = 2)
1 -> 3      递增
13 -> 2     递减，去掉3

12219       (k = 1)
1 -> 2      递增
12 -> 2     递增（不变）
122 -> 1    递减，去掉后一个2

1219        (k = 0)
```

#### 代码
用一个`List<Character>`表示字符串。
```java
class Solution {
    public String removeKdigits(String num, int k) {
        List<Character> chars = new LinkedList<>();
        for (char c : num.toCharArray()) chars.add(c);
        while (k-- > 0) {
            boolean find = false;
            for (int i = 0; i < chars.size(); i++) {
                if ((i + 1) == chars.size() || chars.get(i) > chars.get(i + 1)) {
                    chars.remove(i);
                    find = true;
                    break;
                }
            }
            if (!find) chars.remove(chars.size() - 1);
        }
        while (!chars.isEmpty()) {
            if (chars.get(0) != '0') break;
            chars.remove(0);
        }
        if (chars.size() == 0) return "0";
        StringBuilder sb = new StringBuilder();
        for (Character c : chars) {
            sb.append(c);
        }
        return sb.toString();
    }
}
```

#### 结果
![remove-k-digits-1](/images/leetcode/remove-k-digits-1.png)


### 直接在`char[]`上操作
`List`开销比较大，用数组会好一些。但数组的问题删除元素开销大。所以暂时用`*`标记要删除的位，最后统一把`*`去掉。

#### 代码
```java
class Solution {

    private final char MARK = '*';

    public String removeKdigits(String num, int k) {
        char[] chars = num.toCharArray();
        int size = chars.length;
        while (k-- > 0) {
            int p = 0;
            while (p < size) {
                if (chars[p] == MARK) {
                    p++;
                    continue;
                }
                int cpP = p;
                while (++p < size && chars[p] == MARK);
                if (p < size && chars[p] < chars[cpP]) {
                        chars[cpP] = MARK;
                        break;
                }
            }
            if (p >= size) {
                int tail = size - 1;
                while (tail >= 0 && chars[tail] == MARK) tail--;
                chars[tail] = MARK;
            }
        }
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (start < size && (chars[start] == MARK || chars[start] == '0')) start++;
        for (int i = start; i < size; i++) {
            if (chars[i] != MARK) sb.append(chars[i]);
        }
        if (sb.length() == 0) return "0";
        return sb.toString();
    }

}
```

#### 结果
![remove-k-digits-2](/images/leetcode/remove-k-digits-2.png)


### 用`Stack`
但这种涉及到单调性的问题，用`Stack`是最好的。把前面的递增序列都存在`Stack`里，遇到较小的数字就逐个弹出来比较，还是`1432219`的例子，
```
stack
+-----
|1 4     <-- 3
+-----

3打败了4，但被1打败
+-----
|1 3     <-- 2
+-----

2打败了3，但被1打败
+-----
|1 2     <-- 2
+-----

这个2谁也没打败，直接保留
+-------
|1 2 2   <-- 1
+-------

...
...
依次类推
```

#### 代码
```java
class Solution {

    public String removeKdigits(String num, int k) {
        int size = num.length();
        char[] stack = new char[size];
        int top = 0;
        char[] numChar = num.toCharArray();
        int cur = 0;
        for ( ; cur < size; cur++) {
            while (k > 0 && top > 0 && (stack[top - 1] > numChar[cur])) {
                top--; k--;
            }
            if (top == 0 && numChar[cur] == '0') continue;
            stack[top++] = numChar[cur];
        }
        while (k > 0 && top > 0) {
            top--; k--;
        }
        String partOne = new String(stack, 0, top);
        String partTwo = new String(numChar, cur, size - cur);
        return (partOne.length() + partTwo.length() == 0)? "0" : partOne + partTwo;
    }

}
```

#### 结果
![remove-k-digits-3](/images/leetcode/remove-k-digits-3.png)

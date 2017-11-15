---
layout: post
title: "Leetcode - Algorithm - Decode String "
date: 2017-11-14 21:02:12
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack"]
level: "medium"
description: >
---

### 题目
Given an encoded string, return it's decoded string.

The encoding rule is: `k[encoded_string]`, where the encoded_string inside the square brackets is being repeated exactly k times. Note that k is guaranteed to be a positive integer.

You may assume that the input string is always valid; No extra white spaces, square brackets are well-formed, etc.

Furthermore, you may assume that the original data does not contain any digits and that digits are only for those repeat numbers, k. For example, there won't be input like `3a` or `2[4]`.

Examples:
```
s = "3[a]2[bc]", return "aaabcbc".
s = "3[a2[c]]", return "accaccacc".
s = "2[abc]3[cd]ef", return "abcabccdcdcdef".
```

### 用`Stack`
像这种解析成对的`[]`，肯定是要用`Stack`。可以像下面这样创建一个新的数据结构`Block`来储存`[repeat-times,string]`对，也可以用两个`Stack`。

#### 代码
```java
class Solution {
    public String decodeString(String s) {
        Stack<Block> stack = new Stack<Block>();
        int cur = 0;
        stack.push(new Block(1));
        while (cur < s.length()) {
            if (Character.isDigit(s.charAt(cur))) {
                int end = cur + 1;
                while (Character.isDigit(s.charAt(end))) { end++; }
                int num = Integer.valueOf(s.substring(cur,end));
                stack.push(new Block(num));
                cur = end;
            } else if (s.charAt(cur) == ']') {
                Block curr = stack.pop();
                StringBuilder atom = curr.sb;
                StringBuilder sub = new StringBuilder();
                for (int i = 0; i < curr.repeat; i++) {
                    sub.append(atom);
                }
                stack.peek().sb.append(sub);
            } else {
                stack.peek().sb.append(s.charAt(cur));
            }
            cur++;
        }
        return stack.pop().sb.toString();
    }
    private class Block {
        private int repeat;
        private StringBuilder sb = new StringBuilder();
        private Block(int num) { repeat = num; }
    }
}
```

```java
class Solution {
    public String decodeString(String s) {
        Stack<Integer> times = new Stack<>();
        Stack<String> atoms = new Stack<>();
        int cur = 0;
        times.push(1);
        atoms.push("");
        while (cur < s.length()) {
            if (Character.isDigit(s.charAt(cur))) {
                int end = cur + 1;
                while (Character.isDigit(s.charAt(end))) { end++; }
                int num = Integer.valueOf(s.substring(cur,end));
                times.push(num);
                atoms.push("");
                cur = end;
            } else if (s.charAt(cur) == ']') {
                int repeat = times.pop();
                String currAtom = atoms.pop();
                String sub = "";
                for (int i = 0; i < repeat; i++) {
                    sub += currAtom;
                }
                atoms.push(atoms.pop() + sub);
            } else {
                atoms.push(atoms.pop() + s.charAt(cur));
            }
            cur++;
        }
        return atoms.pop();
    }
}
```

#### 结果
![decode-string-1](/images/leetcode/decode-string-1.png)

---
layout: post
title: "Leetcode - Algorithm - Different Ways To Add Parentheses "
date: 2017-07-09 15:25:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["divide and conquer"]
level: "medium"
description: >
---

### 主要思路
> 遇到这种如果用普通迭代比较复杂的题，可以考虑分治法。

### 题目
Given a string of numbers and operators, return all possible results from computing all the different possible ways to group numbers and operators. The valid operators are `+`, `-` and `*`.

**Example 1**
Input: `2-1-1`.
```
((2-1)-1) = 0
(2-(1-1)) = 2
```
Output: `[0, 2]`


**Example 2**
Input: `2*3-4*5`
```
(2*(3-(4*5))) = -34
((2*3)-(4*5)) = -14
((2*(3-4))*5) = -10
(2*((3-4)*5)) = -10
(((2*3)-4)*5) = 10
```
Output: `[-34, -14, -10, -10, 10]`

### 分治法 $$O(n\log_{}{n})$$
比如`2*3-4*5`可以分解为下面若干子问题，
* 子问题`2` * 子问题`3-4*5`
* 子问题`2*3` * 子问题`4*5`
* 子问题`2*3-4` * 子问题`5`

#### 代码
```java
public class Solution {

    private Map<String,List<Integer>> memo = new HashMap<>();

    public List<Integer> diffWaysToCompute(String input) {
        int len = input.length();
        // check history
        List<Integer> result = memo.get(input);
        if (result != null) { return result; }
        result = new ArrayList<>();
        // base case
        if (isDigit(input)) {
            result.add(Integer.parseInt(input));
            memo.put(input,result);
            return result;
        }
        // recursion (divid & conquer)
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (c == '+' || c == '-' || c == '*') {
                List<Integer> left = diffWaysToCompute(input.substring(0,i));
                List<Integer> right = diffWaysToCompute(input.substring(i+1,len));
                for (Integer il : left) {
                    for (Integer ir : right) {
                        switch (c) {
                            case '+': result.add(il + ir); break;
                            case '-': result.add(il - ir); break;
                            case '*': result.add(il * ir); break;
                        }
                    }
                }
            }
        }
        memo.put(input,result);
        return result;
    }
    private boolean isDigit(String s) {
        for (Character c : s.toCharArray()) {
            if (!Character.isDigit(c)) { return false; }
        }
        return true;
    }
}
```

#### 结果
![different-ways-to-add-parentheses-1](/images/leetcode/different-ways-to-add-parentheses-1.png)


### 解法2

#### 代码
```java

```

#### 结果
![different-ways-to-add-parentheses-2](/images/leetcode/different-ways-to-add-parentheses-2.png)

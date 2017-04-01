---
layout: post
title: "Leetcode - Algorithm - Generate Parentheses "
date: 2017-03-31 15:46:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","backtracking"]
level: "medium"
description: >
---

### 主要收获
> 强化动态规划的思维方式。把每个方案抽象成一系列小决策的组合。经典的锯钢条问题把据钢条的方案抽象成在每个可以切割的点选择“切”或者“不切”。这题可以抽象成在每个点可以选择写一个右括号关闭之前还没有关闭的左括号，也可以选择新开一个左括号。

### 题目
Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses.

For example, given n = 3, a solution set is:
```
[
  "((()))",
  "(()())",
  "(())()",
  "()(())",
  "()()()"
]
```

### 动态规划递归 $$O(2^n)$$
这是一个标准的动态规划问题。问题可以抽象成：
> 给定已有的一个括号串，以及还有几对括号要写，这两个条件，决定下一个括号怎么写。有两个策略：
>   1. 如果当前还有未关闭的左括号，可以用一个右括号把之前的左括号关闭。
>   2. 如果当前括号的数量还不够，可以再加上一个左括号。

看`n=3`的例子：
递归第一层：`left = 3`, `right = 0`. 说明还有3对括号要写，当前没有未关闭的左括号。这时，不能补右括号，唯一的选择就是新开一个左括号。
```
选择1：空缺。
选择2：写上"("。 left - 1, right + 1。
```
递归第二层：`left = 2`, `right = 1`. 还有2个左括号没写。当前还有1个左括号没关上。所以两个选择都可行，
```
选择1：补上右括号"()"。 right - 1。
选择2：写上新的左括号"(("。 left - 1, right + 1。
```
递归第三层：对`()`来说：`left = 2`, `right = 0`. 只有一个选择，就是写新的左括号，
```
选择1：空缺。
选择2：写新左括号"()("。 left - 1, right + 1。
```
还是递归第三层：对`((`来说：`left = 1`, `right = 2`. 有两个选择，
```
选择1：补上右括号"(()"。 right - 1。
选择2：写上新的左括号"((("。 left - 1, right + 1。
```
以此类推。递归的`base case`是当`left = 0 && right = 0`。也就是3对括号全部补全之后，再插入`List`。

#### 关于回溯算法
这题的`tag`标注了`Backtracking`回溯算法。只不过上面抽象出的两种情况都是正确的情况，所以不涉及回退和剪枝。如果不判定任何条件，每次只是随意写`(`或`)`，就会有错误的写法出现，这时候就需要回退。

#### 代码
这个版本是优先关闭左括号的。如果把`generateParenthesisRecursive()`函数的第2，第3行换一下，就变成了优先开新的左括号。
```java
public class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> list = new ArrayList<>();
        if (n == 0) { return list; }
        generateParenthesisRecursive(list,"",n,0);
        return list;
    }
    public void generateParenthesisRecursive(List<String> list, String str, int left, int right) {
        if (left == 0 && right == 0) { list.add(str); return; }
        if (right > 0) { generateParenthesisRecursive(list,str+")",left,right-1); }
        if (left > 0) { generateParenthesisRecursive(list,str+"(",left-1,right+1); }
    }
}
```

#### 结果
银弹！
![generate-parentheses-1](/images/leetcode/generate-parentheses-1.png)

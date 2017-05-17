---
layout: post
title: "Leetcode - Algorithm - Sum Root To Leaf Numbers "
date: 2017-05-16 17:58:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","depth first search","backtracking"]
level: "medium"
description: >
---

### 题目
Given a binary tree containing digits from `0-9` only, each root-to-leaf path could represent a number.

An example is the root-to-leaf path `1->2->3` which represents the number `123`.

Find the total sum of all root-to-leaf numbers.

For example,
```
    1
   / \
  2   3
```
The root-to-leaf path `1->2` represents the number `12`.
The root-to-leaf path `1->3` represents the number `13`.

Return the sum = `12 + 13 = 25`.

### 自底向上的动态规划把数字收集起来
比如，
```
    1
   / \
  2   3
```
先用动态规划，用一个`List<List<Integer>>`把数字全收集起来，得到`[[1,2],[1,3]]`。然后再计算结果。

#### 代码
```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
public class Solution {
    public int sumNumbers(TreeNode root) {
        List<List<Integer>> res = dp(root);
        int sum = 0;
        for (List<Integer> list : res) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                sum += list.get(i) * (int)Math.pow(10,size-1-i);
            }
        }
        return sum;
    }
    public List<List<Integer>> dp(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) { return res; }
        if (root.left == null && root.right == null) {
            res.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{root.val})));
            return res;
        }
        List<List<Integer>> left = dp(root.left);
        List<List<Integer>> right = dp(root.right);
        for (List<Integer> list : left) {
            list.add(0,root.val);
            res.add(list);
        }
        for (List<Integer> list: right) {
            list.add(0,root.val);
            res.add(list);
        }
        return res;
    }
}
```

#### 结果
![sum-root-to-leaf-numbers-1](/images/leetcode/sum-root-to-leaf-numbers-1.png)


### 换成用`String`记录数字

#### 代码
```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
public class Solution {
    public int sumNumbers(TreeNode root) {
        List<String> res = dp(root);
        int sum = 0;
        for (String s : res) {
            sum += Integer.parseInt(s);
        }
        return sum;
    }
    public List<String> dp(TreeNode root) {
        List<String> res = new ArrayList<>();
        if (root == null) { return res; }
        if (root.left == null && root.right == null) {
            res.add(""+root.val);
            return res;
        }
        List<String> left = dp(root.left);
        List<String> right = dp(root.right);
        for (String s : left) {
            res.add(root.val + s);
        }
        for (String s : right) {
            res.add(root.val + s);
        }
        return res;
    }
}
```

#### 结果
`List<String>`比`List<List<Integer>>`快。但还不是银弹！
![sum-root-to-leaf-numbers-2](/images/leetcode/sum-root-to-leaf-numbers-2.png)


### 自顶向下回溯算法
这题如果用自底向上的动态规划，主要问题在于当前数字放在哪一位比较难确定，会有很多种可能。
```
    1
   / \
  2   3
 /
4
```
这里的`1`既是`124`的百位，也是`13`的十位。从底下递归上来，需要附带的信息比较多。

但如果翻过来自顶向下推进，就不存在这个问题。每往下一层，只需要把积累起来的总和`*10`，再加上当前节点的值即可。比如，从`2`推进到`4`的时候，已知之前得到的和为`12`，然后计算`12*10+4=124`即可。

#### 代码
```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
public class Solution {
    public int sumNumbers(TreeNode root) {
        if (root == null) { return 0; }
        int[] res = new int[1];
        backtracking(root,0,res);
        return res[0];
    }
    public void backtracking(TreeNode root, int sum, int[] res) {
        sum = sum * 10 + root.val;
        if (root.left == null && root.right == null) { res[0] += sum; }
        if (root.left != null) { backtracking(root.left,sum,res); }
        if (root.right != null) { backtracking(root.right,sum,res); }
    }
}
```

#### 结果
银弹！
![sum-root-to-leaf-numbers-3](/images/leetcode/sum-root-to-leaf-numbers-3.png)

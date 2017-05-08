---
layout: post
title: "Leetcode - Algorithm - Unique Binary Search Trees Two "
date: 2017-05-04 16:02:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","dynamic programming","divid and conquer"]
level: "medium"
description: >
---

### 思考问题的范式（下面的`BST`指代“二叉搜索树”）
1. 笨办法该怎么做？ 根据数组，把`BTS`构造出来，然后和现有的所有`BTS`比较。
2. 痛点在哪儿？构造`BTS`，比较`BTS`开销很大。
3. 有没有可能解决这个痛点？原本的过程是：输入数组 -> `BTS` -> 在标准空间内比较。有没有可能绕过`BTS`，直接把数组映射到标准空间？
4. 基本不可能。首先，设想的映射到标准空间的方法是类似计算哈希值的方法，每一层对应一个素数为乘数。这样每棵构造构造不同的树，最后都有个不同的积。但这个方法很难实现。因为以 **DFS** 的顺序遍历`BTS`容易，但以 **BFS** 的顺序就非常困难。第二，题目本身需要返回所有可能的树。构造树的过程是不可能跳过的。计算出哈希值最后还要回来构造树。
5. 明确基本事实：树需要一棵一棵构造，节点要一个一个增加。
6. 我手上有哪些常规武器？回溯算法，动态规划，贪心算法？能不能够用来解决问题？
7. 构造一棵树的时候，有没有可能每次保留大部分之前的结构，只改动很少部分的节点？
8. 很难。因为全排列相邻两种排列的变化幅度可以很大，经常整棵树需要重新构造。
9. **已知2个节点的时候有两种结构`[1,2]`,`[2,1]`，变到3个节点的情况只是加入新节点`3`。有没有可能根据`n-1`个节点的结果，推演出`n`个节点的结果？**
10. 答案是肯定的！发现一个规律：因为新加入的`3`一定大于之前`1~n-1`中所有的数字， **所以不管在哪里插入，都是插在右节点。然后原来的右节点子树，整棵变成新节点的左子树。**
11. 本来以为可能需要去掉重复的情况。后来推演下来，这样每次都保证是新的结构。
12. 最后对于`BST`这种典型的同构子结构，不要忘了 **分治法**。 这题通过 **把任何树抽象成`{左子树，根节点，右子树}`三部分**，可以轻松地用递归解决问题。思路和遍历`BST`非常相似。

> 遇到问题，从几大常规武器入手：回溯，动态规划，贪心算法，分治法。


### 题目
Given an integer n, generate all structurally unique BST's (binary search trees) that store values 1...n.

For example,
Given `n = 3`, your program should return all 5 unique BST's shown below.
```
   1         3     3      2      1
    \       /     /      / \      \
     3     2     1      1   3      2
    /     /       \                 \
   2     1         2                 3
```

### 笨办法，一颗颗树构造出来，然后比较
因为给出的`TreeNode`数据结构太简陋，什么功能都没有。需要自己用函数实现所有 **构造**， **插入**， **比较** 的功能函数。还附带了一个用来生成排列组合数组的函数`permutation()`。

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
    public List<TreeNode> generateTrees(int n) {
        List<TreeNode> res = new ArrayList<>();
        List<List<Integer>> numsStream = permutation(n);
        for (List<Integer> nums : numsStream) {
            TreeNode tree = buildTree(nums);
            if (isNewTree(res,tree)) { res.add(tree); }
        }
        return res;
    }
    public boolean isNewTree(List<TreeNode> res, TreeNode tree) {
        for (TreeNode member : res) {
            if (equals(member,tree)) { return false; }
        }
        return true;
    }
    public List<List<Integer>> permutation(int n) {
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= n; i++) { nums.add(i); }
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        permutationRecur(nums,new ArrayList<Integer>(),res);
        return res;
    }
    public void permutationRecur(List<Integer> nums, List<Integer> temp, List<List<Integer>> res) {
        if (nums.isEmpty() && !temp.isEmpty()) { res.add(new ArrayList<Integer>(temp)); return; }
        for (int i = 0; i < nums.size(); i++) {
            temp.add(nums.remove(i));
            permutationRecur(nums,temp,res);
            nums.add(i,temp.remove(temp.size()-1));
        }
    }
    public TreeNode buildTree(List<Integer> nums) {
        if (nums.isEmpty()) { return null; }
        TreeNode res = new TreeNode(nums.get(0));
        for (int i = 1; i < nums.size(); i++) {
            res = insert(res,nums.get(i));
        }
        return res;
    }
    public TreeNode insert(TreeNode tree, int num) {
        TreeNode newNode = new TreeNode(num);
        if (tree == null) { return newNode; }
        TreeNode cur = tree, pre = tree;
        boolean goLeft = num < tree.val;
        while (cur != null) {
            pre = cur;
            if (num < cur.val) {
                cur = cur.left;
                goLeft = true;
            } else { // num > cur.val  (num == cur.val is unreachable)
                cur = cur.right;
                goLeft = false;
            }
        }
        if (goLeft) {
            pre.left = newNode;
        } else {
            pre.right = newNode;
        }
        return tree;
    }
    public boolean equals(TreeNode first, TreeNode second) {
        if (first == null && second == null) { return true; }
        if (!nodeEquals(first,second)) { return false; } // 数值不等，或者只有其中一个为空
        // assert: first != null && second != null && first.val == second.val
        return (equals(first.left,second.left) && equals(first.right,second.right));
    }
    public boolean nodeEquals(TreeNode first, TreeNode second) {
        return (first == null)? second == null : first.val == second.val;
    }
}
```

#### 结果
结果肯定非常慢。但实现笨办法，为后面的优化铺平了道路。
![unique-binary-search-trees-two-1](/images/leetcode/unique-binary-search-trees-two-1.png)


### 动态规划 + 回溯
已知2个节点的时候有两种结构`[1,2]`,`[2,1]`，变到3个节点的情况只是加入新节点`3`。
已知3个节点的时候有5种不同结构的树，在此基础上，看新节点`4`怎么插入，可以得到新的结构的树。以`1->2->3`为例，插入新节点`4`有4种插法。
![unique-binary-search-trees-two-100](/images/leetcode/unique-binary-search-trees-two-100.png)

插入动作可以归纳为三步走：
1. 把右节点整棵子树拆下来。
2. 把新节点替代原来的子树。
3. 把原先的右节点子树整棵再嫁接到新节点的左子树。


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
    public List<TreeNode> generateTrees(int n) {
        List<TreeNode> res = new ArrayList<>();
        if (n <= 0) { return res; }
        if (n == 1) { res.add(new TreeNode(1)); return res; }
        res.addAll(generateTrees(n-1));
        TreeNode nodeN = new TreeNode(n);
        int size = res.size();
        for (int i = 0; i < size; i++) {
            TreeNode dummy = new TreeNode(Integer.MIN_VALUE), cur = dummy;
            dummy.right = res.remove(0);
            while (cur != null) {
                TreeNode oldRight = cur.right;
                cur.right = nodeN; // 嫁接
                nodeN.left = oldRight;
                res.add(copyOf(dummy.right)); // 定妆照
                cur.right = oldRight; // 回溯
                nodeN.left = null;
                cur = cur.right;
            }
        }
        return res;
    }
    public TreeNode copyOf(TreeNode tree) {
        if (tree == null) { return null; }
        TreeNode head = new TreeNode(tree.val);
        head.left = copyOf(tree.left);
        head.right = copyOf(tree.right);
        return head;
    }
}
```

#### 结果
![unique-binary-search-trees-two-2](/images/leetcode/unique-binary-search-trees-two-2.png)


### 分治法（递归版） 空间复杂度最小
结构上，每一棵二叉树都可以抽象为三部分：
![unique-binary-search-trees-two-bst](/images/leetcode/unique-binary-search-trees-two-bst.png)

这一题因为所有的节点包含`1~n`的连续数字。所以每一棵树都分为`1~k-1`,`k`,`k+1,n`，三个部分。用分治法可以很简单地得到结果。终结条件是当`[1,k-1]`和`[k+1,n]`空间为空时，返回只包含一个`null`元素的`List`。

这个版本速度未必是最快的，但 **空间复杂度一定是最小的**。因为每一棵树，只有`root`节点是新创建的，`left`子树和`right`子树都是和之前的树共有的。

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
    public List<TreeNode> generateTrees(int n) {
        if (n <= 0) { return new ArrayList<TreeNode>(); }
        return recursive(1,n);
    }
    public List<TreeNode> recursive(int start, int end) {
        List<TreeNode> res = new ArrayList<>();
        if (start > end) { res.add(null); return res; } // must have null
        for (int index = start; index <= end; index++) {
            List<TreeNode> leftSubTree = recursive(start,index-1);
            List<TreeNode> rightSubTree = recursive(index+1,end);
            for (TreeNode ln : leftSubTree) {
                for (TreeNode rn : rightSubTree) {
                    TreeNode root = new TreeNode(index);
                    root.left = ln;
                    root.right = rn;
                    res.add(root);
                }
            }
        }
        return res;
    }
}
```

#### 结果
![unique-binary-search-trees-two-3](/images/leetcode/unique-binary-search-trees-two-3.png)

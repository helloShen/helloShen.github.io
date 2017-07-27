---
layout: post
title: "Leetcode - Algorithm - Verify Preorder Sequence In Bst "
date: 2017-07-26 21:37:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","tree"]
level: "medium"
description: >
---

### 主要收获
对一个问题抽象的过程，经常需要经过下面3个步骤，
1. 把操作具体化。老老实实一步步实现整个过程。所有进一步的投机取巧，都是从最基本的操作中来的。比如这道题，想要抛开二叉树，最好的办法就是先用二叉树实现一遍。
2. 归纳问题的本质，最好给出数学定义。这题的核心问题就是：数字序列什么时候才不是`preorder`的？当我能准确地给出数学定义的时候，问题基本上就解决了。
3. 最后落实到算法的时候一定要精确地知道每一步都发生了什么。比如第三种方法，向前遍历所有历史数字的做法是可以得出正确结果。但精确地回溯出新节点具体是哪个父节点的右子节点，就会发现实际上只需要保留非常少一部分的历史节点即可。直接帮助想到要用`Stack`.

### 题目
Given an array of numbers, verify whether it is the correct preorder traversal sequence of a binary search tree.

You may assume each number in the sequence is unique.

Follow up:
Could you do it using only constant space complexity?

### 解题思路
下面的5种方法，代表了我一步一步思考，并抽象问题的过程。
![verify-preorder-sequence-in-bst](/images/leetcode/verify-preorder-sequence-in-bst.png)

### 第一步：最朴素的方法完成一次。完整实现二叉树, 复杂度 $$O(n)$$
既然要判断是否符合二叉树`preorder`遍历的顺序，最简单的做法就是真的构建一棵二叉树。然后用`preorder`递归遍历一次所有元素。

虽然复杂度是 $$O(n)$$，但速度很慢。

#### 代码
```java
public class Solution {
    private class TreeNode {
        private int val;
        private TreeNode left;
        private TreeNode right;
        public TreeNode(int val) { this.val = val; }
        public boolean insert(int num) {
            TreeNode node = new TreeNode(num);
            if (val > num) { // go left
                if (left == null) {
                    left = node; return true;
                } else {
                    return left.insert(num);
                }
            } else if (val < num) { // go right
                if (right == null) {
                    right = node; return true;
                } else {
                    return right.insert(num);
                }
            } else { // duplicate value
                return false;
            }
        }

    }
    public boolean verifyPreorder(int[] preorder) {
        if (preorder.length == 0) { return true; }
        TreeNode root = new TreeNode(preorder[0]);
        for (int i = 1; i < preorder.length; i++) {
            if(!root.insert(preorder[i])) { return false; }
        }
        List<Integer> order = preorder(root);
        if (preorder.length != order.size()) { return false; }
        for (int i = 0; i < preorder.length; i++) {
            if (preorder[i] != order.get(i)) { return false; }
        }
        return true;
    }
    private List<Integer> preorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) { return result; }
        result.add(root.val);
        result.addAll(preorder(root.left));
        result.addAll(preorder(root.right));
        return result;
    }
}
```

#### 结果
![verify-preorder-sequence-in-bst-1](/images/leetcode/verify-preorder-sequence-in-bst-1.png)


### 第二步：剖析问题的本质
给出的数组要不符合`preorder`的条件其实是很严格的。

首先，任意给出的一组数，总是能生成一棵合法的二叉树。所以任意的序列，对二叉树来说总是合法的。

到底什么样的序列才不是`preorder`的呢，`[2,3,1]`就是最简单的错误例子，生成的二叉树看上去像这样，
```
    2
   / \
  1   3
```
生成的`preorder`序列是：`[2,1,3]`。

所以这道题的关键就是：
> **序列不符合`preorder`顺序的唯一可能就是：在没有构建完“左子树”的情况下，提前开始构建“右子树”。**

换句话说，**我在构建二叉树的过程中，在进入了某个节点的右子树之后，就不能再进入它的左子树插入节点。也就是不能再出现比这个节点值小的数。** 所以在构建二叉树的同时就可以判断时候符合`preorder`顺序。不需要重新再用`preorder`遍历一遍，再比较。

#### 代码
```java
public class Solution {
    private class TreeNode {
        private int val;
        private TreeNode left;
        private TreeNode right;
        public TreeNode(int val) { this.val = val; }
        public boolean insert(int num, int[] min) {
            if (num < min[0]) { return false; }
            TreeNode node = new TreeNode(num);
            if (val > num) { // go left
                if (left == null) {
                    left = node; return true;
                } else {
                    return left.insert(num,min);
                }
            } else { // go right (assertion: no duplicate value)
                min[0] = Math.max(min[0],val); // KEY: if enter right substree, never go back to left
                if (right == null) {
                    right = node; return true;
                } else {
                    return right.insert(num,min);
                }
            }
        }
    }
    public boolean verifyPreorder(int[] preorder) {
        if (preorder.length == 0) { return true; }
        TreeNode root = new TreeNode(preorder[0]);
        int[] min = new int[]{Integer.MIN_VALUE};
        for (int i = 1; i < preorder.length; i++) {
            if (!root.insert(preorder[i],min)) { return false; }
        }
        return true;
    }
}
```

#### 结果
![verify-preorder-sequence-in-bst-2](/images/leetcode/verify-preorder-sequence-in-bst-2.png)


### 第三步：根据关键点把问题抽象化
问题进一步抽象化，甚至可以考虑看看能不能彻底抛开二叉树的数据结构，
> 比如我有`[a,b,c,d,e,f,g,h]`，也就是说 **当发现`e > b`（e在b的右子树内）之后，`b`就成了一个阈值，`e`后面的`[f,g,h]`都不能再`>b`。**

也就是说，用一个指针遍历数组的过程中，再用第二个指针，不断地回顾前面所有的数，发现小于当前数的，就用来提高阈值。

#### 代码
到这一步代码已经非常简单了。但复杂度却提高了，$$O(n^2)$$。
```java
public class Solution {
    public boolean verifyPreorder(int[] preorder) {
        int min = Integer.MIN_VALUE;
        for (int i = 0; i < preorder.length; i++) {
            if (preorder[i] < min) { return false; }
            for (int j = i; j >= 0; j--) {
                if (preorder[i] > preorder[j]) { min = Math.max(min,preorder[j]); }
            }
        }
        return true;
    }
}
```

#### 结果
![verify-preorder-sequence-in-bst-3](/images/leetcode/verify-preorder-sequence-in-bst-3.png)

### 第四步：把过程彻底清晰化
之前用第二个指针反向再遍历数组的做法还是有点粗放，因为我们明明知道不是所有的节点都需要遍历。甚至每个节点在提高了阈值之后，是可以丢弃的。

第二个关键点就是：
> 可以利用`Stack`容器帮我们更好地完成任务。

因为明显像二叉树的历史节点这种东西，`Stack`是一个非常好的容器。

具体过程如下：
* 新节点如果是父节点的左子节点（小于`Stack`中的所有历史节点），直接压入栈就好。
* 新节点是某个层次的父节点的右节点（大于`Stack`中某些历史节点），就先把小于新节点的所有历史节点都弹出来（相当于回滚找到新节点的父节点），并且用来更新最小阈值。然后把新节点压入栈。

注意：**整个过程中`Stack`中的元素始终保持从大到小的顺序。**

#### 代码
```java
public class Solution {
    public boolean verifyPreorder(int[] preorder) {
        int min = Integer.MIN_VALUE;
        Deque<Integer> stack = new LinkedList<>();
        for (int num : preorder) {
            if (num < min) { return false; }
            while (!stack.isEmpty() && stack.peekFirst() < num) {
                min = stack.pollFirst(); // stack里的数字总是由大到小排列，可以直接更新最小阈值
            }
            stack.offerFirst(num);
        }
        return true;
    }
}
```

#### 结果
![verify-preorder-sequence-in-bst-4](/images/leetcode/verify-preorder-sequence-in-bst-4.png)

### 第五步：一些细微的优化
显然，如果`Stack`能工作，而且数组的长度又是固定的，那就可以用数组替代`Stack`。

#### 代码
```java
public class Solution {
    public boolean verifyPreorder(int[] preorder) {
        int min = Integer.MIN_VALUE;
        int[] stack = new int[preorder.length];
        int index = -1;
        for (int num : preorder) {
            if (num < min) { return false; }
            while (index >= 0 && stack[index] < num) {
                min = stack[index--];// stack里的数字总是由大到小排列，可以直接更新最小阈值
            }
            stack[++index] = num;
        }
        return true;
    }
}
```

更凶残一点的话，如果允许破坏参数数组的话，可以不开一个新的数组，直接在原数组上记录`Stack`信息。因为历史节点的数字不会再用了。

```java
public class Solution {
    public boolean verifyPreorder(int[] preorder) {
        int min = Integer.MIN_VALUE;
        int index = -1;
        for (int num : preorder) {
            if (num < min) { return false; }
            while (index >= 0 && preorder[index] < num) {
                min = preorder[index--];
            }
            preorder[++index] = num;
        }
        return true;
    }
}
```

#### 结果
![verify-preorder-sequence-in-bst-5](/images/leetcode/verify-preorder-sequence-in-bst-5.png)

### 总结
问题的抽象很重要，但问题的具体化更重要。如果没有一步一步实际操作整个过程，是很难找到问题的关键的，就更不用谈更高的抽象。

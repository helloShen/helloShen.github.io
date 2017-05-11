---
layout: post
title: "Leetcode - Algorithm - Construct Binary Tree From Preorder And Inorder Traversal"
date: 2017-05-09 23:55:14
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","array","depth first search"]
level: "medium"
description: >
---

### 题目
Given preorder and inorder traversal of a tree, construct the binary tree.

Note:
You may assume that duplicates do not exist in the tree.

### 顺序遍历`preorder` $$O(n\log_{}{n})$$
遍历`preorder`，用根据节点在`inorder`中的顺序，决定插在哪里。假设现有`preorder=[8,2,10,11]`，`inorder=[2,8,10,11]`。其中`2-8-10`已经放好，最后一个`11`。插入的时候从根节点开始比较在`inorder`中的顺序，
```
  8
 / \
2  10
```
`inorder`中`11`在`8`的后面，`11`就应该插在`8`的右节点，如果`8`的右节点不为空，则和右节点的`10`比较，`11`在`inorder`中比`10`还要靠后，因此，插入作为`10`的右节点。
```
      8
     / \
    2  10
         \
         11
```

注意，这里假设的前提是`preorder`和`inorder`两个数组本身没有错误，它们都描述了一个正确的二叉树。所以检错的步骤都被省略了。

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
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0 || preorder.length != inorder.length) { return null; }
        Map<Integer,Integer> inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) { inorderMap.put(inorder[i],i); }
        TreeNode root = new TreeNode(preorder[0]);
        for (int i = 1; i < preorder.length; i++) {
            int index = inorderMap.get(preorder[i]);
            TreeNode cur = root;
            while (cur != null) {
                if (inorderMap.get(cur.val) < index) { // 新节点应当在这个节点的右子树
                    if (cur.right == null) {
                        cur.right = new TreeNode(preorder[i]); break;
                    } else {
                        cur = cur.right;
                    }
                } else { // 新节点应当在这个节点的左子树
                    if (cur.left == null) {
                        cur.left = new TreeNode(preorder[i]); break;
                    } else {
                        cur = cur.left;
                    }
                }
            }
        }
        return root;
    }
}
```

#### 结果
银弹还没找到。
![construct-binary-tree-from-preorder-and-inorder-traversal-1](/images/leetcode/construct-binary-tree-from-preorder-and-inorder-traversal-1.png)


### 分治递归
根据老套路，看到二叉树首先就应该考虑分治法。思路和前面一种做法有点不同。假设：
* `preorder = [14, 6, 4, 1, 3, 5, 12, 8, 7, 10, 13, 16, 15, 19, 17, 18, 20]`
* `inorder = [1, 3, 4, 5, 6, 7, 8, 10, 12, 13, 14, 15, 16, 17, 18, 19, 20]`

首先以preorder首个元素，`14`节点为根。在inorder里查找`14`，并据此将inorder分为三个部分，左子树从`1~13`有10个元素，右子树从`15~20`有6个元素。
`inorder` = `left: [1, 3, 4, 5, 6, 7, 8, 10, 12, 13]` + `root: [14]` + `right: [15, 16, 17, 18, 19, 20]`

然后在preorder里，去掉首元素`14`，取inorder的左子树相同长度的10个元素，`[6, 4, 1, 3, 5, 12, 8, 7, 10, 13]`，这就是preorder的左子树。剩下的6个元素就是preorder的右子树。

然后重复上面的步骤，直到元素的窗口为空。

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
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0) { return null; }
        return buildTreeRecur(preorder,0,preorder.length-1,inorder,0,inorder.length-1);
    }
    public TreeNode buildTreeRecur(int[] preorder, int preLo, int preHi, int[] inorder, int inLo, int inHi) {
        if (preLo > preHi) { return null; }
        TreeNode root = new TreeNode(preorder[preLo]);
        int indexInInorder = indexOf(inorder,inLo,inHi,preorder[preLo]);
        int leftSize = indexInInorder - inLo;
        int rightSize = inHi - indexInInorder;
        root.left = buildTreeRecur(preorder,preLo+1,preLo+leftSize,inorder,inLo,indexInInorder-1);
        root.right = buildTreeRecur(preorder,preLo+leftSize+1,preHi,inorder,indexInInorder+1,inHi);
        return root;
    }
    // return the index of target number in the array
    // return -1 if target not found
    // [lo,hi], both sides are inclusive
    public int indexOf(int[] nums, int lo, int hi, int target) {
        for (int i = lo; i <= hi; i++) {
            if (nums[i] == target) { return i; }
        }
        return -1;
    }
}
```

#### 结果
已经到达第一个峰值，但还不是银弹！
![construct-binary-tree-from-preorder-and-inorder-traversal-2](/images/leetcode/construct-binary-tree-from-preorder-and-inorder-traversal-2.png)


### 去掉`indexOf()`函数
之前的方法每次都要到`inorder`里查询在`inorder`里的index。如果按照元素在`preorder`里的顺序，事先将每个元素在`inorder`里的index全部用一个`HashMap`记录下来，就可以$$O(1)$$的速度查询。

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
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0) { return null; }
        Map<Integer,Integer> inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i],i);
        }
        return buildTreeRecur(preorder,0,preorder.length-1,inorder,0,inorder.length-1,inorderMap);
    }
    public TreeNode buildTreeRecur(int[] preorder, int preLo, int preHi, int[] inorder, int inLo, int inHi, Map<Integer,Integer> inorderMap) {
        if (preLo > preHi) { return null; }
        TreeNode root = new TreeNode(preorder[preLo]);
        int indexInInorder = inorderMap.get(preorder[preLo]);
        int leftSize = indexInInorder - inLo;
        int rightSize = inHi - indexInInorder;
        root.left = buildTreeRecur(preorder,preLo+1,preLo+leftSize,inorder,inLo,indexInInorder-1,inorderMap);
        root.right = buildTreeRecur(preorder,preLo+leftSize+1,preHi,inorder,indexInInorder+1,inHi,inorderMap);
        return root;
    }
}
```

#### 清理代码
不需要同时传递`preorder`和`inorder`两个数组，以及他们的窗口。只需要传递`inorder`上的窗口。至于`preorder`只需要用一个指针`cur`指向当前根节点的位置即可。
```java
public class Solution {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0) { return null; }
        Map<Integer,Integer> inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) { inorderMap.put(inorder[i],i); }
        return buildTreeRecur(preorder,0,0,inorder.length-1,inorderMap);
    }
    public TreeNode buildTreeRecur(int[] preorder, int cur, int lo, int hi, Map<Integer,Integer> inorderMap) {
        if (lo > hi) { return null; }
        TreeNode root = new TreeNode(preorder[cur]);
        int indexInInorder = inorderMap.get(preorder[cur]);
        int leftSize = indexInInorder - lo;
        int rightSize = hi - indexInInorder;
        root.left = buildTreeRecur(preorder,cur+1,lo,indexInInorder-1,inorderMap);
        root.right = buildTreeRecur(preorder,cur+leftSize+1,indexInInorder+1,hi,inorderMap);
        return root;
    }
}
```

#### 结果
银弹！
![construct-binary-tree-from-preorder-and-inorder-traversal-3](/images/leetcode/construct-binary-tree-from-preorder-and-inorder-traversal-3.png)

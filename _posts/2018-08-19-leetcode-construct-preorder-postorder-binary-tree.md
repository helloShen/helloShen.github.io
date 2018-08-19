---
layout: post
title: "Leetcode - Algorithm - Construct Preorder Postorder Binary Tree "
date: 2018-08-19 01:18:37
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree"]
level: "medium"
description: >
---

### 题目
Return any binary tree that matches the given preorder and postorder traversals.

Values in the traversals pre and post are distinct positive integers.


Example 1:
```
Input: pre = [1,2,4,5,3,6,7], post = [4,5,2,6,7,3,1]
Output: [1,2,3,4,5,6,7]
```

Note:
* 1 <= pre.length == post.length <= 30
* pre[] and post[] are both permutations of 1, 2, ..., pre.length.
* It is guaranteed an answer exists. If there exists multiple answers, you can return any of them.

### 关键要理解数组中二叉树子树的结构
首先`preorder`的首元素即为整棵树的根节点。而根节点同样也在`postorder`数组的末尾。
```
[root][......left......][...right..]  ---pre
[......left......][...right..][root]  ---post
```
然后他们的左子树和右子树是两块连续的区域。关键就在于 **找到左右子树的边界在哪里**。

这里用到一个技巧如下图所示。首先可以确定的是`preorder`中紧挨着首元素根节点的第二个元素即为左子树的根节点。然后找到这个元素在`postorder`数组中的位置，就找到了`postorder`数组中左子树的右边界。这很重要。因为以此就能确定左子树的总结点数。然后再通过这个节点数反过来确定`preorder`中左子树的右边界。
![construct-preorder-postorder-binary-tree-a](/images/leetcode/construct-preorder-postorder-binary-tree-a.png)

#### 代码
```java
class Solution {
    public TreeNode constructFromPrePost(int[] pre, int[] post) {
        preArray = pre;
        postArray = post;
        return helper(0,preArray.length-1,0,postArray.length-1);
    }
    private int[] preArray;
    private int[] postArray;
    private TreeNode helper(int preLeft, int preRight, int postLeft, int postRight) {
        if (preLeft > preRight) { return null; }
        TreeNode root = new TreeNode(preArray[preLeft]);     
        if (preLeft == preRight) {
            return root;   
        }
        int preLeftSubStart = preLeft + 1;                             
        int preLeftSubEnd = 0;                             
        int postLeftSubStart = postLeft;
        int postLeftSubEnd = 0;                             
        int leftSubLen = 0;                             
        for (int i = postLeft; i <= postRight; i++) {
            if (postArray[i] == preArray[preLeftSubStart]) {
                postLeftSubEnd = i;
                leftSubLen = postLeftSubEnd - postLeft + 1;
            }                            
        }
        preLeftSubEnd = preLeftSubStart + leftSubLen - 1;                             
        root.left = helper(preLeftSubStart, preLeftSubEnd, postLeftSubStart, postLeftSubEnd);                             
        root.right = helper(preLeftSubEnd+1, preRight, postLeftSubEnd+1, postRight-1);                            
        return root;
    }
}
```

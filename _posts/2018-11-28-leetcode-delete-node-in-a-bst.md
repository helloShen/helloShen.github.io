---
layout: post
title: "Leetcode - Algorithm - Delete Node In A Bst "
date: 2018-11-28 19:39:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree", "binary search tree"]
level: "meidum"
description: >
---

### 题目
Given a root node reference of a BST and a key, delete the node with the given key in the BST. Return the root node reference (possibly updated) of the BST.

Basically, the deletion can be divided into two stages:
1. Search for a node to remove.
2. If the node is found, delete the node.

Note: Time complexity should be O(height of tree).

Example:
```
root = [5,3,6,2,4,null,7]
key = 3

    5
   / \
  3   6
 / \   \
2   4   7

Given key to delete is 3. So we find the node with value 3 and delete it.

One valid answer is [5,4,6,2,null,null,7], shown in the following BST.

    5
   / \
  4   6
 /     \
2       7

Another valid answer is [5,2,6,null,4,null,7].

    5
   / \
  2   6
   \   \
    4   7
```

### 和后继节点交换
思路是这样，首先，如果要删除的节点只有一个子节点（要么左子，要么右子），问题很简单，直接删除这个节点，然后用子树接上即可。

```
     5
    / \
-> 4   6 <-
  /     \
 2       7

如果要删[4]: 直接拿[2]接上
如果要删[6]: 直接拿[7]接上
```

问题在于，当删除目标节点有两个子节点，就需要决定先嫁接哪一棵子树，再嫁接另一棵。如果处理不好树的平衡性会变差。

然后T.Hibbard在1962年提出一个解决办法，就是“替换后继节点”。思路是这样，
1. 如果目标节点的右子节点为空，直接嫁接左子节点。
2. 如果右子节点不为空，将右子树中的最小元素（即目标元素的后继节点，数值上比目标元素大的紧挨着的下一个数字）拷贝至目标节点，然后删除这个后继节点。

第一条很好理解，第二条如图所示，
```
     7
    / \
-> 3   8
  / \   \
 2   6   9
    /
   5

要删除[3]，发现右子树[6,5]不为空。用其中最小元素[5]替换[3]，变为，
     7
    / \
-> 5   8
  / \   \
 2   6   9
    /
   5

最后再删除[5]这个后继节点。
```


#### 代码
```java
class Solution {
    public TreeNode deleteNode(TreeNode root, int key) {
        return deleteHelper(root, root, null, key);
    }

    private TreeNode deleteHelper(TreeNode root, TreeNode ite, TreeNode parent, int key) {
        if (ite == null) return root;
        if (ite.val == key) { // swap with successor
            if (ite.right != null) {
                ite.val = deleteMin(ite);
            } else { // connect with left sub-tree
                if (ite == root) {
                    root = root.left;
                } else {
                    if (ite == parent.left) {
                        parent.left = ite.left;
                    } else {
                        parent.right = ite.left;
                    }
                }
            }
            return root;
        } else if (ite.val > key) {
            return deleteHelper(root, ite.left, ite, key);
        } else {
            return deleteHelper(root, ite.right, ite, key);
        }
    }

    /**
     * Delete the min element in node's right subtree
     * assertion: node != null &&  node.right != null
     * @return deleted min value
     */
    private int deleteMin(TreeNode node) {
        TreeNode root = node.right, parent = node;
        if (root.left == null) {
            parent.right = root.right;
            return root.val;
        }
        while (root.left != null) {
            parent = root;
            root = root.left;
        }
        parent.left = root.right;
        return root.val;
    }
}
```

#### 结果
![delete-node-in-a-bst-1](/images/leetcode/delete-node-in-a-bst-1.png)

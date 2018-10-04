---
layout: post
title: "Leetcode - Algorithm - Convert Bst To Linked List "
date: 2018-10-03 21:46:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search tree", "tree", "stack"]
level: "medium"
description: >
---

### 题目
Convert a BST to a sorted circular doubly-linked list in-place. Think of the left and right pointers as synonymous to the previous and next pointers in a doubly-linked list.

Let's take the following BST as an example, it may help you understand the problem better:
![convert-bst-to-linked-list-a](/images/leetcode/convert-bst-to-linked-list-a.png)

We want to transform this BST into a circular doubly linked list. Each node in a doubly linked list has a predecessor and successor. For a circular doubly linked list, the predecessor of the first element is the last element, and the successor of the last element is the first element.

The figure below shows the circular doubly linked list for the BST above. The "head" symbol means the node it points to is the smallest element of the linked list.
![convert-bst-to-linked-list-b](/images/leetcode/convert-bst-to-linked-list-b.png)

Specifically, we want to do the transformation in place. After the transformation, the left pointer of the tree node should point to its predecessor, and the right pointer should point to its successor. We should return the pointer to the first element of the linked list.

The figure below shows the transformed BST. The solid line indicates the successor relationship, while the dashed line means the predecessor relationship.
![convert-bst-to-linked-list-c](/images/leetcode/convert-bst-to-linked-list-c.png)



### 递归遍历所有节点
注意这里的返回值是`Node[2]`以当前节点`root`为根节点的子树的最大值和最小值。
```
recursion(Node#2)返回值为：[Node#1, Node#3]
        4
      /   \
     2     5
    / \
   1   3
```

#### 代码
```java
class Solution {
    public Node treeToDoublyList(Node root) {
        if (root == null) return null;
        Node[] headTail = recursion(root);
        headTail[0].left = headTail[1];
        headTail[1].right = headTail[0];
        return headTail[0];
    }
    private Node[] recursion(Node root) {
        if (root == null) return null;
        Node[] res = new Node[]{root, root};
        if (root.left != null) {
            Node[] left = recursion(root.left);
            res[0] = left[0];
            root.left = left[1];
            left[1].right = root;
        }
        if (root.right != null) {
            Node[] right = recursion(root.right);
            res[1] = right[1];
            root.right = right[0];
            right[0].left = root;
        }
        return res;
    }
}
```

#### 结果
![convert-bst-to-linked-list-1](/images/leetcode/convert-bst-to-linked-list-1.png)


### 二叉树能用递归解的也能用`Stack`迭代法解

#### 代码
```java
class Solution {
    public Node treeToDoublyList(Node root) {
        if (root == null) return null;
        Node dummy = new Node();
        Node pre = dummy;
        Node curr = root;
        LinkedList<Node> stack = new LinkedList<>();
        while (!stack.isEmpty() || curr != null) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();
            pre.right = curr;
            curr.left = pre;
            pre = curr;
            curr = curr.right;
        }
        dummy.right.left = pre;
        pre.right = dummy.right;
        return dummy.right;
    }
}
```

#### 结果
![convert-bst-to-linked-list-2](/images/leetcode/convert-bst-to-linked-list-2.png)

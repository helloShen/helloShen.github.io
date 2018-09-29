---
layout: post
title: "Leetcode - Algorithm - Serialize And Deserialize Bst "
date: 2018-09-29 15:12:12
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","tree","binary search tree"]
level: "medium"
description: >
---

### 题目
Serialization is the process of converting a data structure or object into a sequence of bits so that it can be stored in a file or memory buffer, or transmitted across a network connection link to be reconstructed later in the same or another computer environment.

Design an algorithm to serialize and deserialize a binary search tree. There is no restriction on how your serialization/deserialization algorithm should work. You just need to ensure that a binary search tree can be serialized to a string and this string can be deserialized to the original tree structure.

The encoded string should be as compact as possible.

Note: Do not use class member/global/static variables to store states. Your serialize and deserialize algorithms should be stateless.


### `pre-order`遍历二叉树
基本思想很简单，就是以`pre-order`的顺序遍历，并序列化节点。

`pre-order`就是`根节点->左子节点->右子节点`的顺序。
```
pre-order: a -> b -> c
        a
       / \
      b   c
```

#### 用`#`表示`null`节点
实现起来可以标记出`null`节点。序列化以后是这样，
```
44-30-23-17-#-#-25-#-#-44-#-#-90-67-62-54-#-#-#-#-#-
```

这样就可以直接用递归反序列化。因为`null`空节点可以告诉我哪里该回到上层节点。
```java
public class Codec {

    private static final String SPLT = "-";
    private static final char SPLT_C = '-';
    private static final String NULL = "#";


    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        preOrder(root, sb);
        return sb.toString();
    }
    public void preOrder(TreeNode root, StringBuilder sb) {
        if (root == null) {
            sb.append(NULL);
            sb.append(SPLT);
            return;
        }
        sb.append(String.valueOf(root.val));
        sb.append(SPLT);
        preOrder(root.left, sb);
        preOrder(root.right, sb);
    }
    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        idx = 0;
        return deserializeHelper(data);
    }
    private int idx;
    private TreeNode deserializeHelper(String data) {
        if (idx == data.length()) return null;
        int end = data.indexOf(SPLT_C, idx);
        String val = data.substring(idx, end);
        idx = end + 1;
        if (val.equals(NULL)) return null;
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(data);
        node.right = deserializeHelper(data);
        return node;
    }
}
```

#### 结果
![serialize-and-deserialize-bst-1](/images/leetcode/serialize-and-deserialize-bst-1.png)


#### 不标记`null`节点，一个一个插入新元素
也可以不标记`null`节点。序列化之后更紧凑，
```
44-30-23-17-25-44-90-67-62-54-
```
缺点就是无法直接递归构造复原二叉树，因为无法提示程序什么时候该回到上层程序栈。但可以每次都从根节点插入新元素的方式构建二叉树。需要重新写一个`insert()`函数。
```java
public class Codec {

    private static final String SPLT = "-";
    private static final char SPLT_C = '-';

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        preOrder(root, sb);
        return sb.toString();
    }
    public void preOrder(TreeNode root, StringBuilder sb) {
        if (root == null) return;
        sb.append(String.valueOf(root.val));
        sb.append(SPLT);
        preOrder(root.left, sb);
        preOrder(root.right, sb);
    }
    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if (data.length() == 0) return null;
        int end = data.indexOf(SPLT_C);
        TreeNode root = new TreeNode(Integer.parseInt(data.substring(0, end)));
        deserializeHelper(data, end + 1, root);
        return root;
    }
    // assertion: (root != null)
    private void deserializeHelper(String data, int start, TreeNode root) {
        if (start == data.length()) return;
        int end = data.indexOf(SPLT_C, start);
        insert(root, Integer.parseInt(data.substring(start, end)));
        deserializeHelper(data, end + 1, root);
    }
    // Insert new node into given binary search tree
    public void insert(TreeNode root, int val) {
        if (root == null) return;
        if (root.val >= val) {
            if (root.left != null) {
                insert(root.left, val);
            } else {
                TreeNode newNode = new TreeNode(val);
                root.left = newNode;
            }
        } else {
            if (root.right != null) {
                insert(root.right, val);
            } else {
                TreeNode newNode =new TreeNode(val);
                root.right = newNode;
            }
        }
    }
}
```

#### 结果
![serialize-and-deserialize-bst-1](/images/leetcode/serialize-and-deserialize-bst-1.png)

---
layout: post
title: "Leetcode - Algorithm - Populating Next Right Pointers In Each Node Two"
date: 2017-05-14 15:27:57
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["breadth first search","tree"]
level: "medium"
description: >
---

### 题目
Follow up for problem "Populating Next Right Pointers in Each Node".

What if the given tree could be any binary tree? Would your previous solution still work?

Note:

You may only use constant extra space.
For example,
Given the following binary tree,
```
         1
       /  \
      2    3
     / \    \
    4   5    7
```
After calling your function, the tree should look like:
```
         1 -> NULL
       /  \
      2 -> 3 -> NULL
     / \    \
    4-> 5 -> 7 -> NULL
```

### "Level Order"遍历整棵树
先尝试最普适的用`List`缓存每行节点元素的方法。既然能按行缓存每行的元素，接下来只需要把元素挨个用`next`连起来就可以。

#### 代码
```java
/**
 * Definition for binary tree with next pointer.
 * public class TreeLinkNode {
 *     int val;
 *     TreeLinkNode left, right, next;
 *     TreeLinkNode(int x) { val = x; }
 * }
 */
public class Solution {
    public void connect(TreeLinkNode root) {
        if (root == null) { return; }
        List<TreeLinkNode> buffer = new ArrayList<>();
        buffer.add(root);
        while (!buffer.isEmpty()) {
            int size = buffer.size();
            for (int i = 0; i < size; i++) {
                TreeLinkNode node = buffer.remove(0);
                if (i+1 < size) { // 链接同层下一个元素
                    TreeLinkNode nextNode = buffer.get(0);
                    node.next = nextNode;
                }
                if (node.left != null) { buffer.add(node.left); }
                if (node.right != null) { buffer.add(node.right); }
            }
        }
    }
}
```

#### 结果
![populating-next-right-pointers-in-each-node-1](/images/leetcode/populating-next-right-pointers-in-each-node-1.png)


### 利用`next`指针按行遍历整棵树
只需要保证处理每行的子节点的时候，这一行所有的元素都用`next`指针连接好就可以。利用`next`指针总能找到下一个邻居。

#### 代码 （递归版）
```java
/**
 * Definition for binary tree with next pointer.
 * public class TreeLinkNode {
 *     int val;
 *     TreeLinkNode left, right, next;
 *     TreeLinkNode(int x) { val = x; }
 * }
 */
public class Solution {
    public void connect(TreeLinkNode root) {
        recursion(root,null);
    }
    public void recursion(TreeLinkNode root, TreeLinkNode nextLevel) {
        if (root == null) { return; }
        if (nextLevel == null) {
            if (root.left != null) {
                nextLevel = root.left;
            } else if (root.right != null) {
                nextLevel = root.right;
            }
        }
        if (root.left != null && root.right != null) { // 处理亲兄弟
            root.left.next = root.right;
        }
        TreeLinkNode lastChild = null; // 表兄弟中的前驱左节点
        if (root.right != null) {
            lastChild = root.right;
        } else if (root.left != null){
            lastChild = root.left;
        }
        TreeLinkNode next = root.next, firstNextChild = null; // 表兄弟中的后续右节点
        while (next != null) { // 利用next指针横向遍历
            if (next.left != null) { firstNextChild = next.left; break; }
            if (next.right != null) { firstNextChild = next.right; break; }
            next = next.next;
        }
        if (lastChild != null && firstNextChild != null) {
            lastChild.next = firstNextChild;
        }
        if (next != null) {
            recursion(next,nextLevel);
        } else {
            recursion(nextLevel,null);
        }
    }
}
```

#### 结果
快了3倍。
![populating-next-right-pointers-in-each-node-2](/images/leetcode/populating-next-right-pointers-in-each-node-2.png)

### 还是利用`next`按行遍历，简洁版
简化逻辑。**把迭代的过程一般化。** 设置一个前驱节点指针`pre`。判断条件集中到“当前节点”上来：
1. 如果有左子节点，前驱节点`pre`链接到左子节点。左子节点成为前驱点`pre`。
2. 如果有右子节点，前驱节点`pre`链接到右子节点。右子节点成为前驱点`pre`。
3. 如果左右子节点都为空，等于跳过当前节点。

迭代过程非常干净。理想情况下，每段迭代的逻辑都应该尽量这么干净。

#### 代码

```java
/**
 * Definition for binary tree with next pointer.
 * public class TreeLinkNode {
 *     int val;
 *     TreeLinkNode left, right, next;
 *     TreeLinkNode(int x) { val = x; }
 * }
 */
public class Solution {
    public void connect(TreeLinkNode root) {
        TreeLinkNode cur = root; // 指向当前节点（实际处理的是当前节点的下一层子节点）
        TreeLinkNode nextLevelHead = null; // 下一层子节点的首元素（当这一层节点用完，需要跳转到下一行）
        TreeLinkNode pre = null; // 前一个被链接的子节点。
        while (cur != null) {
            if (cur.left != null) { // 左子节点不为空
                if (pre == null) {
                    nextLevelHead = cur.left;
                } else {
                    pre.next = cur.left;
                }
                pre = cur.left;
            }
            if (cur.right != null) { // 右子节点不为空
                if (pre == null) {
                    nextLevelHead = cur.right;
                } else {
                    pre.next = cur.right;
                }
                pre = cur.right;
            }
            if (cur.next != null) {
                cur = cur.next;
            } else {
                cur = nextLevelHead;
                nextLevelHead = null;
                pre = null;
            }
        }
    }
}
```

#### 结果
因为完全没有多余动作，是可能的最快的做法。真正做到了效率和可读性的统一。
![populating-next-right-pointers-in-each-node-3](/images/leetcode/populating-next-right-pointers-in-each-node-3.png)

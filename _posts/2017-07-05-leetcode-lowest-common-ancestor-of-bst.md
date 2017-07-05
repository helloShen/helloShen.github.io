---
layout: post
title: "Leetcode - Algorithm - Lowest Common Ancestor Of Bst "
date: 2017-07-05 12:49:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","tree","backtracking"]
level: "easy"
description: >
---

### 主要收获 - 要习惯从底层开始构建逻辑
我比较喜欢的逻辑，一定不能是相互间依赖复杂的，规则一定要简单，而且最好没有`corner case`。

> 我喜欢的逻辑应该是从底层开始构建的责任体系。底层逻辑单元只在简单的规则下负责简单的职责。然后上层逻辑单元也在简单的规则下，负责管理底层的逻辑单元。

比如这道题，
* 最底层的逻辑控件是 **“探索指针”**。它只管自己在二叉树里比较探索。只有找到目标元素，或者确定找不到，才终止。
* 上层 **“指针控制单元”** 就是一个`while`循环。监听所有“探索指针”，只负责当发现全部指针都完成工作以后，停止迭代。
* 中层 **“具体操作单元”** 就是在上层`while`循环的过程中，每一步负责比较探索指针指向元素的值，当发现相等值，就记录下来。

`leetcode`这么多实践下来，逻辑越清晰的代码，效率越高，虽然代码行数不一定最少。但代码长短不是最重要的，可读性和可写性才是最重要的。

### 题目
Given a binary search tree (BST), find the lowest common ancestor (LCA) of two given nodes in the BST.

According to the definition of LCA on Wikipedia: “The lowest common ancestor is defined between two nodes v and w as the lowest node in T that has both v and w as descendants (where we allow a node to be a descendant of itself).”
```
        _______6______
       /              \
    ___2__          ___8__
   /      \        /      \
   0      _4       7       9
         /  \
         3   5
```
For example, the lowest common ancestor (LCA) of nodes 2 and 8 is 6. Another example is LCA of nodes 2 and 4 is 2, since a node can be a descendant of itself according to the LCA definition.

### 万能的用回溯算法分别记录通向两个节点的路径
这个方法适用于任何的树，不仅仅限于二叉搜索树。 在递归中储存路径用`Stack`。
* time: $$O(n)$$
* space: $$O(n)$$

#### 代码
```java
public class Solution {
    /**
     * Return the lowest common ancestor of two node p & q in a tree root.
     * Return null if nothing found.
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        Deque<TreeNode> pathP = getPath(root,p);
        Deque<TreeNode> pathQ = getPath(root,q);
        TreeNode ancestor = null;
        while (!pathP.isEmpty() && !pathQ.isEmpty()) {
            TreeNode currP = pathP.pollFirst();
            TreeNode currQ = pathQ.pollFirst();
            if (currP == currQ) { ancestor = currP; }
        }
        return ancestor;
    }
    /**
     * Return a queue of nodes on the path towards target node.
     * Return an empty container if target is not found.
     */
    private Deque<TreeNode> getPath(TreeNode root, TreeNode target) {
        Deque<TreeNode> path = new LinkedList<>();
        if (target == null) { return path; }
        findTarget(root,target,path);
        return path;
    }
    /**
     *  If target is found, return true. path parameter contains all node on the path.
     *  Otherwise, return false. And the path parameter will be empty.
     */
    private boolean findTarget(TreeNode root, TreeNode target, Deque<TreeNode> path) {
        if (root == null) { return false; }
        if (root == target) { path.offerFirst(root); return true; }
        if (findTarget(root.left,target,path)) { path.offerFirst(root); return true; }
        if (findTarget(root.right,target,path)) { path.offerFirst(root); return true; }
        return false;
    }
}
```

#### 结果
![lowest-common-ancestor-of-bst-1](/images/leetcode/lowest-common-ancestor-of-bst-1.png)


### 在二叉树上分别记录通向两个节点的路径
既然是二叉树，就可以在 $$O(\log_{}{n})$$ 的时间里找路径。
* time: $$O(\log_{}{n})$$
* space: $$O(\log_{}{n})$$


#### 比较注重工程的版本
```java
public class Solution {
    /**
     * Return the lowest common ancestor of two node p & q in a tree root.
     * Return null if nothing found.
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathP = getPath(root,p);
        List<TreeNode> pathQ = getPath(root,q);
        TreeNode ancestor = null;
        for (int i = 0; i < pathP.size() && i < pathQ.size(); i++) {
            TreeNode currP = pathP.get(i);
            TreeNode currQ = pathQ.get(i);
            if (currP == currQ) { ancestor = currP; }
        }
        return ancestor;
    }
    /**
     * Return a queue of nodes on the path towards target node.
     * Return an empty container if target is not found.
     */
    private List<TreeNode> getPath(TreeNode root, TreeNode target) {
        List<TreeNode> path = new ArrayList<>();
        if (target == null) { return path; }
        findTarget(root,target,path);
        return path;
    }
    /**
     *  If target is found, return true. path parameter contains all node on the path.
     *  Otherwise, return false. And the path parameter will be empty.
     */
    private void findTarget(TreeNode root, TreeNode target, List<TreeNode> path) {
        if (root == null) { path.clear(); return; }
        path.add(root);
        if (root.val > target.val) {
            findTarget(root.left,target,path);
        } else if (root.val < target.val) {
            findTarget(root.right,target,path);
        }
    }
}
```

#### 结果
![lowest-common-ancestor-of-bst-2](/images/leetcode/lowest-common-ancestor-of-bst-2.png)


#### 下面是比较注重效率的版本，我比较喜欢这个。
不分模块了，直接用两个指针探索路径。如果`p`和`q`都不在二叉树中，也以它们应该插入的位置来确定最近的共同祖先。实际就是返回他们共同路径的最后一个点。

这个版本逻辑非常清楚，而且从底层开始构建的逻辑的层次也非常清楚：

> 下层两个指针`curP`和`curQ`的逻辑：自己管自己探索。只有找到目标元素，或者确定找不到，才终止。

> 上层`while`循环的逻辑：只负责监听`curP`和`curQ`两个指针，当它们都停下来之后，迭代终止。

> 在`while`循环过程中，只要`curP`和`curQ`指向同一个对象，`commonAncestor`就也指向这个对象。

```java
public class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode curP = root, curQ = root;
        TreeNode commonAncestor = null;
        while ((curP != null && curP != p) || (curQ != null && curQ != q)) {
            if (curP == curQ) {
                commonAncestor = curP;
            } else { // 路径分叉，不用再往下探索。
                break;
            }
            if (curP != null && curP != p) {
                if (curP.val > p.val) {
                    curP = curP.left;
                } else if (curP.val < p.val) {
                    curP = curP.right;
                }
            }
            if (curQ != null && curQ != q) {
                if (curQ.val > q.val) {
                    curQ = curQ.left;
                } else if (curQ.val < q.val) {
                    curQ = curQ.right;
                }
            }
        }
        return (curP == null || curQ == null)? null : commonAncestor;
    }
}
```

#### 结果
![lowest-common-ancestor-of-bst-3](/images/leetcode/lowest-common-ancestor-of-bst-3.png)

#### 更简化版
把二叉树路径选择逻辑，独立出来一个函数`next()`。代码更短，但效率上没有多少优化。
```java
/**
 * 如果p或q元素不存在，就以他们应该插入的位置来查找最近共同祖先。
 */
public class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode commonAncestor = null;
        TreeNode curP = root, curQ = root;
        while (curP == curQ && curP != null) {
            commonAncestor = curP;
            curP = next(curP,p);
            curQ = next(curQ,q);
        }
        return commonAncestor;
    }
    /**
     * pointer go forward in binary tree.
     * return null if curr == target (means stop iteration when target is found)
     */
    public TreeNode next(TreeNode curr, TreeNode target) {
        if (curr.val > target.val) {
            return curr.left;
        } else if (curr.val < target.val) {
            return curr.right;
        } else {
            return null;
        }
    }
}
```

#### 结果
![lowest-common-ancestor-of-bst-4](/images/leetcode/lowest-common-ancestor-of-bst-4.png)

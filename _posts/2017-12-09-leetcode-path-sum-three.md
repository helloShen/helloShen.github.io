---
layout: post
title: "Leetcode - Algorithm - Path Sum Three "
date: 2017-12-09 16:29:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree"]
level: "easy"
description: >
---

### 题目
You are given a binary tree in which each node contains an integer value.

Find the number of paths that sum to a given value.

The path does not need to start or end at the root or a leaf, but it must go downwards (traveling only from parent nodes to child nodes).

The tree has no more than 1,000 nodes and the values are in the range -1,000,000 to 1,000,000.

Example:
```
root = [10,5,-3,3,2,null,11,3,-2,null,1], sum = 8

      10
     /  \
    5   -3
   / \    \
  3   2   11
 / \   \
3  -2   1

Return 3. The paths that sum to 8 are:

1.  5 -> 3
2.  5 -> 2 -> 1
3. -3 -> 11
```

### 递归
递归式如下：
> T(root) = T(left) + T(right) + G(root)

其中`T(N)`代表本题的问题，`G(N)`是本问题的一个变种，即：
> 必须以N为根节点的情况下，有多少条path的总和等于sum。

其中问题`G(N)`也可以用递归来完成，而且是比较传统的深度优先(DFS)递归。

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
class Solution {
    public int pathSum(TreeNode root, int sum) {
        if (root == null) { return 0; }
        int left = pathSum(root.left, sum);
        int right = pathSum(root.right,sum);
        int curr = dfs(root,sum);
        return left + right + curr;
    }
    private int dfs(TreeNode root, int remain) {
        if (root == null) { return 0; }
        int left = dfs(root.left,remain - root.val);
        int right = dfs(root.right,remain - root.val);
        int curr = (root.val == remain)? 1 : 0;
        return left + right + curr;
    }
}
```

再简化点可以写成，
```java
class Solution {
    public int pathSum(TreeNode root, int sum) {
        if (root == null) { return 0; }
        return pathSum(root.left, sum) + pathSum(root.right,sum) + dfs(root,sum);
    }
    private int dfs(TreeNode root, int remain) {
        if (root == null) { return 0; }
        return dfs(root.left,remain - root.val) + dfs(root.right,remain - root.val) + ((root.val == remain)? 1 : 0);
    }
}
```

#### 结果
![path-sum-three-1](/images/leetcode/path-sum-three-1.png)


### 使用`Prefix Sum`
参考这个帖子：<https://discuss.leetcode.com/topic/64526/17-ms-o-n-java-prefix-sum-method/16?page=1>

用一个`Map`记录到从根节点，到当前节点所有`Prefix Sum`的值，比如有树，
```
         10
        /  \
       5   -3
      / \    \
     3   2   11
    / \   \
   3  -2   1
```
到第一个`5`节点为止，`Map`中应该有3个记录，`Map`的键值对中，`Key`是从根节点开始到目前节点的累加总和，`Value`节点是产生这个总和的次数（路径总数）。
```
(0,1)   // 初始条件
(10,1)  // 根节点: 0 + 10
(15,1)  // 10 + 5
```
这样做有个好处，比如遇到第1个`3`节点，累加和上升到`18`。这时候只要先减去目标值，比如说`8`，
然后和所有Map中的历史累加和作比较，
```
target = 8
current sum = 18
18 - 8 = 10
然后检查之前的Map中总和为10的路径有几条：结果是1条

这就代表：从根节点开始到第一个3节点的路径上，有1条子路径的总和等于目标值8.

通过观察可以发现这条路径就是：5 + 3 = 8
```

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
class Solution {
    public int pathSum(TreeNode root, int sum) {
        if (root == null) { return 0; }
        Map<Integer,Integer> map = new HashMap<>();
        map.put(0,1);
        return specialDFS(root,0,sum,map);
    }
    private int specialDFS(TreeNode root, int sum, int target, Map<Integer,Integer> map) {
        if (root == null) { return 0; }
        sum += root.val;
        int pathFromRootNum = map.getOrDefault(sum - target,0);
        map.put(sum,map.getOrDefault(sum,0)+1);
        int res =   pathFromRootNum +
                    specialDFS(root.left,sum,target,map) +
                    specialDFS(root.right,sum,target,map);
        map.put(sum,map.getOrDefault(sum,0)-1);
        return res;
    }
}
```

#### 结果
![path-sum-three-2](/images/leetcode/path-sum-three-2.png)

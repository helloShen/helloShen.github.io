---
layout: post
title: "Leetcode - Algorithm - House Robber Three "
date: 2017-10-09 19:04:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
The thief has found himself a new place for his thievery again. There is only one entrance to this area, called the "root." Besides the root, each house has one and only one parent house. After a tour, the smart thief realized that "all houses in this place forms a binary tree". It will automatically contact the police if two directly-linked houses were broken into on the same night.

Determine the maximum amount of money the thief can rob tonight without alerting the police.

Example 1:
```
     3
    / \
   2   3
    \   \
     3   1
```
Maximum amount of money the thief can `rob = 3 + 3 + 1 = 7`.
Example 2:
```
     3
    / \
   4   5
  / \   \
 1   3   1
```
Maximum amount of money the thief can `rob = 4 + 5 = 9`.

### 非动态规划的递归
递归逻辑如下：
> T(n) = MAX( 自己 + 孙代最优解之和， 子代最优解之和)

缺点就是重复解决了很多子问题。

#### 代码
```java
class Solution {
    public int rob(TreeNode root) {
        if (root == null) { return 0; }
        int l = 0,r = 0,ll = 0,lr = 0,rl = 0,rr = 0;
        if (root.left != null) {
            l = rob(root.left);
            ll = rob(root.left.left);
            lr = rob(root.left.right);
        }
        if (root.right != null) {
            r = rob(root.right);
            rl = rob(root.right.left);
            rr = rob(root.right.right);
        }
        int takeSon = l + r;
        int takeGrandSon = root.val + ll + lr + rl + rr;
        return Math.max(takeSon,takeGrandSon);
    }
}
```

#### 结果
![house-robber-three-1](/images/leetcode/house-robber-three-1.png)


### 动态规划避免重复解决子问题
把树的信息转换到一个数组中，然后以这个数组为备忘录，自底向上更新每一层子问题的解。缺点是需要使用一个较大的额外空间。

#### 代码
```java
class Solution {

    private static int[] houses = new int[0];

    public int rob(TreeNode root) {
        init(root);
        treeToArray(root,0);
        for (int i = houses.length - 1; i >= 0; i--) {
            int l = i * 2 + 1, r = l + 1;
            if (r < houses.length) {            // has son
                int ll = l * 2 + 1, lr = ll + 1;
                int rl = r * 2 + 1, rr = rl + 1;
                if (rr < houses.length) {       // has grand son
                    int takeGrandSon = houses[i] + houses[ll] + houses[lr] + houses[rl] + houses[rr];
                    int takeSon = houses[l] + houses[r];
                    houses[i] = Math.max(takeGrandSon,takeSon);
                } else {
                    houses[i] = Math.max(houses[i], houses[l] + houses[r]);
                }
            }
        }
        return houses[0];
    }
    private void treeToArray(TreeNode root, int offset) {
        if (root == null) { return; }
        houses[offset] = root.val;
        treeToArray(root.left,offset * 2 + 1);
        treeToArray(root.right,offset * 2 + 2);
    }
    private void init(TreeNode root) {
        houses = new int[(int)(Math.pow(2,depth(root,0)+1)-1)];
    }
    private int depth(TreeNode root, int depth) {
        if (root == null) { return 0; }
        return Math.max(depth,Math.max(depth(root.left,depth+1),depth(root.right,depth+1)));
    }
}
```

#### 结果
![house-robber-three-2](/images/leetcode/house-robber-three-2.png)


### 两种方法可以避免使用额外空间

#### 第一种：直接在原来的树上修改
这种做法破坏了原始数据，不推荐。
```java
class Solution {
    public int rob(TreeNode root) {
        if (root == null) { return 0; }
        int takeSon = 0, takeGrandSon = root.val;
        if (root.left != null) {
            rob(root.left);
            takeSon += root.left.val;
            if (root.left.left != null) { takeGrandSon += root.left.left.val; }
            if (root.left.right != null) { takeGrandSon += root.left.right.val; }
        }
        if (root.right != null) {
            rob(root.right);
            takeSon += root.right.val;
            if (root.right.left != null) { takeGrandSon += root.right.left.val; }
            if (root.right.right != null) { takeGrandSon += root.right.right.val; }
        }
        root.val = Math.max(takeSon,takeGrandSon);
        return root.val;
    }
}
```

#### 第二种：每次递归都返回`[偷这家最优解，不偷这家最优解]`的数组
推荐这种方法。也显示了这个问题动态规划背后的根本逻辑：
> 每个子问题都是一道简单的选择题：偷这家，或者不偷这家。所以有两种最优解：偷这一家的最优解，不偷这一家的最优解。

```java
class Solution {
    public int rob(TreeNode root) {
        int[] res = dp(root);
        return Math.max(res[0],res[1]);
    }
    private int[] dp(TreeNode root) {
        /**
         * res[0]: take son
         * res[1]: take itself and grand son
         */
        int[] res = new int[2];
        if (root == null) { return res; }
        int[] left = dp(root.left);
        int[] right = dp(root.right);
        res[0] = root.val + left[1] + right [1];
        res[1] = Math.max(left[0],left[1]) + Math.max(right[0],right[1]);
        return res;
    }
}
```

#### 结果
![house-robber-three-3](/images/leetcode/house-robber-three-3.png)

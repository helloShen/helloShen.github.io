---
layout: post
title: "Leetcode - Algorithm - Verify Preorder Serialization Of A Binary Tree "
date: 2018-08-10 15:37:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","binary tree"]
level: "medium"
description: >
---

### 题目

One way to serialize a binary tree is to use pre-order traversal. When we encounter a non-null node, we record the node's value. If it is a null node, we record using a sentinel value such as `#`.
```
     _9_
    /   \
   3     2
  / \   / \
 4   1  #  6
/ \ / \   / \
# # # #   # #
```
For example, the above binary tree can be serialized to the string `9,3,4,#,#,1,#,#,2,#,6,#,#`, where # represents a null node.

Given a string of comma separated values, verify whether it is a correct preorder traversal serialization of a binary tree. Find an algorithm without reconstructing the tree.

Each comma separated value in the string must be either an integer or a character `#` representing null pointer.

You may assume that the input format is always valid, for example it could never contain two consecutive commas such as `1,,3`.

Example 1:
```
Input: "9,3,4,#,#,1,#,#,2,#,6,#,#"
Output: true
```
Example 2:
```
Input: "1,#"
Output: false
```
Example 3:
```
Input: "9,#,#,1"
Output: false
```

### 利用Stack剪枝
> 如果一个节点左右两个子节点都为空，这一整棵子树就算到头了。把这棵子树剪掉，用`null`空节点代替。

![verify-preorder-serialization-of-a-binary-tree-a](/images/leetcode/verify-preorder-serialization-of-a-binary-tree-a.png)

到最后，如果是一棵合法的二叉树，应该正好遍历完整个`String`，而且`Stack`里只留下一个为`null`空的根节点。

#### 代码
```java
class Solution {
    /**
     * 利用一个Stack给树剪枝
     * 如果是一棵合法的二叉树：
     * 则当遍历完数组，Stack正好被剪空（只留一个为空的根节点）
     * Stack提前被剪空也不行
     */
    public boolean isValidSerialization(String preorder) {
        if (preorder == null || preorder.length() == 0) { return false; }
        //两个##抵消父节点
        Deque<Character> stack = new LinkedList<>();
        int p = 0;
        while (p < preorder.length()) {
            char c = preorder.charAt(p);
            stack.push(c);
            if (c == '#') {
                offset(stack);
            }
            // 找到下一个逗号","
            while (p < preorder.length() && preorder.charAt(p) != ',') { p++; }
            p++;
            //如果根节点也被抵消了，就结束
            if (stack.size() == 1 && stack.peek() == '#') { break; }
        }
        return p == preorder.length()+1 && stack.size() == 1 && stack.peek() == '#';
    }
    /**
     * 模拟树的剪枝：如果某节点左右两个节点都为空，说明这棵子树都探过了
     * 就把整棵子树都剪掉（删除左右两个空节点，再把本节点设为空）
     * 如果头两个元素是'#'，就删掉头3个元素，然后再压入一个'#'
     */
    private void offset(Deque<Character> stack) {
        if (stack == null || stack.size() < 3) { return; }
        Character first = stack.pop();
        Character second = stack.pop();
        if (first == '#' && second == '#') {
            Character c = stack.pop();
            stack.push('#');
            offset(stack);
        } else {
            stack.push(second);
            stack.push(first);
        }
    }
}
```

#### 结果
![verify-preorder-serialization-of-a-binary-tree-1](/images/leetcode/verify-preorder-serialization-of-a-binary-tree-1.png)


### 用数组模拟一个Stack来提高效率

#### 代码
```java
class Solution {
    /**
     * 优化Solution1，用一个char[]数组模拟Stack
     * 利用一个Stack给树剪枝
     * 如果是一棵合法的二叉树：
     * 则当遍历完数组，Stack正好被剪空（只留一个为空的根节点）
     * Stack提前被剪空也不行
     */
    //模拟Stack的数组
    private char[] stack;
    private int stackP;
    public boolean isValidSerialization(String preorder) {
        if (preorder == null || preorder.length() == 0) { return false; }
        //数组模拟Stack
        stack = new char[preorder.length()];
        stackP = 0;
        //两个##抵消父节点
        int p = 0;
        while (p < preorder.length()) {
            char c = preorder.charAt(p);
            stack[stackP++] = c;
            if (c == '#') {
                offset();
            }
            // 找到下一个逗号","
            while (p < preorder.length() && preorder.charAt(p) != ',') { p++; }
            p++;
            //当遍历完数组，Stack正好被剪空（只留一个为空的根节点）
            //Stack提前被剪空也不行
            if (stackP == 1 && stack[0] == '#') { break; }
        }
        return p == preorder.length()+1 && stackP == 1 && stack[0] == '#';
    }
    /**
     * 模拟树的剪枝：如果某节点左右两个节点都为空，说明这棵子树都探过了
     * 就把整棵子树都剪掉（删除左右两个空节点，再把本节点设为空）
     * 如果头两个元素是'#'，就删掉头3个元素，然后再压入一个'#'
     */
    private void offset() {
        if (stackP >= 3 && stack[stackP-1] == '#' && stack[stackP-2] == '#') {
            stack[stackP-3] = '#';
            stackP -= 2;
            offset();
        }
    }
}
```

#### 结果
![verify-preorder-serialization-of-a-binary-tree-2](/images/leetcode/verify-preorder-serialization-of-a-binary-tree-2.png)

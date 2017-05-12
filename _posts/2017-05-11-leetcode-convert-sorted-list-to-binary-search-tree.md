---
layout: post
title: "Leetcode - Algorithm - Convert Sorted List To Binary Search Tree "
date: 2017-05-11 20:56:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","linked list"]
level: "medium"
description: >
---

### 题目
Given a singly linked list where elements are sorted in ascending order, convert it to a height balanced BST.

### 把`ListNode`转化成`int[]`再做，复杂度$$O(n)$$
既然`ListNode`单向链表随机访问下标不方便，就把`ListNode`转换成`int[]`再做。关键是复杂度还是`O(n)`。当然这题本来的用意肯定不是让我们这么干。

#### 代码
```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
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
    public TreeNode sortedListToBST(ListNode head) {
        int[] array = listToArray(head);
        return sortedArrayToBST(array);
    }
    public int[] listToArray(ListNode head) {
        ListNode cur = head;
        int size = 0;
        while (cur != null) { size++; cur = cur.next; }
        int[] array = new int[size];
        cur = head;
        for (int i = 0; i < size; i++) {
            if (cur != null) {
                array[i] = cur.val;
                cur = cur.next;
            } else {
                break;
            }
        }
        return array;
    }
    public TreeNode sortedArrayToBST(int[] nums) {
        return recursion(nums,0,nums.length-1);
    }
    public TreeNode recursion(int[] nums, int lo, int hi) {
        if (lo > hi) { return null; }
        int mid = lo + (hi - lo) / 2; // 下位中位数
        TreeNode root = new TreeNode(nums[mid]);
        root.left = recursion(nums,lo,mid-1);
        root.right = recursion(nums,mid+1,hi);
        return root;
    }
}
```

#### 结果
![convert-sorted-list-to-binary-search-tree-1](/images/leetcode/convert-sorted-list-to-binary-search-tree-1.png)


### 直接在`ListNode`上操作，分治递归，$$O(n)$$
先计算整个链表的长度，后面才好取中位数。每次递归，先找到中点，然后再以开头点和中点的下一个点为端点递归下去。比如下面这个链表，长度为`13`。
```
1->2->3->4->5->6->7->8->9->10->11->12->13
```
先取中点`6`，作为根节点。然后再分别以`1`和`7`作为左右子树的根节点，递归下去。


#### 代码
```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
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
    public TreeNode sortedListToBST(ListNode head) {
        int size = size(head);
        return recursion(head,size);
    }
    public TreeNode recursion(ListNode head, int size) {
        if (size == 0) { return null; }
        int half = (size - 1) / 2;
        ListNode cur = head;
        for (int i = 0; i < half; i++) { cur = cur.next; }
        TreeNode root = new TreeNode(cur.val);
        root.left = recursion(head,half);
        root.right = recursion(cur.next,size-half-1);
        return root;
    }
    public int size(ListNode head) {
        ListNode cur = head;
        int count = 0;
        while (cur != null) {
            count++; cur = cur.next;
        }
        return count;
    }
}
```

#### 结果
![convert-sorted-list-to-binary-search-tree-2](/images/leetcode/convert-sorted-list-to-binary-search-tree-2.png)

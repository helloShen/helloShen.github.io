---
layout: post
title: "Leetcode - Algorithm - Binary Tree Paths "
date: 2017-07-14 11:15:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","depth first search"]
level: "easy"
description: >
---

### 题目
Given a binary tree, return all root-to-leaf paths.

For example, given the following binary tree:
```
   1
 /   \
2     3
 \
  5
```
All root-to-leaf paths are:
```
["1->2->5", "1->3"]
```

### 基本思路
遍历二叉树，没什么花样。递归DFS最快。

### 递归DFS
* time: $$O(n)$$
* space: $$0$$

#### Java代码
```java
/** dfs */
public class Solution {
    private List<String> result = new ArrayList<>();
    public List<String> binaryTreePaths(TreeNode root) {
        TreeNode dummy = new TreeNode(0);
        dummy.right = root;
        dfs(dummy,"");
        return result;
    }
    private void dfs(TreeNode root, String path) {
        if (root.left == null && root.right == null) {
            if (!path.isEmpty()) { result.add(path.substring(2)); } return;
        }
        if (root.left != null) { dfs(root.left,path + "->" + root.left.val); }
        if (root.right != null) { dfs(root.right,path + "->" + root.right.val); }
    }
}
```

#### 结果
![binary-tree-paths-1](/images/leetcode/binary-tree-paths-1.png)

#### C代码
C语言一看就是单身，没人照顾他，什么都得自己来。
```java
/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     struct TreeNode *left;
 *     struct TreeNode *right;
 * };
 */
 static char** binaryTreePaths(struct TreeNode *, int*);
 static char **dfs(struct TreeNode *, const char *, int *);
 static char *getlocalpath(int);
 static char *appendpath(const char *, const char *);
 static char **mergelist(char **, int, char **, int);

/**
 * Return an array of size *returnSize.
 * Note: The returned array must be malloced, assume caller calls free().
 */
char** binaryTreePaths(struct TreeNode* root, int* returnSize) {
    char path[] = "";
    return dfs(root,path,returnSize);
}
/**
 * [backtracking description]
 * @param  root       [pointer to the TreeNode structure]
 * @param  path       [allocated char array on heap]
 * @param  returnSize [length of returned string list]
 * @return            [address of first element in the return string list. memory allocated in heap.]
 */
static char **dfs(struct TreeNode *root, const char *path, int *returnSize) {
    char **result = (char **)malloc(sizeof(char *));
    result = 0; *returnSize = 0; // null pointer
    // base case
    if (!root) { return result; } // return null pointer
    // append path in heap
    char *localpath = getlocalpath(root->val);
    char *newpath = appendpath(path,localpath);
    free(localpath);
    // recursion
    int leftlen = 0;
    char **left = dfs(root->left,newpath,&leftlen);
    int rightlen = 0;
    char **right = dfs(root->right,newpath,&rightlen);
    result = mergelist(left,leftlen,right,rightlen);
    *returnSize = leftlen + rightlen;
    free(left); free(right);
    // add current path into the result list, if two returned list are both null.
    if (!*returnSize) {
        result = (char **)malloc(sizeof(char *));
        int len = strlen(newpath);
        char *pathtowrite = (char *)malloc((len-1) * sizeof(char)); //allocate space on heap for path string
        strcpy(pathtowrite,&newpath[2]);
        pathtowrite[len-2] = '\0';
        result[0] = pathtowrite;
        *returnSize = 1;
    }
    free(newpath);
    return result;
}
/**
 * return the allocated char pointer in heap
 * ex: given 10, return "->10", with null "\0" at the end
 */
static char *getlocalpath(int n) {
    char *path = (char *)malloc(15 * sizeof(char));
    strcpy(path,"->"); // "\0" at the end
    snprintf(&path[2],13,"%d",n); // "\0" at the end
    return path;
}
/**
 * append the local path to the end of inherit path.
 * return an allocated char array in heap.
 * do not free() two given paths.
 */
static char *appendpath(const char *orig, const char *toappend) {
    int newlen = strlen(orig) + strlen(toappend) + 1;
    char *newpath = (char *)malloc(newlen * sizeof(char));
    strcpy(newpath,orig);
    strcat(newpath,toappend);
    return newpath;
}
/**
 * merge two string list "char **"
 * return allocated string list in heap, return null pointer if total len is 0.
 * do not free() two given string list
 */
static char **mergelist(char **list1, int len1, char **list2, int len2) {
    char **result = (char **)malloc(sizeof(char *));
    int sum = len1 + len2;
    if (!sum) { return result; }
    result = (char **)realloc(result,sum * sizeof(char *));
    if (len1) {
        for (int i = 0; i < len1; i++) {
            result[i] = list1[i];
        }
    }
    if (len2) {
        for (int i = 0; i < len2; i++) {
            result[len1+i] = list2[i];
        }
    }
    return result;
}
```

#### 结果
![binary-tree-paths-2](/images/leetcode/binary-tree-paths-2.png)

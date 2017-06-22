---
layout: post
title: "Leetcode - Algorithm - Implement Trie "
date: 2017-06-21 16:22:38
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","binary search tree","design","trie"]
level: "medium"
description: >
---

### 主要收获一 - 字符串搜索的思路
> 优先考虑 **树**。不管是 **Binary Search Tree**，还是 **Prefix Tree**。

### 主要收获二 — Prefix Tree
以后处理搜索问题，除了传统的二叉树，又多了一种思路 `Prefix Tree`。 既保留了二叉树 $$O(\log_{}{n})$$ 的效率，又最大限度地减少了信息冗余。在储存相同信息量的前提下，`Prefix Tree`尽可能重复利用了每一个字符。
```
words: "aaa","aab","abc"
        a
       / \
      a   b
     / \   \
    a   b   c
```


### 题目
Implement a trie with `insert`, `search`, and `startsWith` methods.

Note:
You may assume that all inputs are consist of lowercase letters `a-z`.

### 基本思路
这个问题可能的方案很多，不要急于动手，先估算一下复杂度，可以排除很多不理想的方案。

首先，遇到查询，优先想到的肯定是`HashMap`，可以在 $$O(1)$$ 的时间内查询元素。但缺点是：因为元素没有排过序，所以 `startsWith()`函数无法划定范围进行推断，**只能 $$O(n)$$ 一个个排查**。
* `insert()`: $$O(1)$$
* `search()`: $$O(1)$$
* `startsWith()`: $$O(n)$$

然后，可以考虑`Array`。因为数组有 **二分查找**，所以就算要维护一个有序数组， `insert()`和`search()`都可以在 $$O(\log_{}{n})$$ 时间内找到位置。但数组的致命缺点是：**`insert()`动作需要拷贝后面整个数组**。复杂度是 $$O(n)$$。最后，因为是一个有序数组，`startsWith()`也可以用二分查找可以用圈定一个范围的方法，确定有没有以特定字符串为前缀的单词。
* `insert()`: $$O(n)$$
* `search()`: $$O(\log_{}{n})$$
* `startsWith()`: $$O(\log_{}{n})$$

这种时候，**二叉搜索树** 是比较好的方案。它结合了 **数组能二分查找**，以及 **链表插入删除快** 的有点。 `insert()`和`search()`以及`startsWith()`三个函数都能在 $$O(\log_{}{n})$$ 时间内完成。
* `insert()`: $$O(\log_{}{n})$$
* `search()`: $$O(\log_{}{n})$$
* `startsWith()`: $$O(\log_{}{n})$$

### 解法一：基于二叉搜索树的`Trie`
这里先要搞清楚，`String`比较大小的机制。下面这段代码是 **Comparator** `CASE_INSENSITIVE_ORDER`中`compare()`方法的实现，
```java
public int compare(String s1, String s2) {
    int n1 = s1.length();
    int n2 = s2.length();
    int min = Math.min(n1, n2);
    for (int i = 0; i < min; i++) {
        char c1 = s1.charAt(i);
        char c2 = s2.charAt(i);
        if (c1 != c2) {
            c1 = Character.toUpperCase(c1);
            c2 = Character.toUpperCase(c2);
            if (c1 != c2) {
                c1 = Character.toLowerCase(c1);
                c2 = Character.toLowerCase(c2);
                if (c1 != c2) {
                    // No overflow because of numeric promotion
                    return c1 - c2;
                }
            }
        }
    }
    return n1 - n2;
}
```

总结起来就是两条，
1. 先比较共有长度部分。比如`aabb`和`aaa`，共有长度为`3`。在共有长度内就分出了胜负。
```
aabb
aaa
  |
共有长度 = 3
```
2. 如果，共有长度内分不出胜负，那就是更长的那个字符串更大。比如`aaabbb`和`aaa`，共有长度`3`之内，没有分出大小，所以较长的`aaabbb`更大。
```
aaabbb
aaa
  |
共有长度 = 3
```

基于以上两条`String`比较大小的规则，`startsWith()`函数，划定范围的两个标签`begin`和`end`为（以`aaa`开头为例）：
* `begin` = `aaa`
* `end` = `aab`

夹在`aaa`和`aab`中间的所有字符串都是以`aaa`开头的。


#### 代码
```java
public class Trie {
        private static class TreeNode {
            private String val;
            private TreeNode left;
            private TreeNode right;
            private TreeNode(String s) {
                val = s;
            }
        }
        private TreeNode dummy;
        /** Initialize your data structure here. */
        public Trie() {
            dummy = new TreeNode("");
        }

        /** Inserts a word into the trie. */
        public void insert(String word) {
            TreeNode pre = dummy, cur = dummy.right;
            boolean goLeft = false;
            while (cur != null) {
                int diff = word.compareTo(cur.val);
                pre = cur;
                if (diff < 0) { // go left
                    cur = cur.left;
                    goLeft = true;
                } else if (diff > 0) { // go right
                    cur = cur.right;
                    goLeft = false;
                } else { // word == cur.val (word already exist)
                    return;
                }
            }
            TreeNode newNode = new TreeNode(word);
            if (goLeft) {
                pre.left = newNode;
            } else {
                pre.right = newNode;
            }
        }

        /** Returns if the word is in the trie. */
        public boolean search(String word) {
            TreeNode cur = dummy.right;
            while (cur != null) {
                int diff = cur.val.compareTo(word);
                if (diff < 0) { // go right
                    cur = cur.right;
                } else if (diff > 0) { // go left
                    cur = cur.left;
                } else { // found target
                    return true;
                }
            }
            return false;
        }

        /** Returns if there is any word in the trie that starts with the given prefix. */
        public boolean startsWith(String prefix) {
            String begin = firstLargerEqual(prefix);
            String nextPrefix = nextString(prefix);
            String end = firstLargerEqual(nextPrefix);
            return ((begin == null && end == null) || begin.equals(end))? false : true;
        }

        /** The last character add one */
        private String nextString(String word) {
            char[] letters = word.toCharArray();
            int tail = letters.length;
            if (tail == 0) { return "a"; }
            for (int i = letters.length-1; i >= 0; i--) {
                if (letters[i] < 'z') {
                    letters[i] = (char)(letters[i] + 1);
                    break;
                } else {
                    tail--;
                }
            }
            return (tail == 0)? "{" : new String(Arrays.copyOfRange(letters,0,tail));   // "{"是z之后的第一个字符
        }

        /** Returns the first element that >= the given String
         *  Returns null if not found.
         */
        private String firstLargerEqual(String word) {
            String lastLarger = null;
            TreeNode pre = dummy, cur = dummy.right;
            while (cur != null) {
                int diff = cur.val.compareTo(word);
                if (diff > 0) { // go left
                    lastLarger = cur.val;
                    cur = cur.left;
                } else if (diff < 0) { // go right
                    cur = cur.right;
                } else { // found word
                    return word;
                }
            }
            return lastLarger;
        }
}
```

#### 结果
![implement-trie-1](/images/leetcode/implement-trie-1.png)


### 解法二：基于`Prefix Tree`
同样是基于 **树**。但每个节点不用储存完整的单词，而是一个字母。比如储存三个单词`aaa`,`aab`和`abc`。
```
words: "aaa","aab","abc"
        a
       / \
      a   b
     / \   \
    a   b   c
```

三个方法还是都保持 $$O(\log_{}{n})$$ 的时间复杂度，
* `insert()`: $$O(\log_{}{n})$$
* `search()`: $$O(\log_{}{n})$$
* `startsWith()`: $$O(\log_{}{n})$$

而且，`Prefix Tree`最大的好处是 **节省空间**。 尽可能重复利用了每一个字符。

#### 代码
```java
public class Trie {
    private static class TreeNode {
        private char val;
        private boolean isWord;
        private TreeNode[] children = new TreeNode[26];
        private TreeNode(char c) {
            val = c;
        }
    }
    private TreeNode root = new TreeNode('\u0000');
    /** Initialize your data structure here. */
    public Trie() { }

    /** Inserts a word into the trie. */
    public void insert(String word) {
        TreeNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int offset = c - 'a';
            TreeNode node = cur.children[offset];
            if (node == null) {
                node = new TreeNode(c);
                cur.children[offset] = node;
            }
            cur = node;
        }
        cur.isWord = true;
    }

    /** Returns if the word is in the trie. */
    public boolean search(String word) {
        TreeNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            int offset = word.charAt(i) - 'a';
            if (cur.children[offset] == null) {
                return false;
            } else {
                cur = cur.children[offset];
            }
        }
        return cur.isWord;
    }

    /** Returns if there is any word in the trie that starts with the given prefix. */
    public boolean startsWith(String prefix) {
        TreeNode cur = root;
        for (int i = 0; i < prefix.length(); i++) {
            int offset = prefix.charAt(i) - 'a';
            if (cur.children[offset] == null) {
                return false;
            } else {
                cur = cur.children[offset];
            }
        }
        return true;
    }

}
```

#### 结果
![implement-trie-2](/images/leetcode/implement-trie-2.png)

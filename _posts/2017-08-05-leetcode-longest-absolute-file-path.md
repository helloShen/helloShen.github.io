---
layout: post
title: "Leetcode - Algorithm - Longest Absolute File Path "
date: 2017-08-05 15:11:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","array"]
level: "medium"
description: >
---

### 题目
Suppose we abstract our file system by a string in the following manner:

The string `dir\n\tsubdir1\n\tsubdir2\n\t\tfile.ext` represents:
```
dir
    subdir1
    subdir2
        file.ext
```
The directory dir contains an empty sub-directory subdir1 and a sub-directory subdir2 containing a file `file.ext.`

The string `dir\n\tsubdir1\n\t\tfile1.ext\n\t\tsubsubdir1\n\tsubdir2\n\t\tsubsubdir2\n\t\t\tfile2.ext` represents:
```
dir
    subdir1
        file1.ext
        subsubdir1
    subdir2
        subsubdir2
            file2.ext
```
The directory dir contains two sub-directories `subdir1` and `subdir2`. `subdir1` contains a file `file1.ext` and an empty second-level sub-directory `subsubdir1`. `subdir2` contains a second-level sub-directory `subsubdir2` containing a file `file2.ext`.

We are interested in finding the longest (number of characters) absolute path to a file within our file system. For example, in the second example above, the longest absolute path is `dir/subdir2/subsubdir2/file2.ext`, and its length is `32` (not including the double quotes).

Given a string representing the file system in the above format, return the length of the longest absolute path to file in the abstracted file system. If there is no file in the system, return `0`.

Note:
The name of a file contains at least a . and an extension.
The name of a directory or sub-directory will not contain a ..
Time complexity required: $$O(n)$$ where n is the size of the input string.

Notice that `a/aa/aaa/file1.txt` is not the longest file path, if there is another path `aaaaaaaaaaaaaaaaaaaaa/sth.png`.

### 用`Stack`记录路径历史
封装了很多小工具函数。

#### 代码
```java
public class Solution {
    // simble
    private final char SPLITTER = '\n';
    private final char TAB = '\t';
    // globle path
    private String path = "";
    private int len = 0;
    private int index = 0;
    // init the path
    private void init(String path) {
        this.path = path;
        len = path.length();
        index = 0;
    }
    // main access
    public int lengthLongestPath(String input) {
        int max = 0;
        init(input); // init globle values
        if (len == 0) { return max; } // defance
        Deque<String> stack = new LinkedList<>();
        String root = getSubDir(stack); // no slash before the root
        if (root.isEmpty()) { return max; } // defance
        stack.offerFirst(root);
        while (canGoDeeper()) {
            int depth = getDepth(); // depth begin from 0
            if (depth < stack.size()) {
                max = Math.max(max,countPathLength(stack)); // find new leaf, update max length
                while (depth < stack.size()) { stack.pollFirst(); }
            }
            stack.offerFirst(getSubDir(stack));
        }
        max = Math.max(max,countPathLength(stack)); // last possible leaf, update max length
        return max;
    }
    /* check if the next character is "\n" or "\t" (will not move the index pointer) */
    private boolean canGoDeeper() {
        return isSplitter() || isTab();
    }
    /* check if the next character is "\n" (will not move the index pointer) */
    private boolean isSplitter() {
        return index < len && path.charAt(index) == SPLITTER;
    }
    /* check if the next character is "\t" (will not move the index pointer) */
    private boolean isTab() {
        return index < len && path.charAt(index) == TAB;
    }
    /* extract the sub dir in the path from the given index. have to check if this is will be the root. (will move index pointer) */
    private String getSubDir(Deque<String> stack) {
        StringBuilder sb = new StringBuilder();
        if (stack.size() > 0) { sb.append("\\"); } // not root
        while (index < len && !isSplitter()) {
            sb.append(path.charAt(index++));
        }
        return sb.toString();
    }
    /* count the number of "\t" (will move the index pointer to the first character after "\t") */
    private int getDepth() {
        skipSplitter();
        int count = 0;
        while (isTab()) { ++index; ++count; }
        return count;
    }
    /* skip "\n" (will move the index pointer to the first character after "\n") */
    private void skipSplitter() {
        if (isSplitter()) { ++index; }
    }
    /* count the length of the current path in stack */
    private int countPathLength(Deque<String> stack) {
        int sum = 0;
        if (stack.isEmpty() || !isFile(stack.peekFirst())) { return sum; } // do not count the length if it's not a file
        for (String s : stack) {
            sum += s.length();
        }
        return sum;
    }
    /* check if the first element in the stack is a file */
    private boolean isFile(String s) {
        return s.contains(".");
    }
}
```

#### 结果
![longest-absolute-file-path-1](/images/leetcode/longest-absolute-file-path-1.png)


### 用数组模拟`Stack`的行为

#### 代码
```java
public class Solution {
    // constant
    private final char SPLITTER = '\n';
    private final char TAB = '\t';
    private final String POINT = ".";
    // environment
    private char[] path = new char[0];          // input path
    private int len = 0;                        // length of input path
    private int p = 0;                          // pointer to the path
    // init enviroment
    private void init(String s) {
        path = s.toCharArray();             // input path
        len = s.length();                   // length of input path
        p = 0;                              // pointer
    }
    // assert: the input path should be valid path.
    public int lengthLongestPath(String input) {
        // defence
        if (input.length() == 0) { return 0; }
        // init environment
        init(input);
        // local variable
        int[] stack = new int[len];             // note length of substrings
        boolean[] isFile = new boolean[len];    // note if substrings in the stack are files
        int currDepth = -1;                     // root has depth 0
        int max = 0;                            // result
        // iterate sub path
        do {
            int depth = nextDepth();
            String subpath = nextSubPath();
            if (depth <= currDepth) {
                max = Math.max(max,collect(stack,isFile,currDepth));
                while (depth <= currDepth) { --currDepth; }
            }
            stack[++currDepth] = subpath.length();
            if (currDepth > 0) { ++stack[currDepth]; } // count slash
            isFile[currDepth] = isFile(subpath);
        } while (hasNext());
        max = Math.max(max,collect(stack,isFile,currDepth));
        return max;
    }
    /* have more sub path? if yes, move pointer to first '\t' */
    private boolean hasNext() {
        boolean res = (p < len) && path[p] == SPLITTER;
        if (res) { ++p; }
        return res;
    }
    /* return the depth of next dir/file. move pointer after the last '\t' */
    private int nextDepth() {
        int depth = 0;
        while (p < len && path[p] == TAB) { ++depth; ++p; }
        return depth;
    }
    /* return the next dir/file. move pointer to next '\n' */
    private String nextSubPath() {
        StringBuilder sb = new StringBuilder();
        while (p < len && path[p] != SPLITTER && path[p] != TAB) { sb.append(path[p++]); }
        return sb.toString();
    }
    /* check if the given string is the name of file. all file name contains "." */
    private boolean isFile(String s) {
        return s.contains(POINT);
    }
    /* sum up the length of sub path in current stack */
    private int collect(int[] stack, boolean[] isFile, int depth) {
        int len = 0;
        if (!isFile[depth]) { return 0; }
        for (int i = 0; i <= depth; i++) {
            len += stack[i];
        }
        return len;
    }
}
```

#### 结果
![longest-absolute-file-path-2](/images/leetcode/longest-absolute-file-path-2.png)


### 优化
能用库函数的就用库函数。尽量简化过程。代码如果足够简单，也就没必要独立出很多小的辅助函数。

主要做了下面4点优化：
1. 用`split()`函数切割字符串。
2. 用`lastIndexOf()`函数计算`\t`的数量，也就是depth。
3. stack数组里记录的不是每一层路径的长度，而是从根目录累计到这一层的总长度。
4. 当且仅当当前路径段是一个文件的时候，才更新最长路径记录。意味着不需要比较路径的深度，也不用记录每个路径是不是文件。

#### 代码
```java
public class Solution {
    public int lengthLongestPath(String input) {
        if (input.length() == 0) { return 0; }
        int max = 0;
        int[] stack = new int[input.length()+1];
        for (String s : input.split("\n")) {
            int depth = s.lastIndexOf('\t') + 2; // depth of root is 1
            int len = stack[depth-1] + (s.length() - depth + 1);
            stack[depth] = (depth > 1)? len+1 : len; // add slash if is not root
            if (s.contains(".")) { max = Math.max(max,stack[depth]); } // is file
        }
        return max;
    }
}
```

#### 结果
![longest-absolute-file-path-3](/images/leetcode/longest-absolute-file-path-3.png)

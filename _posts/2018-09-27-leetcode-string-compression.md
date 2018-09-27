---
layout: post
title: "Leetcode - Algorithm - String Compression "
date: 2018-09-27 15:20:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
Given an array of characters, compress it in-place.

The length after compression must always be smaller than or equal to the original array.

Every element of the array should be a character (not int) of length 1.

After you are done modifying the input array in-place, return the new length of the array.


Follow up:
* Could you solve it using only O(1) extra space?


Example 1:
```
Input:
["a","a","b","b","c","c","c"]

Output:
Return 6, and the first 6 characters of the input array should be: ["a","2","b","2","c","3"]

Explanation:
"aa" is replaced by "a2". "bb" is replaced by "b2". "ccc" is replaced by "c3".
```

Example 2:
```
Input:
["a"]

Output:
Return 1, and the first 1 characters of the input array should be: ["a"]

Explanation:
Nothing is replaced.
```

Example 3:
```
Input:
["a","b","b","b","b","b","b","b","b","b","b","b","b"]

Output:
Return 4, and the first 4 characters of the input array should be: ["a","b","1","2"].

Explanation:
Since the character "a" does not repeat, it is not compressed. "bbbbbbbbbbbb" is replaced by "b12".
Notice each digit has it's own entry in the array.
```

Note:
* All characters have an ASCII value in [35, 126].
* 1 <= len(chars) <= 1000.

### 用3个指针
一个指针`pen`用来写，另外两个`start`和`end`用来找到相同字符的边界。考虑下面数组，
```
[a, a, a, b, c, c, c, c, d, d]

等处理完"a"和"b", [start, end]的窗口找到"c"的长度，准备从pen的位置写入。
           start        end
             |           |
[a, 3, b, b, c, c, c, c, d, d]
          |
         pen
```

#### 代码
```java
class Solution {

    public int compress(char[] chars) {
        int pen = 0;
        int start = 0;
        while (start < chars.length) {
            for (int end = start + 1; end <= chars.length; end++) {
                if (end == chars.length || chars[end] != chars[start]) {
                    chars[pen++] = chars[start];
                    if (end - start > 1) {
                        String len = String.valueOf(end - start);
                        for (int i = 0; i < len.length() ; i++) {
                            chars[pen++] = len.charAt(i);
                        }
                    }
                    start = end;
                    break;
                }
            }
        }
        return pen;
    }

}
```

#### 结果
![string-compression-1](/images/leetcode/string-compression-1.png)

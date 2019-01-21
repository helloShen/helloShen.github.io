---
layout: post
title: "Leetcode - Algorithm - Read N Characters Given Read4 "
date: 2019-01-20 22:51:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "string"]
level: "easy"
description: >
---

### 题目
Given a file and assume that you can only read the file using a given method read4, implement a method to read n characters.

Method read4:
```
The API read4 reads 4 consecutive characters from the file, then writes those characters into the buffer array buf.

The return value is the number of actual characters read.

Note that read4() has its own file pointer, much like FILE *fp in C.

Definition of read4:

    Parameter:  char[] buf
    Returns:    int

Note: buf[] is destination not source, the results from read4 will be copied to buf[]
Below is a high level example of how read4 works:

File file("abcdefghijk"); // File is "abcdefghijk", initially file pointer (fp) points to 'a'
char[] buf = new char[4]; // Create buffer with enough space to store characters
read4(buf); // read4 returns 4. Now buf = "abcd", fp points to 'e'
read4(buf); // read4 returns 4. Now buf = "efgh", fp points to 'i'
read4(buf); // read4 returns 3. Now buf = "ijk", fp points to end of file
```

Method read:
```
By using the read4 method, implement the method read that reads n characters from the file and store it in the buffer array buf. Consider that you cannot manipulate the file directly.

The return value is the number of actual characters read.

Definition of read:

    Parameters:	char[] buf, int n
    Returns:	int

Note: buf[] is destination not source, you will need to write the results to buf[]
```

Example 1:
```
Input: file = "abc", n = 4
Output: 3
Explanation: After calling your read method, buf should contain "abc". We read a total of 3 characters from the file, so return 3. Note that "abc" is the file's content, not buf. buf is the destination buffer that you will have to write the results to.
```

Example 2:
```
Input: file = "abcde", n = 5
Output: 5
Explanation: After calling your read method, buf should contain "abcde". We read a total of 5 characters from the file, so return 5.
```

Example 3:
```
Input: file = "abcdABCD1234", n = 12
Output: 12
Explanation: After calling your read method, buf should contain "abcdABCD1234". We read a total of 12 characters from the file, so return 12.
```

Example 4:
```
Input: file = "leetcode", n = 5
Output: 5
Explanation: After calling your read method, buf should contain "leetc". We read a total of 5 characters from the file, so return 5.
```

Note:
* Consider that you cannot manipulate the file directly, the file is only accesible for read4 but not for read.
* The read function will only be called once for each test case.
* You may assume the destination buffer array, buf, is guaranteed to have enough space for storing n characters.

### 题目解释
这题题目描述不清楚。补充说明一下，假设我有`123456789`这个字符串，要求读取前`7`个字符。首先我需要一个长度为`4`的数组`localBuf`，
```
char[] localBuf = new char[4];
```
调用`read4()`的时候，把`localBuf`传递进去。`read4()`会依次将字符拷贝到`localBuf`里。
```
第一次：
localBuf = "1234", read4返回4

第二次：
localBuf = "5678"，read4返回4

第三次：
localBuf = "9"，read4返回1
```

但因为只需要前`7`个字符，所以不需要调用`3`次`read4()`。调用`2`次就够了。
```
第一次把localBuf里全部4个字符都拷贝到buf里， buf = "1234"

第二次把localBuf里前3个字符拷贝到buf里，buf = "1234567"
```

所以`read()`函数，不但要返回正确的长度，还要能将所有字符都拷贝到`buf`数组中。而这个`buf`和题目描述中的`buf`不是同一个数组。实际传递给`read4()`函数的可以理解为我们自己给出的`localBuf`数组。

#### 代码
```java
/**
 * The read4 API is defined in the parent class Reader4.
 *     int read4(char[] buf);
 */
public class Solution extends Reader4 {
    /**
     * @param buf Destination buffer
     * @param n   Number of characters to read
     * @return    The number of actual characters read
     */
    public int read(char[] buf, int n) {
        if (n <= 0) return 0;
        char[] localBuf = new char[4];
        int total = 0;
        while (true) {
            int localLen = read4(localBuf);
            if (localLen == 0) break;
            for (int i = 0; i < localLen; i++) {
                if (total == n) break;
                buf[total++] = localBuf[i];
            }
        }
        return total;
    }
}
```

#### 结果
![read-n-characters-given-read4-1](/images/leetcode/read-n-characters-given-read4-1.png)

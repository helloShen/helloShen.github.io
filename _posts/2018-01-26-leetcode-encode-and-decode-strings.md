---
layout: post
title: "Leetcode - Algorithm - Encode And Decode Strings "
date: 2018-01-26 19:59:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "medium"
description: >
---

### 题目
Design an algorithm to encode a list of strings to a string. The encoded string is then sent over the network and is decoded back to the original list of strings.

Machine 1 (sender) has the function:
```
string encode(vector<string> strs) {
  // ... your code
  return encoded_string;
}
```
Machine 2 (receiver) has the function:
```
vector<string> decode(string s) {
  //... your code
  return strs;
}
```
So Machine 1 does:
```
string encoded_string = encode(strs);
```
and Machine 2 does:
```
vector<string> strs2 = decode(encoded_string);
```
strs2 in Machine 2 should be the same as strs in Machine 1.

Implement the encode and decode methods.

Note:
* The string may contain any possible characters out of 256 valid ascii characters. Your algorithm should be generalized enough to work on any possible characters.
* Do not use class member/global/static variables to store states. Your encode and decode algorithms should be stateless.
* Do not rely on any library method such as eval or serialize methods. You should implement your own encode/decode algorithm.

### 方法1：用空格分割字符串
主要问题在于：怎么处理字符串中原有的空格？ 解决方法可以引入“转义字符”。
* 每个String用空格" "分割，空字符串""后面也用一个空格分割
* "\"是转义符
* "\ "表示字符串中的空格
* "\\"表示字符串中的转义符本身
* 如果原始字符串列表为空，返回空字符串""
* [Hello,World] -> "hello world "

#### 代码
```java
public class Codec {

        public String encode(List<String> strs) {
            char space = ' ';
            char trans = '\\';
            StringBuilder res = new StringBuilder();
            for (String str : strs) {
                char[] chars = str.toCharArray();
                for (char c : chars) {
                    if (c == space || c == trans) { res.append(trans); }
                    res.append(c);
                }
                res.append(space); // 用空格分割每个字符串
            }
            return res.toString();
        }
        public List<String> decode(String s) {
            List<String> res = new ArrayList<>();
            char space = ' ';
            char trans = '\\';
            char[] chars = s.toCharArray();
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (c == space) {
                    res.add(word.toString());
                    word.delete(0,word.length());
                } else if (c == trans) {
                    char next = chars[++i];
                    if (next == space) {
                        word.append(space);
                    } else if (next == trans) {
                        word.append(trans);
                    }
                } else {
                    word.append(c);
                }
            }
            return res;
        }

}

// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.decode(codec.encode(str                        s));
```

#### 结果
![encode-and-decode-strings-2](/images/leetcode/encode-and-decode-strings-2.png)


### 计算每个单词的长度
计算并记录下每个单词的长度，提示程序到后面多少个偏移量的位置去读取下一个单词。
```
[Hello,World] -> "5/Hello5/World"
```
注意：如果保证从头开始遍历字符串的话，即使单词中包含斜杠"/"，也不影响。

#### 代码
```java
public class Codec {

        public String encode(List<String> strs) {
            StringBuilder res = new StringBuilder();
            for (String str : strs) {
                res.append(String.valueOf(str.length()));
                res.append("/" + str);
            }
            return res.toString();
        }
        public List<String> decode(String s) {
            List<String> res = new ArrayList<>();
            int i = 0;
            while (i < s.length()) {
                int slash = s.indexOf("/",i);
                int offset = Integer.parseInt(s.substring(i,slash));
                res.add(s.substring(slash+1,slash+1+offset));
                i = slash + 1 + offset;
            }
            return res;
        }

}

// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.decode(codec.encode(strs));
```

#### 结果
![encode-and-decode-strings-1](/images/leetcode/encode-and-decode-strings-1.png)

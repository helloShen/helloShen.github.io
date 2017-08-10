---
layout: post
title: "Leetcode - Algorithm - Encode And Decode Tiny Url "
date: 2017-08-09 18:56:01
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["design"]
level: "medium"
description: >
---

### 题目
TinyURL is a URL shortening service where you enter a URL such as `https://leetcode.com/problems/design-tinyurl` and it returns a short URL such as `http://tinyurl.com/4e9iAk`.

Design the encode and decode methods for the TinyURL service. There is no restriction on how your encode/decode algorithm should work. You just need to ensure that a URL can be encoded to a tiny URL and the tiny URL can be decoded to the original URL.

### 主要思路
这题如果从信息压缩的思路考虑就错了。工业界的解决方案非常简单，**就是把长URL存起来，然后返回内部分配的一个ID。** 其他的一些优化也是针对实际使用过程中暴露出来的问题做优化。

### 直接返回自增序列号
最简单的做法其实就是这么简单。后台只是维护了一个自增计数器而已。然后把`id`和`url`的映射关系存起来，仅此而已。
```
http://tinyurl.com/1
http://tinyurl.com/2
http://tinyurl.com/3
... ...
```

#### 代码
```java
private Map<Integer,String> dic = new HashMap<>();
private int count = 0;
private final String PREFIX = "http://tinyurl.com/";

public String encode(String longUrl) {
    int id = ++count;
    dic.put(id,longUrl);
    return PREFIX + String.valueOf(id);
}
public String decode(String shortUrl) {
    int id = Integer.parseInt(shortUrl.replace(PREFIX,""));
    return dic.remove(id);
}
```

#### 结果
![encode-and-decode-tiny-url-1](/images/leetcode/encode-and-decode-tiny-url-1.png)


### 解法2
在进入工业级的实际应用后，`stefan`的文章，<https://discuss.leetcode.com/topic/81637/two-solutions-and-thoughts> 提出了至少4个可能的缺陷，
1. 相同的`URL`，每次请求也会被分配不同的`ID`。
2. 公司当然不希望`ID`暴露实际请求的数量。
3. 为了一个漂亮的`ID`，用户会故意多次提交请求。

解决方案也很简单，给一个像下面这样的随机码，就不会暴露请求数量，也不存在所谓的漂亮号码，因为都是随机的。
```
4e9iAk
```
至于相同的`URL`要给出相同的编码，只需要把用户提交过的`URL`全记下来就可以了。

#### 代码
```java
public class Codec {

        private Map<String,String> shortLong = new HashMap<>(); // short-long
        private Map<String,String> longShort = new HashMap<>(); // long-short
        private final String PREFIX = "http://tinyurl.com/";
        private final int LEN = 7;

        public String encode(String longUrl) {
            if (longShort.containsKey(longUrl)) { return longShort.get(longUrl); }
            String code = randomCode(LEN);
            while (shortLong.containsKey(code)) { code = randomCode(LEN); }
            String shortUrl = PREFIX + code;
            shortLong.put(shortUrl,longUrl);
            longShort.put(longUrl,shortUrl);
            return shortUrl;
        }
        public String decode(String shortUrl) {
            return shortLong.get(shortUrl);
        }

        private final char[] C = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6','7','8','9','0'};
        private final Random R = new Random();
        private String randomCode(int len) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append(C[R.nextInt(C.length)]);
            }
            return sb.toString();
        }

}
```

#### 结果
![encode-and-decode-tiny-url-2](/images/leetcode/encode-and-decode-tiny-url-2.png)

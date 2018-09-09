---
layout: post
title: "Use NIO to Create Recursive Directory"
date: 2018-09-09 16:26:39
author: "Wei SHEN"
categories: ["java"]
tags: ["io","nio","file"]
description: >
---

### 问题
用`java.io.File`的时候，创建文件之前，最好检查目录是否存在，如果不存在需要用`mkdir()`函数创建，以免到后面拿到`FileNotFoundException`。

但如果像下面这样，直接创建一个比较深的文件夹，如果从根上，比如`.../github/`文件夹就没有创建，`mkdir()`函数就会失败，因为默认只创建一层目录，也就是整个父级目录`/Users/Wei/github/leetcode/java/src/main/java/com/ciaoshen/`必须存在，才能在下面创建`.../leetcode`。
```java
import java.io.File;

public class CreateDirectoryExample {

    public static void main(String[] args) {
        File files = new File("/Users/Wei/github/leetcode/java/src/main/java/com/ciaoshen/leetcode/");
        if (!files.exists()) {
            if (files.mkdirs()) {
                System.out.println("Multiple directories are created!");
            } else {
                System.out.println("Failed to create multiple directories!");
            }
        }
    }

}
```

想要递归地创建整个路径上遇到的所有没有创建的文件夹，可以用`java.nio`，具体代码如下，
```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateDirectoryExample {
    public static void main(String[] args) {

        Path path = Paths.get("/Users/Wei/github/leetcode/java/src/main/java/com/ciaoshen/leetcode/");
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }
    }
}
```

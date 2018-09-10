---
layout: post
title: "Use NIO to Create Recursive Directory"
date: 2018-09-09 16:26:39
author: "Wei SHEN"
categories: ["java"]
tags: ["io","nio","file"]
description: >
---

### 为什么要递归创建多级路径？
`new FileWriter(File file)`构建字符流输出的时候，如果目标文件不存在会得到一个`IOException`。所以一般会预先检查目标文件或目标路径是否已经存在，若不存在则先创建这些路径，然后再构建FileWriter。这是比较稳妥的做法。遇上路径复杂的情况，经常需要创建一连串子文件夹。

### 【Java 6】用传统的`java.io.File`类实现
先用`java.io.File`类的`exists()`函数检查路径是否存在，再用`mkdirs()`函数创建完整的多级路径（注意，不是`mkdir()`）。
```java
import java.io.File;

public class RecursiveDirectoryJava6 {

    // path can be very long such as: "/Users/Wei/github/leetcode/java/src/main/java/com/ciaoshen/leetcode/"
    public recursiveDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("All recursive directories are created!");
            } else {
                System.out.println("Fail to create recursive directories!");
            }
        }
    }

}
```

### 【Java 7】可以用`java.nio`包实现
先用`java.nio.Paths`类的静态方法`get(String Path)`函数把字符串路径封装成一个`java.nio.Path`类对象。 然后用`java.nio.Files`类的静态方法`exists(Path path)`判断完整路径是否存在。如果不存在再调用另一个静态方法`createDirectories(Path path)`创建完整多级路径。
```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RecursiveDirectoryJava7 {

    // path can be very long such as: "/Users/Wei/github/leetcode/java/src/main/java/com/ciaoshen/leetcode/"
    public recursiveDirectory(String pathStr) {
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
```

总之逐渐少用Java 6的`File`体系，拥抱Java 7的`nio`体系是大势所趋。

### 配合递归创建多级路径，安全地拿到一个用`BufferedWriter`修饰过的`FileWriter`
比如用Java 7的`java.nio`包就可以这么写。
```java
Writer getFileWriter(String path) {
    Path directory = Paths.get(path.substring(0, path.lastIndexOf("/")));
    try {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        return Files.newBufferedWriter(Paths.get(path));
    } catch (IOException ioe) { // createDirectories() && newBufferedWriter()
        ioe.printStackTrace();
        return null;
    }
}
```
要创建一个很深的文件比如`/Users/Wei/github/leetcode/java/src/main/java/com/ciaoshen/leetcode/Solution.java`。先把最后的目标文件`Solution.java`切掉。用刚才的方法确保前面的路径被正确创建之后，才创建`BufferedWriter`。


### 递归删除某个路径下的所有子目录以及所有文件
创建子目录还算简单，删起来要更麻烦一点。Java 7的官方文档介绍了一种用`Files.walkFileTree()`函数配合`FileVisitor`来遍历整个文件夹子树，
```java
Path start = "~/directory/to/start";
Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException
    {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException e)
        throws IOException
    {
        if (e == null) {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        } else {
            // directory iteration failed
            throw e;
        }
    }
});
```

看上去有点麻烦，另一种绕过的方法就是自己写一个利用传统`File`类的`listFiles()`方法列出所有子目录，然后递归删除所有子目录的函数，
```java
/** recursively delete a directory and all its sub directories */
private void deleteRecursiveDirectory(File file) {
    File[] subs = file.listFiles();
    if (subs != null) {
        for (File sub : subs) {
            deleteRecursiveDirectory(sub);
        }
    }
    file.delete();
}
```

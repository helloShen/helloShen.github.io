/**
 * Format the categories and tags in my post
 */
package com.ciaoshen.blog;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class MyFileWriter {
    /**
     * [用List<String>里的每个元素填写这个文件。]
     * @param file [目标路径，加文件名.]
     *     没有这个文件，就新建一个。
     *     有这个文件，就清空原文件，重新写。
     */
    public static void writeFile(File file, List<String> content) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException(file + " is a directory. " + " writeFile() can only write a file.");
        }
        try {
            if (! file.exists()) { //如果文件不存在，FileWriter不创建新文件。所以必须我自己创建。
                if (! file.createNewFile()) {
                    throw new IllegalStateException(file + " cannot be created! Please check the path!");
                }
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            try {
                for (String line : content) {
                    bw.write(line); //String已经包含换行符，可以直接写。
                }
            } finally {
                bw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("writeFile() meet problem when writting "+ file + e);
        }
    }
    public static void main(String[] args) {
        // test usage
        String path = "/Users/Wei/github/ciaoshen/java/src/com/ciaoshen/blog/MyFileWriter.java";
        File file = new File(path);
        List<String> list = MyFileReader.readFile(file);
        list.add("ADD ONE LINE !!!!!!");
        MyFileWriter.writeFile(file,list);
        System.out.println(MyFileReader.readFile(file));
    }
}

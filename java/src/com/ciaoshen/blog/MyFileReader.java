/**
 * Format the categories and tags in my post
 */
package com.ciaoshen.blog;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class MyFileReader {
    /**
     * [打开文件，以一个List<String>返回文件内容。每行占List中一条记录。]
     * @param  file [目标文件。不可以是文件夹。]
     * @return      [包含所有文件内容的一个List<String>。文件不存在或打不开，直接抛异常，不返回值。]
     * @throws IllegalStateException [文件不存在，或无法打开，抛这个异常]
     */
    public static List<String> readFile(File file) {
        if ( (! file.exists()) || (! file.isFile()) ) {
            throw new IllegalStateException("readFile() method need a file. " + file + " does not exist or is not a file!");
        }
        List<String> content = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String line = "";
                loopEachLine:
                while (true) {
                    line = br.readLine();
                    if (line == null) { break loopEachLine; }
                    content.add(line + "\n"); //每行占一条记录
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException("readFile() method cannot create BufferedReader for file: " + file);
        }
        return content;
    }
    public static void main(String[] args) {
        // test usage
        String path = "/Users/Wei/github/ciaoshen/java/src/com/ciaoshen/blog/MyFileReader.java";
        System.out.println(MyFileReader.readFile(new File(path)));
    }
}

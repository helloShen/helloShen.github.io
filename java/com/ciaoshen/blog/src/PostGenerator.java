/**
 * 帮助我创建新文件
 */
package com.ciaoshen.blog;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

final class PostGenerator {
    /**
     * 主要路径
     */
    private static final String BLOG_ROOT = "/Users/Wei/github/ciaoshen"; // 项目根目录
    private static final String POSTS_DIR = BLOG_ROOT + "/_posts/"; //博客文章在这里。从文章里提取categories属性

    /**
     * 日期格式
     */
    private static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

    // 获取当前日期字符串
    private static String getDate(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
    private static List<String> initTemplate() {
        List<String> template = new ArrayList<>();
        template.add("---\n");
        template.add("layout: post\n");
        template.add("title: \"\"\n");
        template.add("date: " + getDate(FULL_DATE_FORMAT) + "\n");
        template.add("author: \"Wei SHEN\"\n");
        template.add("categories: [\"\"]\n");
        template.add("tags: [\"\"]\n");
        template.add("description: > \n");
        template.add("---\n");
        return template;
    }

    public static void generate(String fileName) {
        String path = POSTS_DIR + getDate(SIMPLE_DATE_FORMAT) + "-" + fileName;
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                try {
                    for (String str : initTemplate()) {
                        bw.write(str);
                    }
                } finally {
                    bw.close();
                }
            } else {
                System.out.println("PostGenerator cannot create " + path + " for you!");
            }
        } catch (IOException e) {
            System.out.println("PostGenerator#generate() ERROR when writing to the file: " + path);
        }
    }
    public static void main(String[] args) {
        //initTemplate(); // test
        if (args.length < 1) {
            throw new IllegalArgumentException("I need the file name to generate the new file!");
        }
        generate(args[0]);
    }
}

/**
 * The template of my post template.
 * Extend this class, implement the initTemplate() method, then it works.
 * initTemplate() should return the Template as a List<String>.
 */
package com.ciaoshen.blog;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

abstract class AbstractPostGenerator {
    /**
     * 主要路径
     */
    String blogRoot = "/Users/Wei/github/ciaoshen"; // 项目根目录
    String postDir = blogRoot + "/_posts/"; //博客文章在这里。从文章里提取categories属性

    /**
     * 日期格式
     */
    static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";


    AbstractPostGenerator() {}
    AbstractPostGenerator(String blogRoot, String postDir) { // 改变路径，可以生成其他类型的文章模板。
        this.blogRoot = blogRoot;
        this.postDir = postDir;
    }
    // 获取当前日期字符串
    String getDate(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    /**
     * generate() should be the only interface provided to the user.
     * @param fileName [name of the new post]
     */
    public void generate(String fileName) {
        String path = postDir + getDate(SIMPLE_DATE_FORMAT) + "-" + fileName;
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

    /**
     * Primitive Function of this skeleton implementation.
     * @return The template of the post.
     * We can design the template according to our needs.
     */
    abstract List<String> initTemplate();
}

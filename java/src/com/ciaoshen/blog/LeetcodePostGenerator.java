/**
 * The specific template for Leetcode post.
 * 此文件会将文件名"leetcode-problem-name.md"中的"leetcode-"前缀，
 * 以及".md"后缀去除，作为内部文件的统一命名空间前缀。例如：
 *      leetcode-two-sum.md --> name space = two-sum
 */
package com.ciaoshen.blog;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.*;

final class LeetcodePostGenerator extends AbstractPostGenerator {
    /**
     * A dummy implementation.
     */
    List<String> initTemplate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Specify the image name with leetcode problem name.
     */
    List<String> initTemplate(String prob) {
        List<String> template = new ArrayList<>();
        template.add("---\n");
        template.add("layout: post\n");
        template.add("title: \"Leetcode - Algorithm - " + toTitle(prob) + "\"\n");
        template.add("date: " + getDate(FULL_DATE_FORMAT) + "\n");
        template.add("author: \"Wei SHEN\"\n");
        template.add("categories: [\"algorithm\",\"leetcode\"]\n");
        template.add("tags: [\"\"]\n");
        template.add("level: \"\"\n");
        template.add("description: > \n");
        template.add("---\n");
        template.add("\n");
        template.add("### 题目\n");
        for (int i = 1; i < 4; i++) {
            template.add("\n");
            template.add("### 解法" + i + "\n");
            template.add("\n");
            template.add("#### 代码\n");
            template.add("```java\n");
            template.add("\n");
            template.add("```\n");
            template.add("\n");
            template.add("#### 结果\n");
            template.add("![" + prob + "-" + i + "](/images/leetcode/" + prob + "-" + i + ".png)\n");
            template.add("\n");
        }
        return template;
    }

    // work only with english file name
    private static String toTitle(String prob) {
        String[] strs = prob.split("-");
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            char[] chars = str.toCharArray();
            if (chars.length > 0) { chars[0] = Character.toUpperCase(chars[0]); }
            sb.append(chars);
            sb.append(" ");
        }
        return sb.toString();
    }

    String extension = "\\.\\w+$"; // file extention regex. ex: ".md"
    String prefix = "^leetcode-"; // "leetcode-" prefix
    /**
     * This overrided version pass the filename as an argument to the initTemplate() method.
     */
    @Override
    public void generate(String fileName) {
        String path = postDir + getDate(SIMPLE_DATE_FORMAT) + "-" + fileName;
        File file = new File(path);
        String prob = fileName.replaceAll(extension,"").replaceAll(prefix,""); // remove "leetcode-" & ".md"
        try {
            if (file.createNewFile()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                try {
                    for (String str : initTemplate(prob)) {
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
        /**
         * Unit Test
         */
        // initTemplate();
        // System.out.println(new LeetcodePostGenerator().removeExtension("leetcode-test.md"));

        /**
         * Main process
         */
        if (args.length < 1) {
            throw new IllegalArgumentException("I need the file name to generate the new file!");
        }
        new LeetcodePostGenerator().generate(args[0]);

    }
}

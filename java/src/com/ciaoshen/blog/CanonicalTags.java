/**
 * Format the categories and tags in my post
 */
package com.ciaoshen.blog;
import java.util.*;
import java.util.regex.*;
import java.io.*;


class CanonicalTags {
    /**
     * category pattern
     *      categories: ["aaa","bbb","ccc"]
     */
    private static final Matcher CATEGORY_LIST = Pattern.compile("(^categories: \\[(.*?)\\])").matcher("");
    /**
     * tags pattern
     *      tags: ["aaa","bbb","ccc"]
     */
    private static final Matcher TAG_LIST = Pattern.compile("(^tags: \\[(.*?)\\])").matcher("");
    /**
     * string pattern
     *      "aaa","bbb","ccc"
     */
    private static final Matcher STR_M = Pattern.compile("\"(.*?)\"").matcher("");
    /**
     * treate one file:
     *      1. open file
     *      2. canonicalize the pattern
     *      3. re-write the file
     * @param  file [target file]
     * @return      [void]
     */
    private static void scanFile(File file) {
        List<String> content = MyFileReader.readFile(file);
        for (String line : content) {
            int index = content.indexOf(line);
            line = checkPattern(CATEGORY_LIST, STR_M, line);
            line = checkPattern(TAG_LIST, STR_M, line);
            content.set(index,line);
        }
        MyFileWriter.writeFile(file,content);
    }
    /**
     * 如果匹配到pattern 1,
     * 找到所有pattern 2，并用canonicalize()函数格式化后的str替换pattern 2。
     */
    private static String checkPattern(Matcher m1, Matcher m2, String str) {
        m1.reset(str);
        if (m1.find()) {
            System.out.println(str);
            m2.reset(str);
            while(m2.find()) {
                String tag = m2.group(1);
                str = str.replaceFirst(tag,canonicalize(tag));
            }
        }
        return str;
    }
    /**
     * [给一个String，返回的它的Canonical版本]
     * @param  str [需要格式化的字符串]
     * @return     [格式化以后的字符串]
     */
    private static String canonicalize(String str) {
        str = str.toLowerCase(); // 变小写
        str = str.replaceAll("_"," "); // 去掉下划线
        return str;
    }

    public static void run(String sourcePath) {
        File sourceDir = new File(sourcePath);
        if (! sourceDir.isDirectory()) {
            throw new IllegalArgumentException("replace() method need a directory as paramater!" + sourcePath + " is not a directory!");
        }
        for (File file : sourceDir.listFiles()) {
            scanFile(file);
        }
    }
    public static void main(String[] args) {
        //String path = "/Users/Wei/github/ciaoshen/_posts/2017-02-27-bootstrapJekyll.md";
        //File file = new File(path);
        //scanFile(file);

        // 主程序
        String dir = "/Users/Wei/github/ciaoshen/_posts/";
        run(dir);
    }
}

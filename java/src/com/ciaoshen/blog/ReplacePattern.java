/**
 * 打开文件夹X，搜索X中的所有文件，找到符合Pattern A的内容，然后用C替换所有的Pattern B。
 */
package com.ciaoshen.blog;
import java.util.*;
import java.util.regex.*;
import java.io.*;

final class ReplacePattern {
    /**
     * [打开文件，以一个List<String>返回文件内容]
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
    /**
     * 用字符串C，替换符合Pattern A的字符串里的Pattern B。
     * 没找到，就返回原List
     */
    public static List<String> replacePattern(List<String> content, Pattern pa, Pattern pb, String replacement) {
        Matcher ma = pa.matcher("");
        Matcher mb = pb.matcher("");
        ListIterator<String> ite = content.listIterator();
        String line = "";
        while (ite.hasNext()) {
            line = ite.next();
            ma = ma.reset(line);
            if (ma.find()) {
                System.out.println(line);
                ite.set(mb.reset(line).replaceFirst(replacement));
            }
        }
        return content;
    }
    public static void replace(String sourcePath, String directionPath, Pattern pa, Pattern pb, String replacement) {
        File sourceDir = new File(sourcePath);
        if (! sourceDir.isDirectory()) {
            throw new IllegalArgumentException("replace() method need a directory as paramater!" + sourcePath + " is not a directory!");
        }
        List<String> content = new ArrayList<>();
        List<String> filteredContent = new ArrayList<>();
        for (File file : sourceDir.listFiles()) {
            content = readFile(file);
            filteredContent = replacePattern(content,pa,pb,replacement);
            File newFile = new File(directionPath + file.getName());
            writeFile(newFile,filteredContent);
        }
    }
    private static class TestUnit {
        private static void testReadFile() {
            String rightPath = "/Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/src/ReplacePattern.java";
            String wrongPath = "/Users/Wei/HelloKitty.txt";
            System.out.println(readFile(new File(rightPath)));
            System.out.println(readFile(new File(wrongPath)));
        }
        private static void testWriteFile() {
            String existFile = "/Users/Wei/testFile.java";
            String emptyPath = "/Users/Wei/testFile2.java";
            String dir = "/Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/src/";
            List<String> strList = Arrays.asList(new String[] {"Hello","Ronald"});
            writeFile(new File(existFile), strList);
            writeFile(new File(emptyPath), strList);
            writeFile(new File(dir), strList);
        }
        private static void testReplacePattern() {
            Pattern pa = Pattern.compile("^!\\[.*?\\](.*?)");
            Pattern pb = Pattern.compile("/uploads/");
            String replacement = "/images/";
            String path = "/Users/Wei/result.txt";
            List<String> content = readFile(new File(path));
            System.out.println(replacePattern(content, pa, pb, replacement));
        }
        /**
         * [对target匹配Pattern p]
         *     PASS: 匹配成功
         *     FAIL: 匹配失败
         */
        private static void testSinglePattern(Pattern p, String target) {
            System.out.print("Pattern: " + p.pattern() + ", String: " + target + "... ...");
            if (p.matcher(target).find()) {
                System.out.println("    PASS!");
            } else {
                System.out.println("    FAIL!");
            }
        }
        /*
         * 给出一组理论上应该匹配成功的例子，去匹配Pattern p
         * 如果有一个显示FAIL，就说明Pattern有问题。
         */
        private static void simpleTestPattern(Pattern p, List<String> targets) {
            for (String target : targets) {
                testSinglePattern(p,target);
            }
        }
        private static void testPattern() {
            Pattern p = Pattern.compile("^!\\[.*?\\](.*?)");
            List<String> targets = new ArrayList<>();
            targets.add("![jre](/uploads/javaEnvi/javaEnvi.png)");
            targets.add("![memoryLayout](/uploads/tij4-2/memoryLayout.gif)");
            targets.add("![heap](/uploads/tij4-2/heap.gif)");
            targets.add("![javaMemory](/uploads/tij4-2/javaMemory.png)");
            simpleTestPattern(p,targets);
        }
    }
    public static void main(String[] args) {
        /**
         * Unit Test
         */
        //TestUnit.testReadFile();
        //TestUnit.testWriteFile();
        //TestUnit.testReplacePattern();
        //TestUnit.testPattern();

        /**
         * 主程序
         */
        String sourcePath = "/Users/Wei/old_hexo_posts/original_posts/";
        String directionPath = "/Users/Wei/github/ciaoshen/_posts/";
        Pattern pa = Pattern.compile("^!\\[.*?\\](.*?)");
        Pattern pb = Pattern.compile("\\(.*?/uploads/");
        String replacement = "\\(/images/";
        replace(sourcePath,directionPath,pa,pb,replacement);
    }
}

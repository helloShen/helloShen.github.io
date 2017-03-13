/**
 * Generate Categories Page
 */
package com.ciaoshen.blog;
import java.util.*;
import java.util.regex.*;
import java.io.*;

final class CategoriesPageGenerator {
    /**
     * 主要路径参数
     */
    private static final String BLOG_ROOT = "/Users/Wei/github/ciaoshen"; // 项目根目录
    private static final String POSTS_DIR = BLOG_ROOT + "/_posts/"; //博客文章在这里。从文章里提取categories属性
    private static final String CATEGORIES_DIR = BLOG_ROOT + "/categories/"; //生成的CategoryName.html文件，存在这里
    /**
     * YAML头区块里，categories属性的Pattern。对应3种格式：
     *  1. 不带引号: categories: aaa
     *  2. 带引号: categories: "aaa"
     *  3. 规范的方括号: categories: ["aaa","bbb","ccc"]
     */
    private static final Matcher CATEGORY_WITH_QUOTE = Pattern.compile("(^categories: \"(.*?)\")").matcher("");
    private static final Matcher CATEGORY_WITHOUT_QUOTE = Pattern.compile("(^categories: (\\w+))").matcher("");
    private static final Matcher CATEGORY_LIST = Pattern.compile("(^categories: \\[(.*?)\\])").matcher("");

    /**
     * 模板: 利用liquid语言，将某个特定category下的所有文章，显示在当前页。
     */
    private static String[] getTemplate(String category) {
        String[] result = new String[] {
             "---\n",
             "layout: main\n",
             "title: \"" + category + "\"\n",
             "---\n",
             "\n",
             "<!-- Page Content -->\n",
             "<div class=\"container\">\n",
             //"<h3 class=\"lead\">Posts in category of \"" + category + "\"</h3>\n",
             "<div class=\"row\">\n",
             "<div class=\"col-lg-9 col-md-9 col-sm-9\">\n",
             "<div class=\"row\">\n",
             "</br>\n",
             "</br>\n",
             "{% for category in site.categories %}\n",
             "{% if category[0] == \"" + category + "\" %}\n",
             "{% for posts in category %}\n",
             "{% for post in posts %}\n",
             "{% if post != \"" + category + "\" %}\n",
             "{% include post-info-without-comment.html %}",
             "{% endif %}\n",
             "{% endfor %}\n",
             "{% endfor %}\n",
             "{% endif %}\n",
             "{% endfor %}\n",
             "</div>\n",
             "<!-- /.articles row -->\n",
             "</div>\n",
             "{% include categoriesSidebar.html %}\n",
             "</div>\n",
             "</div>\n",
             "<script>\n",
             "$(\"a#cat_" + category.replaceAll(" ","-") + "\").addClass(\"active\");\n",
             "</script>\n"
        };
        return result;
    }
    public static Set<String> getCategories(String dirPath) {
        List<File> files = getFiles(dirPath);
        Set<String> categories = new HashSet<String>();
        for (File file : files) {
            if(checkExtension(file.getName(),"md")) { // markdown file
                List<String> category = getCategory(file);
                for (String str : category) {
                    categories.add(str);
                }
            }
        }
        return categories;
    }
    // return null if no categories field in YAML header
    private static List<String> getCategory(File file) {
        List<String> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String line = "";
                lineLoop:
                while (true) {
                    line = br.readLine();
                    if (line == null) { break lineLoop; } // exit point
                    CATEGORY_WITH_QUOTE.reset(line);
                    CATEGORY_WITHOUT_QUOTE.reset(line);
                    CATEGORY_LIST.reset(line);
                    if (CATEGORY_WITH_QUOTE.find()) { // categories: A
                        result.add(CATEGORY_WITH_QUOTE.group(2));
                    }
                    if (CATEGORY_WITHOUT_QUOTE.find()) { // categories: "A"
                        result.add(CATEGORY_WITHOUT_QUOTE.group(2));
                    }
                    if (CATEGORY_LIST.find()) { // categories: ["A","B","C"]
                        String listStr = CATEGORY_LIST.group(2);
                        //System.out.println("In Squar: " + listStr);
                        Matcher quoteMatcher = Pattern.compile("\"(.*?)\"").matcher(listStr);
                        while(quoteMatcher.find()) {
                            result.add(quoteMatcher.group(1));
                        }
                    }
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            System.out.println("Check file " + file.getName());
        }
        return result;
    }
    private static List<File> getFiles(String dirPath) {
        File dir = new File(dirPath);
        checkDir(dir);
        File[] posts = dir.listFiles();
        return Arrays.asList(posts);
    }
    private static void checkDir(File dir) {
        if (! dir.isDirectory()) {
            throw new IllegalArgumentException(dir + " does not exist!");
        }
    }
    private static boolean checkExtension(String fileName, String extension) {
        Matcher matcher = Pattern.compile("(.+?)(\\." + extension + ")$").matcher(fileName);
        return matcher.find();
    }
    private static void createPage(String dir, String category) {
        File dirFile = new File(dir);
        dirFile.mkdir();
        File newPage = new File(dir+category.replaceAll(" ","-")+".html"); //网址，去掉空格
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(newPage));
            try {
                for (String str : getTemplate(category)) {
                    writer.write(str);
                }
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Check the path: " + dir + category + ".html");
        }
    }

    public static void generate() { // 自己到 _posts 文件夹下提取categories
        generate(getCategories(POSTS_DIR).toArray(new String[0]));
    }
    public static void generate(String[] categories) {
        File dir = new File(CATEGORIES_DIR);
        if (delete(dir)) { // 生成新页面前，先尝试删除旧页面，创新创建文件夹
            dir.mkdir();
        }
        for (String category : categories) {
            createPage(CATEGORIES_DIR,category);
        }
    }
    /**
     * 递归删除路径下的所有文件
     * @return true，这个文件夹不存在了（本来就不存在，或者正确删除了）
     * @return false, 文件夹存在，但暂时无法删除。
     */
    public static boolean delete(File file) {
        if (! file.exists()) { // 有这个文件才删，没有就不删
            return true;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        if (! file.delete()) {
            System.out.println(file + " cannot be deleted!");
            return false;
        }
        return true;
    }
    private static class TestUnit {
        private static void testGetFiles() {
            List<File> files = getFiles(POSTS_DIR);
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
        private static void testCheckExtension() {
            List<File> files = getFiles(POSTS_DIR);
            for (File file : files) {
                String name = file.getName();
                System.out.println(name + ": " + checkExtension(name,"md"));
            }
        }
        private static void testGetCategories() {
            System.out.println(getCategories(POSTS_DIR));
        }
        private static void testPattern() {
            String withSquar = "categories: [\"Hello\",\"Ronald\"]";
            Pattern p = Pattern.compile("(^categories: \\[(.*?)\\])");
            Matcher m = p.matcher(withSquar);
            if (m.find()) {
                System.out.println(m.group(2));
            }
        }
        private static void testDelete() {
            delete(new File(CATEGORIES_DIR));
        }
    }
    public static void main(String[] args) {
        /**
         * 单元测试
         */
        //TestUnit.testGetFiles();
        //TestUnit.testCheckExtension();
        //TestUnit.testGetCategories();
        //TestUnit.testPattern();
        //TestUnit.testDelete();

        /**
         * 运行主程序
         */
        generate();
    }
}

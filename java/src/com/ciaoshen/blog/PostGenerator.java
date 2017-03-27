/**
 * The Default post template for the general-purpose usage.
 */
package com.ciaoshen.blog;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

final class PostGenerator extends AbstractPostGenerator {
    /**
     * The default post template with only a simple YMAL Header.
     */
    List<String> initTemplate() {
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

    public static void main(String[] args) {
        //initTemplate(); // test
        if (args.length < 1) {
            throw new IllegalArgumentException("I need the file name to generate the new file!");
        }
        new PostGenerator().generate(args[0]);
    }
}

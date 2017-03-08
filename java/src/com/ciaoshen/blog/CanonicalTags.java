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

    public static canonicalize() {

    }
    public static scanFile() {
        // open file, and read a big string
        // recognize all categories and tags, format them
        // rewrite the file
    }

}

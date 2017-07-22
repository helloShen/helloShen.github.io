---
layout: post
title: "A Leetcode Solution Strategy Template"
date: 2017-07-19 22:04:41
author: "Wei SHEN"
categories: ["leetcode"]
tags: ["design"]
description: >
---

### Leetcode 给出的接口
Leetcode给出的接口基本就是一个在`Solution`类里的方法签名。比如，下面就是题目`Two Sum`的接口。
```java
public class Solution {
    public int[] twoSum(int[] nums, int target) {

    }
}
```

但在自己电脑上做题的时候，不可能都叫`Solution`，为了管理好代码，每道都要有自己的命名空间。而且提交之前如果想简单测试一下，还需要配备测试代码。所以每做一题之前经常要花10分钟的时间搭个小框架。时间久了，这就变成了重复性劳动。

为了节省下这部分时间，把注意力集中到算法上，可以考虑写个模板，自动生成框架。目标是每次要做题了，输入一行命令行，框架就搭好了。
```
new
```

### Problem - Solution - Test 框架
主要就是一个 **策略模式**，
* 每题都是一个`Problem`
* 每个`Problem`可以有好几个解题方法`Solution`
* 在`Problem`里提供一个`Solution`的注册表
* `Test`类就在注册表里加载不同的`Solution`进行测试

数据结构的设计细节，
* `Solution`是`Problem`类的内部抽象类。
* 不同的解题方法`Solution1`,`Solution2`等等都继承自`Solution`抽象类。
* `Problem`类内部的注册表是面向`Solution`抽象类的。
* `Test`也是`Problem`类的静态内部套嵌类，`Test`类握有一个`Problem`的实例。
* `Test`的操作也是面向`Solution`抽象类的。

一个`Problem`类看上去就像下面这个样子，
```java
/**
 * Leetcode - Algorithm - AnyProblem
 */
package com.ciaoshen.leetcode;
import java.util.*;
import com.ciaoshen.leetcode.myUtils.*;

/**
 *  Each problem is initialized with 3 solutions.
 *  You can expand more solutions.
 *  Before using your new solutions, don't forget to register them to the solution registry.
 */
class AnyProblem implements Problem {
    private Map<Integer,Solution> solutions = new HashMap<>(); // solutions registry
    // register solutions HERE...
    private AnyProblem() {
        register(new Solution1());
        register(new Solution2());
        register(new Solution3());
    }
    private abstract class Solution {
        private int id = 0;
        abstract public boolean isStrobogrammatic(String num);
    }
    private class Solution1 extends Solution {
        { super.id = 1; }
        // implement your solution's method HERE...
        public boolean isStrobogrammatic(String num) {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }
    }

    private class Solution2 extends Solution {
        { super.id = 2; }
        // implement your solution's method HERE...
        public boolean isStrobogrammatic(String num) {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }
    }

    private class Solution3 extends Solution {
        { super.id = 3; }
        // implement your solution's method HERE...
        public boolean isStrobogrammatic(String num) {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }
    }
    // you can expand more solutions HERE if you want...


    /**
     * register a solution in the solution registry
     * return false if this type of solution already exist in the registry.
     */
    private boolean register(Solution s) {
        return (solutions.put(s.id,s) == null)? true : false;
    }
    /**
     * chose one of the solution to test
     * return null if solution id does not exist
     */
    private Solution solution(int id) {
        return solutions.get(id);
    }

    private static class Test {
        private AnyProblem problem = new AnyProblem();
        private Solution solution = null;


        /** initialize your testcases HERE... */
            //... ...
            //... ...
            //... ...
            //... ...
            //... ...
            //... ...


        // call method in solution
        private void call() {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }

        // public API of Test interface
        public void test(int i) {
            solution = problem.solution(i);
            if (solution == null) { System.out.println("Sorry, [id:" + i + "] doesn't exist!"); return; }

            /** involk call() method HERE */
            /** involk call() method HERE */
            /** involk call() method HERE */

            call();

            /** involk call() method HERE */
            /** involk call() method HERE */
            /** involk call() method HERE */
        }
    }
    public static void main(String[] args) {
        Test test = new Test();
        test.test(1); // call by the solution id
    }
}
```

### 模板和解析器
在`.txt`文本文件里写下下面的文本（模板的模板）。在所有用到类名的地方用`_CLASS_`标记，用到方法签名的地方用`_METHOD_`标记。
```
/**
 * Leetcode - Algorithm - _CLASS_
 */
package com.ciaoshen.leetcode;
import java.util.*;
import com.ciaoshen.leetcode.myUtils.*;

/**
 *  Each problem is initialized with 3 solutions.
 *  You can expand more solutions.
 *  Before using your new solutions, don't forget to register them to the solution registry.
 */
class _CLASS_ implements Problem {
    private Map<Integer,Solution> solutions = new HashMap<>(); // solutions registry
    // register solutions HERE...
    private _CLASS_() {
        register(new Solution1());
        register(new Solution2());
        register(new Solution3());
    }
    private abstract class Solution {
        private int id = 0;
        abstract _METHOD_;
    }
    private class Solution1 extends Solution {
        { super.id = 1; }
        // implement your solution's method HERE...
        _METHOD_ {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }
    }

    private class Solution2 extends Solution {
        { super.id = 2; }
        // implement your solution's method HERE...
        _METHOD_ {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }
    }

    private class Solution3 extends Solution {
        { super.id = 3; }
        // implement your solution's method HERE...
        _METHOD_ {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }
    }
    // you can expand more solutions HERE if you want...


    /**
     * register a solution in the solution registry
     * return false if this type of solution already exist in the registry.
     */
    private boolean register(Solution s) {
        return (solutions.put(s.id,s) == null)? true : false;
    }
    /**
     * chose one of the solution to test
     * return null if solution id does not exist
     */
    private Solution solution(int id) {
        return solutions.get(id);
    }

    private static class Test {
        private _CLASS_ problem = new _CLASS_();
        private Solution solution = null;


        /** initialize your testcases HERE... */
            //... ...
            //... ...
            //... ...
            //... ...
            //... ...
            //... ...


        // call method in solution
        private void call() {
            /** write your code HERE */
            // ... ...
            // ... ...
            // ... ...
            // ... ...
            // ... ...
        }

        // public API of Test interface
        public void test(int i) {
            solution = problem.solution(i);
            if (solution == null) { System.out.println("Sorry, [id:" + i + "] doesn't exist!"); return; }

            /** involk call() method HERE */
            /** involk call() method HERE */
            /** involk call() method HERE */

            call();

            /** involk call() method HERE */
            /** involk call() method HERE */
            /** involk call() method HERE */
        }
    }
    public static void main(String[] args) {
        Test test = new Test();
        test.test(1); // call by the solution id
    }
}
```

Java没有宏替换，所以需要写个解析器，替换所有`_CLASS_`和`_METHOD_`标记，就可以生成类的源文件的模板，
```java
/**
 * Read template from "~/github/leetcode/java/template/problem.txt"
 * To generate template of a class under "~/github/leetcode/java/src/com/ciaoshen/leetcode/"
 */
package com.ciaoshen.leetcode.template;
import java.util.*;
import java.io.*;

class ProblemGenerator {
    /**
     * @param args [classname and method]
     * args[0]: classname
     * args[1]: method
     */
    private static final String CLASS_FLAG = "_CLASS_";
    private static final String METHOD_FLAG = "_METHOD_";
    public static void main(String[] args) {
        if (args.length < 4) { // defense
            System.out.println("Parameters not enough!"); return;
        }
        String srcpath = args[0]; // template source path
        String despath = args[1]; // new class path
        String classname = args[2]; // calss name
        String method = args[3]; // method signature

        String content = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(srcpath)));
            StringBuilder sb = new StringBuilder();
            try {
                while (true) {
                    String line = br.readLine();
                    if (line == null) { break; }
                    line = line.replaceAll(CLASS_FLAG,classname);
                    line = line.replaceAll(METHOD_FLAG,method);
                    sb.append(line+"\n");
                }
                content = sb.toString();
            } finally {
                br.close();
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Check file path: " + fnfe);
        } catch (IOException ioe) {
            System.out.println("Error when reading file " + ioe);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(despath)));
            try {
                bw.write(content,0,content.length());
            } finally {
                bw.close();
            }
        } catch (IOException ioe) {
            System.out.println("Error when writing file " + ioe);
        }
    }
}
```

用下面这个`shell`脚本，
```bash
#!/bin/sh
#############################################################
# Parameters
# 每次修改 CLASS_NAME 和 METHOD
# CLASS_NAME: 希望生成类的名字。
# METHOD: Leetcode给出的Solution类里的主要方法签名。
#
# 例如：
# 对问题Two Sum我想写一个叫TwoSum的类
# Leetcode给出的方法签名如下：
# 	public class Solution {
#   		public int[] twoSum(int[] nums, int target) {
#
#    		}
#	}
#
# 我应该把  CLASS_NAME 和 METHOD 改为：
# CLASS_NAME: "TwoSum"
# METHOD: "public int[] twoSum(int[] nums, int target)"
##############################################################

# basic dir
BASE_DIR="/Users/Wei/github/leetcode/java/template"
CLASS_DIR="${BASE_DIR}/bin"
SOURCE_DIR="${BASE_DIR}/src"
PACKAGE="com.ciaoshen.leetcode.template"

# template
SOURCE_PATH="${SOURCE_DIR}/com/ciaoshen/leetcode/template"

# file
FILE_NAME="ProblemGenerator"
FILE="${FILE_NAME}.java"

# parameters
################################################
# 请修改下面  CLASS_NAME  和  METHOD  两个参数
################################################
CLASS_NAME="AnyProblem"
METHOD="public boolean isStrobogrammatic(String num)"
TEMPLATE_FILE="${BASE_DIR}/problem.txt"
DESTINATION="/Users/Wei/github/leetcode/java/src/com/ciaoshen/leetcode/${CLASS_NAME}.java"

# compile
javac -d ${CLASS_DIR} -cp ${CLASS_DIR} -sourcepath ${SOURCE_DIR} "${SOURCE_PATH}/${FILE}"
# run
java -cp ${CLASS_DIR} "${PACKAGE}.${FILE_NAME}" ${TEMPLATE_FILE} ${DESTINATION} ${CLASS_NAME} "${METHOD}"
```

每次只需要运行`new`命令，就可以得到一个`Problem`的模板，和测试框架。再也不用每次都花10分钟搭框架了。现在什么时候想写一个算法，坐下来就写，也是安逸。
```
new
```

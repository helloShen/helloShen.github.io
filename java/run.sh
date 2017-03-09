#!/bin/sh
BASE_DIR="/Users/Wei/github/ciaoshen/java"
CLASS_PATH="$BASE_DIR/bin/"
SOURCE_DIR="$BASE_DIR/src/com/ciaoshen/blog"

#####################################
#
# 用ReplacePattern替换图片库路径
#
#####################################

#javac -cp $CLASS_PATH -d $CLASS_PATH $SOURCE_DIR/ReplacePattern.java
#java -cp $CLASS_PATH com.ciaoshen.blog.ReplacePattern

###################################################
#
# 测试MyFileReader.java 和 MyFileWriter.java
#
###################################################
#javac -cp $CLASS_PATH -d $CLASS_PATH $SOURCE_DIR/MyFileReader.java
#java -cp $CLASS_PATH com.ciaoshen.blog.MyFileReader

#javac -cp $CLASS_PATH -d $CLASS_PATH $SOURCE_DIR/MyFileWriter.java
#java -cp $CLASS_PATH com.ciaoshen.blog.MyFileWriter

###################################################
#
# 测试MyFileReader.java 和 MyFileWriter.java
#
###################################################
javac -cp $CLASS_PATH -d $CLASS_PATH $SOURCE_DIR/CanonicalTags.java
java -cp $CLASS_PATH com.ciaoshen.blog.CanonicalTags


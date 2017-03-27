###################################################################
#
# 此文件用来自动按照当地时间，生成一个带有默认YAML头的.md文件
# 命令格式如下：
#
# 	sh new.sh <post_name>
#
# 注意post_name需要带有扩展名，比如.md表示markdown文件。
#
###################################################################


# 参数
BASE_DIR="/Users/Wei/github/ciaoshen/java"
CLASS_PATH="$BASE_DIR/bin/"
SOURCE_DIR="$BASE_DIR/src/com/ciaoshen/blog"

# 运行PostGenerator，以生成带YAML头区块的新markdown文件
# 参数：$1 代表文件名。例如，newFile.md。 注意！文件名需要带后缀
javac -cp $CLASS_PATH -d $CLASS_PATH $SOURCE_DIR/LeetcodePostGenerator.java
java -cp $CLASS_PATH com.ciaoshen.blog.LeetcodePostGenerator $1

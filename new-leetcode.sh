###################################################################
#
# 此文件用来自动按照当地时间，生成一个带有默认YAML头的.md文件
# 专门用于Leetcode的文章：
#
# 	sh new.sh <leetcode-problem-name.md>
#
# 注意需要带有扩展名，".md"表示markdown文件。
#
#
# 此文件会将文件名"leetcode-problem-name.md"中的"leetcode-"前缀
# 以及".md"后缀去除，作为内部文件的统一命名空间前缀。例如：
#	leetcode-two-sum.md --> name space = two-sum
#
##################################################################


# 参数
BASE_DIR="/Users/Wei/github/ciaoshen/java"
CLASS_PATH="$BASE_DIR/bin/"
SOURCE_DIR="$BASE_DIR/src/com/ciaoshen/blog"

# 运行PostGenerator，以生成带YAML头区块的新markdown文件
# 参数：$1 代表文件名。例如，newFile.md。 注意！文件名需要带后缀
javac -d $CLASS_PATH -cp $CLASS_PATH $SOURCE_DIR/LeetcodePostGenerator.java
java -cp $CLASS_PATH com.ciaoshen.blog.LeetcodePostGenerator $1

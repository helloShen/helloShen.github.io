# 生成所有categories页面
javac -d /Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/bin/ /Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/src/CategoriesPageGenerator.java
java -classpath /Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/bin/ com.ciaoshen.blog.CategoriesPageGenerator

# 生成所有tag页面
javac -d /Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/bin/ /Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/src/TagsPageGenerator.java
java -classpath /Users/Wei/github/ciaoshen/java/com/ciaoshen/blog/bin/ com.ciaoshen.blog.TagsPageGenerator

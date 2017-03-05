# 提交至Github
# $1: 更新描述，比如增加了哪篇文章
git checkout master
git merge jekyll_beta
git fetch origin    # origin = git@github.com:helloShen/helloShen.github.io.git
git merge origin/master
git push origin master

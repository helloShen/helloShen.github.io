---
layout: post
title: "Leetcode - Algorithm - Design Twitter "
date: 2018-08-16 01:08:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","heap","design"]
level: "medium"
description: >
---

### 题目
Design a simplified version of Twitter where users can post tweets, follow/unfollow another user and is able to see the 10 most recent tweets in the user's news feed. Your design should support the following methods:

1. **postTweet(userId, tweetId)**: Compose a new tweet.
2. **getNewsFeed(userId)**: Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent.
3. **follow(followerId, followeeId)**: Follower follows a followee.
4. **unfollow(followerId, followeeId)**: Follower unfollows a followee.

Example:
```
Twitter twitter = new Twitter();

// User 1 posts a new tweet (id = 5).
twitter.postTweet(1, 5);

// User 1's news feed should return a list with 1 tweet id -> [5].
twitter.getNewsFeed(1);

// User 1 follows user 2.
twitter.follow(1, 2);

// User 2 posts a new tweet (id = 6).
twitter.postTweet(2, 6);

// User 1's news feed should return a list with 2 tweet ids -> [6, 5].
// Tweet id 6 should precede tweet id 5 because it is posted after tweet id 5.
twitter.getNewsFeed(1);

// User 1 unfollows user 2.
twitter.unfollow(1, 2);

// User 1's news feed should return a list with 1 tweet id -> [5],
// since user 1 is no longer following user 2.
twitter.getNewsFeed(1);
```

### 初步理解问题
这题首先要搞清楚“原创文章”和“最近提要”的区别：
1. 原创文章是每个作者的固定资产
2. 10篇最近提要是对其他作者原创文章的一种“引用”

所以只有每个作者的原创文章需要实际存档。这是不可省略的。但怎么维护10篇最近提要就存在2种完全相反的做法。

##### 一种是“勤劳”地 **主动推送**
> 我希望为每个用户维护一个最近动态表。获得最近提要的getNewsFeed()函数只需要直接返回这个表。

![design-twitter-a](/images/leetcode/design-twitter-a.png)

这样做是因为博客文章的被阅读量要远远大于作者提交新文章的频率。所以宁愿每次更新文章的时候都保持最近提要表是最新的，比用户每次访问都重新计算一个最近提要表要好很多。实际应用中肯定是这种设计方法。

##### 第二种是“懒惰”的 **临时搜索** 流，
> 每个用户只知道他有多少偶像。每次查看最近提要的时候都临时到每个偶像的原创列表中去找。

![design-twitter-b](/images/leetcode/design-twitter-b.png)

效果正好和主动推送相反，更新文章啥都不干，所以快。但查阅最近提要的时候就比较要命。


### 主动推送流派
设计采用的OO思想，数据封装成`User`和`Tweet`两个类。并且用`PriorityQueue`维护最近提要列表。PriorityQueue的好处是能在`O(logn)`时间里找出当前最小值。但PriorityQueue只保证`head`元素是全局最小元素，所以按顺序删元素比较麻烦，不能保持长度为`10`，必须保留全部的最近提要。然后在`getNewsFeed()`函数中取前10个。

#### 代码
```java
class Twitter {

    public Twitter() {
        time = 0;
        users = new HashMap<Integer,User>();
    }
    public void postTweet(int userId, int tweetId) {
        User u = confirmUser(userId);
        Tweet t = new Tweet(userId,tweetId);
        u.posts.add(t);                     //更新自己原创列表
        u.newsFeed.add(t);                  //更新自己摘要
        for (User follower : u.followers) { //更新粉丝摘要
            follower.newsFeed.add(t);
        }
    }
    public List<Integer> getNewsFeed(int userId) {
        List<Integer> res = new ArrayList<>();
        List<Tweet> removed = new ArrayList<>();
        User u = confirmUser(userId);
        for (int remain = MAX; !u.newsFeed.isEmpty() && remain > 0; remain--) {
            Tweet t = u.newsFeed.poll();
            removed.add(t);
            res.add(t.tweetId);
        }
        for (Tweet t : removed) {
            u.newsFeed.add(t);
        }
        return res;
    }
    public void follow(int followerId, int followeeId) {
        if (followerId == followeeId) { return; }
        User follower = confirmUser(followerId);
        User followee = confirmUser(followeeId);
        if (!followee.followers.contains(follower)) {
            followee.followers.add(follower);   //更新粉丝列表
            for (Tweet t : followee.posts) { //更新摘要
                follower.newsFeed.add(t);
            }
        }
    }
    public void unfollow(int followerId, int followeeId) {
        if (followerId == followeeId) { return; }
        User follower = confirmUser(followerId);
        User followee = confirmUser(followeeId);
        followee.followers.remove(follower);                 //更新粉丝列表
        Iterator<Tweet> ite = follower.newsFeed.iterator();  //更新摘要
        while (ite.hasNext()) {
            Tweet t = ite.next();
            if (t.userId == followee.userId) {
                ite.remove();
            }
        }
    }

    /** ====================【私有成员】==================== */

    //创建新用户
    private User confirmUser(int userId) {
        if (!users.containsKey(userId)) {
            users.put(userId,new User(userId));
        }
        return users.get(userId);
    }


    private final int MAX = 10; //new feed的大小
    private int time;
    private Map<Integer,User> users;

    private class User {
        private int userId;
        private LinkedList<Tweet> posts;
        private PriorityQueue<Tweet> newsFeed;
        private Set<User> followers;

        private User(int userId) {
            this.userId = userId;
            posts = new LinkedList<Tweet>();
            newsFeed = new PriorityQueue<Tweet>();
            followers = new HashSet<User>();
        }
    }
    private class Tweet implements Comparable<Tweet> {
        private int tweetId;
        private int userId;
        private int timeStamp;

        private Tweet(int userId, int tweetId) {
            this.userId = userId;
            this.tweetId = tweetId;
            this.timeStamp = ++time;
        }
        public int compareTo(Tweet t) {
            return t.timeStamp - timeStamp; //时间戳越大，文章越新
        }
    }
}
```

#### 结果
![design-twitter-2](/images/leetcode/design-twitter-2.png)


### 懒惰的临时搜索流派
还是用OO设计。几乎所有的工作量全部推到了`getNewsFeed()`函数里执行。虽然后面测下来速度很快，但不适合大规模线上使用。而且扩展性很差，数据结构上每个用户的原创文章列表，以及后面用来合并出最近提要的临时列表都是`PriorityQueue`，针对这道题是优化了，缺点是通用性不足。


#### 代码
```java
class Twitter {

    public Twitter() {
        time = 0;
        users = new HashMap<Integer,User>();
    }
    public void postTweet(int userId, int tweetId) {
        User u = confirmUser(userId);
        Tweet t = new Tweet(userId,tweetId);
        u.posts.add(t);
    }
    public List<Integer> getNewsFeed(int userId) {
        User u = confirmUser(userId);
        PriorityQueue<Tweet> candidates = new PriorityQueue<>();
        Map<Integer,List<Tweet>> selected = new HashMap<>();
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer,PriorityQueue<Tweet>> idol : u.idols.entrySet()) { //先每个作者选一篇最新的
            if (!idol.getValue().isEmpty()) {
                candidates.add(idol.getValue().poll());
            }
        }
        for (int remain = MAX; remain > 0 && candidates.size() > 0; remain--) {
            Tweet newest = candidates.poll();
            int idolId = newest.userId;
            if (!u.idols.get(idolId).isEmpty()) {
                candidates.add(u.idols.get(idolId).poll()); //摘录某偶像的一篇文章，候选列表里马上补上下一篇
            }
            if (!selected.containsKey(newest.userId)) {
                selected.put(idolId,new ArrayList<Tweet>());
            }
            selected.get(idolId).add(newest); //把Tweet对象存起来，准备还给每个作者的原创列表
            result.add(newest.tweetId); //记录结果
        }
        for (Map.Entry<Integer,List<Tweet>> selectedEntry : selected.entrySet()) { //把取出来的文章塞回作者的原创列表
            int selectedIdol = selectedEntry.getKey();
            for (Tweet t : selectedEntry.getValue()) {
                u.idols.get(selectedIdol).add(t);
            }
        }
        for (Tweet candidate : candidates) { //把未入选的候选文章也还回去
            u.idols.get(candidate.userId).add(candidate);
        }
        return result;
    }
    //往粉丝的偶像列表里添加新偶像
    public void follow(int followerId, int followeeId) {
        User fans = confirmUser(followerId);
        User idol = confirmUser(followeeId);
        fans.idols.put(followeeId,idol.posts);
    }
    //从粉丝的偶像列表中删去某个偶像
    public void unfollow(int followerId, int followeeId) {
        if (followerId != followeeId) { //不能取关自己
            User fans = confirmUser(followerId);
            fans.idols.remove(followeeId);
        }
    }

    /** ====================【私有成员】==================== */

    //创建新用户
    private User confirmUser(int userId) {
        if (!users.containsKey(userId)) {
            users.put(userId,new User(userId));
        }
        return users.get(userId);
    }


    private final int MAX = 10; //new feed的大小
    private int time;
    private Map<Integer,User> users;

    private class User {
        private int userId;
        private PriorityQueue<Tweet> posts;
        private Map<Integer,PriorityQueue<Tweet>> idols;

        private User(int userId) {
            this.userId = userId;
            posts = new PriorityQueue<Tweet>();
            idols = new HashMap<Integer,PriorityQueue<Tweet>>();
            idols.put(this.userId,this.posts); //每个人首先都订阅自己的原创文章
        }
    }
    private class Tweet implements Comparable<Tweet> {
        private int tweetId;
        private int userId;
        private int timeStamp;

        private Tweet(int userId, int tweetId) {
            this.userId = userId;
            this.tweetId = tweetId;
            this.timeStamp = ++time;
        }
        public int compareTo(Tweet t) {
            return t.timeStamp - timeStamp; //时间戳越大，文章越新
        }
    }

}
```

#### 结果
![design-twitter-3](/images/leetcode/design-twitter-3.png)


### 不用OO设计，也可以把问题抽象成模拟关系型数据库
不像OO设计思想需要先抽象出`User`和`Tweet`类。关系型数据库只关心数据之间的映射查找关系。

![design-twitter-d](/images/leetcode/design-twitter-d.png)
![design-twitter-e](/images/leetcode/design-twitter-e.png)

下面这个代码看着长，但因为数据关系完整，系统扩展性好。要加什么功能都很方便。

#### 代码
```java
class Twitter {
        /**
         * 构造函数
         */
        public Twitter() {
            userPostsTable = new HashMap<Integer,LinkedList<Tweet>>();
            userNewsFeedTable = new HashMap<Integer,LinkedList<Tweet>>();
            userFollowersTable = new HashMap<Integer,Set<Integer>>();
            userFollowingTable = new HashMap<Integer,Set<Integer>>();
        }
        /**
         * 用户每发布一篇新文章，系统要做3件事，
         *      1. 更新自己的原创文章列表
         *      2. 更新自己的最近提要列表
         *      3. 更新所有粉丝的最近提要列表
         *
         * 假设系统使用的userId和tweetId不会重复，我不做这方面的检查
         */
        public void postTweet(int userId, int tweetId) {
            Tweet tweet = new Tweet(tweetId);
            //更新原创文章
            updatePostsList(userId,tweet);
            //更新最近提要
            updateNewsFeedList(userId,tweet);
            //更新所有粉丝的最近提要
            if (userFollowersTable.containsKey(userId)) {
                for (Integer follower : userFollowersTable.get(userId)) {
                    updateNewsFeedList(follower,tweet);
                }
            }
        }
        /**
         * 返回news feed视图
         */
        public List<Integer> getNewsFeed(int userId) {
            List<Integer> res = new ArrayList<Integer>();
            if (userNewsFeedTable.containsKey(userId)) {
                for (Tweet tweet : userNewsFeedTable.get(userId)) {
                    res.add(tweet.tweetId);
                }
            }
            return res;
        }
        /**
         * 粉丝关注一个偶像，系统会尝试将这个偶像的文章推送到粉丝的最近提要列表
         */
        public void follow(int followerId, int followeeId) {
            boolean newFollowee = false;
            //给粉丝更新偶像列表
            if (!userFollowingTable.containsKey(followerId)) {
                userFollowingTable.put(followerId,new HashSet<Integer>());
            }
            newFollowee = userFollowingTable.get(followerId).add(followeeId);   //已关注的偶像不重复关注
            //给偶像更新粉丝列表
            if (!userFollowersTable.containsKey(followeeId)) {
                userFollowersTable.put(followeeId,new HashSet<Integer>());
            }
            userFollowersTable.get(followeeId).add(followerId);
            //更新粉丝最近提要（重复关注已有偶像不更新）
            if (newFollowee && userPostsTable.containsKey(followeeId)) {
                for (Tweet tweet : userPostsTable.get(followeeId)) {
                    updateNewsFeedList(followerId, tweet);
                }
            }
        }

        /**
         * 粉丝取关一个偶像，他的最近提要列表也会被更新
         */
        public void unfollow(int followerId, int followeeId) {
            boolean unfollowed = false;
            //给粉丝更新偶像列表
            if (userFollowingTable.containsKey(followerId)) {
                unfollowed = userFollowingTable.get(followerId).remove(followeeId);
            }
            //给偶像更新粉丝列表
            if (userFollowersTable.containsKey(followeeId)) {
                userFollowersTable.get(followeeId).remove(followerId);
            }
            if (unfollowed) {
                //先清空所有最近提要
                userNewsFeedTable.put(followerId,new LinkedList<Tweet>());
                //重新添加粉丝自己的原创文章
                if (userPostsTable.containsKey(followerId)) {
                    for (Tweet tweet : userPostsTable.get(followerId)) {
                        updateNewsFeedList(followerId,tweet);
                    }
                }
                //重新添加所有偶像的文章
                if (userFollowingTable.containsKey(followerId)) {
                    for (Integer followee : userFollowingTable.get(followerId)) {
                        if (userPostsTable.containsKey(followee)) {
                            for (Tweet tweet : userPostsTable.get(followee)) {
                                updateNewsFeedList(followerId,tweet);
                            }
                        }
                    }
                }
            }
        }


        /*==================== 【私有成员】 =======================*/

        private final int MAX = 10;                                     //最近提要最大文章数

        //模拟关系型数据库的映射表
        private Map<Integer,LinkedList<Tweet>> userPostsTable;          //用户原创文章列表的集合（无需按时间戳排序）
        private Map<Integer,LinkedList<Tweet>> userNewsFeedTable;       //用户头最近提要列表的集合（按时间戳排序）
        private Map<Integer,Set<Integer>> userFollowersTable;           //用户粉丝列表的集合（去重）
        private Map<Integer,Set<Integer>> userFollowingTable;           //用户关注对象集合（去重）


        //更新不按时间戳排序的原创文章列表
        private void updatePostsList(int userId, Tweet tweet) {
            if (userPostsTable.containsKey(userId) && !userPostsTable.get(userId).contains(tweet)) { //不重复发布同一篇文章
                userPostsTable.get(userId).add(tweet);
            } else {
                userPostsTable.put(userId,new LinkedList<Tweet>(Arrays.asList(new Tweet[]{tweet})));
            }
        }
        //更新按时间戳排序的最近提要列表
        private void updateNewsFeedList(int userId, Tweet tweet) {
            if (userNewsFeedTable.containsKey(userId)) {
                LinkedList<Tweet> newsFeed = userNewsFeedTable.get(userId);
                if (!newsFeed.contains(tweet)) {    //已经有的文章不重新添加
                    boolean updated = false;
                    for (int i = 0; i < newsFeed.size(); i++) {
                        if (newsFeed.get(i).compareTo(tweet) < 0) { newsFeed.add(i,tweet); updated = true; break; } // 时间戳大的在前
                    }
                    if (!updated) { newsFeed.addLast(tweet); }
                    if (newsFeed.size() > MAX) { newsFeed.removeLast(); }
                }
            } else {
                userNewsFeedTable.put(userId,new LinkedList<Tweet>(Arrays.asList(new Tweet[]{tweet})));
            }
        }
        /**
         * 带时间戳的文章对象
         */
        private class Tweet implements Comparable<Tweet> {
            private int tweetId;
            private long timeStamp; //每篇文章都有个时间戳

            private Tweet(int tweetId) {
                this.tweetId = tweetId;
                timeStamp = System.nanoTime();
            }
            //假设tweetId具有唯一性。区分不同的Tweet个体不依赖时间戳。时间戳仅用来按时间排序。
            public boolean equals(Object another) {
                return this.tweetId == ((Tweet)another).tweetId;
            }
            private int hash = -1;
            public int hashCode() {
                if (hash < 0) { //惰性初始化
                    hash = tweetId;
                }
                return hash;
            }
            //以时间戳降序排序，时间戳相同以id升序排序（时间戳越大，文章越新）
            public int compareTo(Tweet another) {
                if (this.timeStamp != another.timeStamp) {
                    return (int) (this.timeStamp - another.timeStamp);
                } else {
                    return this.tweetId - another.tweetId;
                }
            }
            public String toString() {
                return "ID = " + tweetId + ",  Time Stamp = " + timeStamp;
            }
        }


        /** ====================【单元测试专用函数】====================

        //查看某个用户所有的原创文章
        private List<Integer> getPosts(int userId) {
            List<Integer> res = new ArrayList<>();
            if (userPostsTable.containsKey(userId)) {
                for (Tweet tweet : userPostsTable.get(userId)) {
                    res.add(tweet.tweetId);
                }
            }
            return res;
        }
        //打印整个数据库
        private void printDataBase() {
            //原创文章表
            System.out.println("\n>>> User-Posts Table: ");
            System.out.println(userPostsTable);
            //新闻提要表
            System.out.println("\n>>> User-News Feed Table: ");
            System.out.println(userNewsFeedTable);
            //关注表
            System.out.println("\n>>> User-Following Table: ");
            System.out.println(userFollowingTable);
            //被关注表
            System.out.println("\n>>> User-Followers Table: ");
            System.out.println(userFollowersTable);
        }
        ===========================================================*/
}

/**
 * Your Twitter object will be instantiated and called as such:
 * Twitter obj = new Twitter();
 * obj.postTweet(userId,tweetId);
 * List<Integer> param_2 = obj.getNewsFeed(userId);
 * obj.follow(followerId,followeeId);
 * obj.unfollow(followerId,followeeId);
 */
```

#### 结果
![design-twitter-1](/images/leetcode/design-twitter-1.png)

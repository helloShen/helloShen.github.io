---
layout: post
title: "Leetcode - Algorithm - Design Twitter "
date: 2018-08-16 01:08:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","heap","design"]
level: ""
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
首先题目定义的四个函数主要做下面几件事，
![design-twitter-a](/images/leetcode/design-twitter-a.png)

比如有用户`[1,2,3,4,5]`，以及文章以`[1,4],[2,3],[4,7],[2,9],[1,11],[1,2],[5,5],[4,6],[3,10],[1,1],[2,8],[3,12]`的顺序被发布，在没有添加任何`follow`关系的情况下，Twitter数据库看起来像下面这个样子，
![design-twitter-b](/images/leetcode/design-twitter-b.png)

如果加入用户关注关系的话，
* 用户1关注了用户3，4
* 用户2关注了用户5

![design-twitter-c](/images/leetcode/design-twitter-c.png)

### 可以把问题抽象成模拟关系型数据库
不像OO设计思想需要先抽象出`User`类。关系型数据库只关心数据之间的映射查找关系。

![design-twitter-d](/images/leetcode/design-twitter-d.png)
![design-twitter-e](/images/leetcode/design-twitter-e.png)


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


### 可以运用OO思想抽象出`User`类
明天再写。

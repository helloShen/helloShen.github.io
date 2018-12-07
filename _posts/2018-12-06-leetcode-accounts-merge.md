---
layout: post
title: "Leetcode - Algorithm - Accounts Merge "
date: 2018-12-06 22:02:14
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["union find"]
level: "medium"
description: >
---

### 题目
Given a list accounts, each element accounts[i] is a list of strings, where the first element `accounts[i][0]` is a name, and the rest of the elements are emails representing emails of the account.

Now, we would like to merge these accounts. Two accounts definitely belong to the same person if there is some email that is common to both accounts. Note that even if two accounts have the same name, they may belong to different people as people could have the same name. A person can have any number of accounts initially, but all of their accounts definitely have the same name.

After merging the accounts, return the accounts in the following format: the first element of each account is the name, and the rest of the elements are emails in sorted order. The accounts themselves can be returned in any order.

Example 1:
```
Input:
accounts = [["John", "johnsmith@mail.com", "john00@mail.com"], ["John", "johnnybravo@mail.com"], ["John", "johnsmith@mail.com", "john_newyork@mail.com"], ["Mary", "mary@mail.com"]]
Output: [["John", 'john00@mail.com', 'john_newyork@mail.com', 'johnsmith@mail.com'],  ["John", "johnnybravo@mail.com"], ["Mary", "mary@mail.com"]]
Explanation:
The first and third John's are the same person as they have the common email "johnsmith@mail.com".
The second John and Mary are different people as none of their email addresses are used by other accounts.
We could return these lists in any order, for example the answer [['Mary', 'mary@mail.com'], ['John', 'johnnybravo@mail.com'],
['John', 'john00@mail.com', 'john_newyork@mail.com', 'johnsmith@mail.com']] would still be accepted.
```

Note:
* The length of accounts will be in the range [1, 1000].
* The length of accounts[i] will be in the range [1, 10].
* The length of accounts[i][j] will be in the range [1, 30].

### 用`union-find`分组
比如例子中的账号，我先给每个账号一个`id`，
```
id      name/emails
0       ["John", "johnsmith@mail.com", "john00@mail.com"]
1       ["John", "johnnybravo@mail.com"]
2       ["John", "johnsmith@mail.com", "john_newyork@mail.com"]
3       ["Mary", "mary@mail.com"]
```

然后我根据电子邮件建立一个索引，如下所示，`johnsmith@mail.com`邮箱同时被`0`号和`2`号账户使用。说明`[0, 2]`两个账户是同一个用户，
```
mail                    id
johnsmith@mail.com      0, 2
john00@mail.common      0
johnnybravo@mail.com    1
john_newyork@mail.com   2
mary@mail.com           3
```

这时候我建立一个`union-find`数组，
```
[0, 1, 2, 3]
```

现在可以合并`[0, 2]`两个账户。遍历整个邮箱索引，可以合并所有属于同一用户的邮箱。
```
[0, 1, 0, 3]
```

最后根据`union-find`的分组结果来收集账户里的邮箱，注意去重，然后排序即可。


#### 代码
```java
class Solution {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        int size = accounts.size();
        // parse email-accountId map
        Map<String, List<Integer>> mailMap = new HashMap<>();
        for (int accountId = 0; accountId < size; accountId++) {
            List<String> account = accounts.get(accountId);
            for (int i = 1; i < account.size(); i++) {
                String mail = account.get(i);
                if (!mailMap.containsKey(mail)) {
                    mailMap.put(mail, new LinkedList<Integer>());
                }
                mailMap.get(mail).add(accountId);
            }
        }
        // build union-find board based on mailMap
        initUnionFind(size);
        for (Map.Entry<String, List<Integer>> entry : mailMap.entrySet()) {
            List<Integer> accountIds = entry.getValue();
            int rootId = accountIds.get(0);
            for (int i = 1; i < accountIds.size(); i++) {
                union(rootId, accountIds.get(i));
            }
        }
        // collect results from union-find board
        Map<Integer, Set<String>> resultMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            int root = find(i);
            if (!resultMap.containsKey(root)) {
                resultMap.put(root, new HashSet<String>());
            }
            List<String> account = accounts.get(i);
            for (int j = 1; j < account.size(); j++) {
                resultMap.get(root).add(account.get(j));
            }
        }
        List<List<String>> result = new ArrayList<>();
        for (Map.Entry<Integer, Set<String>> entry : resultMap.entrySet()) {
            List<String> mailList = new ArrayList<String>(entry.getValue());
            Collections.sort(mailList);
            mailList.add(0, accounts.get(entry.getKey()).get(0));
            result.add(mailList);

        }
        return result;
    }

    private void initUnionFind(int size) {
        board = new int[size];
        for (int i = 0; i < size; i++) board[i] = i;
    }

    // union-find
    private int[] board;

    /** append group B to group A */
    private void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        board[rootB] = rootA;
    }

    private int find(int a) {
        if (board[a] == a) return a;
        int root = find(board[a]);
        board[a] = root; // path compress
        return root;
    }
}
```

#### 结果
![accounts-merge-1](/images/leetcode/accounts-merge-1.png)

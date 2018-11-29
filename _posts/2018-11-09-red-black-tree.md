---
layout: post
title: "Red-Black Tree"
date: 2018-11-09 00:50:11
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["red black tree", "binary search tree", "tree"]
description: >
---

### 最初的思路
1. 红黑树本质是一棵【二叉搜索树】。
2. 二叉搜索树要做到`lgN`时间内的“搜索”，“插入”，“删除”操作必须做高度平衡。
3. 自平衡二叉搜索树很多，AVL树，2-3树，2-3-4树，都可以。
4. 2-3树，2-3-4树几乎是完美平衡的。
5. 平衡的原因是：2-3树和2-3-4树不是向下生长，而是向上生长的。
6. 最大深度是`lg(N+1)`。`N`是内部节点数，`N+1`是叶节点数。
7. 但2-3树和2-3-4树的效率受到复杂数据结构的影响。
8. 最初的红黑树就是用普通的二叉搜索树模拟2-3-4树的行为，来简化数据结构。
9. 平衡性上，红黑树到每个叶节点的路径都包含相同数量的黑节点。
10. 后来Sedgewick又改良了基于2-3树的红黑树。


### 我们为什么需要二叉搜索树？
红黑树本质上是一棵 **二叉搜索树（Binary Search Tree）**。首先，引入二叉树实际是为了解决传统的`List`或者`Array`在实现“二分查找”时性能上的缺陷。

`Array`和`ArrayList`擅长在`O(1)`时间内随机访问。结合二分查找，可以在有序序列中在`O(lgN)`时间内完成查找任意元素的操作。缺点是，插入新元素的时间复杂度是`O(N)`，因为要将插入元素后的所有元素整个向后平移一个位置。`LinkedList`正好相反，可以在`O(1)`时间完成插入新元素（增加两个链接），但访问某个特定元素必须遍历整个链表，时间复杂度`O(N)`。

二叉搜索树正好继承了`Array`和`LinkedList`两者的优点。即可以在`O(lgN)`时间内完成查找。又可以在`O(1)`时间内插入新元素。

观察一下一棵“满二叉树”（即每一层的节点都达到最大值）的一些数学性质，
```
            6
          /   \
         4     8
        / \   / \
       2   5 7   9
      ||  || ||  ||
      **  ** **  **
```
上面这棵二叉树，一共有`7`个内部节点，`8`个叶节点（都为空），树高`3`层。归纳一下，对于一个有`N`个内部节点的满二叉树，有`N+1`个叶节点，树的高度为`lg(N+1)`。

### 二叉搜索树最大的问题是平衡性
一棵完全随机构成的二叉搜索树，平均查找复杂度是`O(lgN)`。但是在最坏情况下，如果节点都集中在一侧，这样树的深度和他的节点数成正比。查找，插入，删除操作的复杂度因此从`lgN`退化到线性的`N`。
```
     1
      \      
       2
        \
         3
          \
           4
            \
            ...
            ...
```

解决这个问题最直观的办法，就是在构造二叉搜索树的过程中，人为维护二叉树的平衡性。理想的状态是左右两棵子树的树高始终相等，或者再放宽一点，左右两棵子树的高度差不超过1.

### 2-3-4树是红黑树的爸爸，它是一种平衡二叉树
构造平衡二叉树有很多种办法。比如AVL树，2-3树，2-3-4树。这里着重介绍2-3-4树，因为它就是红黑树的爸爸。

2-3-4树的每个节点可以容纳`1~3`个元素。
```
   2节点      3节点        4节点
    1         1|2         1|2|3
   / \       / | \       / | | \
```

和普通二叉搜索树不同，2-3-4树是向上生长的，具体的插入操作核心算法就是一句话：
> 向下查找插入位置的过程中，分解所有遇到的4节点。

具体执行规则很简单，
1. 当父节点是一个2节点的时候，和被向上推送的4节点的中间元素结合成一个3节点。
2. 当父节点是一个3节点的时候，和被向上推送的4节点的中间元素结合成一个4节点。

![234tree-insert](/images/red-black-tree/234tree-insert.png)

下面用一个具体的例子演示各种不同的情况：
![234tree-insert-expand](/images/red-black-tree/234tree-insert-expand.png)

这里面存在一个策略问题，当插入操作的搜索路径上存在多个4节点的时候，向上推送的动作可能需要持续不断地向上推送，直至树的顶部（根）。因此有两种可能的处理方法，
1. 先向下走找到要插入的位置，插入新元素。然后自底向上开始分解4节点。
2. 在向下走的过程中分解每一个遇到的4节点。走到叶节点插入新元素。不需要再向上分解4节点。下次插入操作会自动分解路径上的4节点。

这两种策略都可行。Sedgewick本人更喜欢第二种，逻辑上更清楚。

下图为一棵具体的2-3-4树的生长过程：
![234tree-a](/images/red-black-tree/234tree-a.png)

### 2-3-4树几乎是完美平衡的
通过观察上面的例子，可以直观地看到2-3-4树是向上生长的。每一个节点最多可以容纳3个元素，相当于带有了缓冲。一旦饱和就开始向上推送中间节点。由于额外的元素都相当于被推送到了树的根部，不会单一地向某一测生长，所以保证了树的平衡性。在任何时候2-3-4树都几乎是一棵“完美平衡树（Perfect Balanced Tree）”。

### 用红黑树来简化2-3-4树的实现
2-3-4树理论上可以`lgN`时间内的“搜索”，“插入”，“删除”操作。但需要额外构造`2`节点，`3`节点，`4`节点这些基础数据结构，以及封装对应的操作。这会拖慢2-3-4树的实际效率。

1978年Sedgewick就是想用普通二叉树来模拟2-3-4树的行为才发明的红黑树。他最朴素的想法就是给用不同的颜色来区分一个元素是下层子节点（黑色），还是一个3或者4节点中包含的多个元素（红色）。具体实现只需要在普通二叉搜索树的节点中多加一位`color`字段。
```java
private class Node {
    int val;
    Node left, right;
    boolean color;      // 多加一个“颜色”字段
    Node(int val, boolean color) {
        this.val = val;
        this.color = color;
    }
}
```

对于2-3-4树中的节点，除了一个元素以黑色表示外，其余元素都用红节点表示。因此，
* 一个2节点就是单个黑色节点
* 一个4节点可以用一个黑色的父节点，外加左右各一个红色节点表示。
* 一个3节点可以用一个黑色父节点，加一个红色子节点表示（要么左，要么右）。
![234tree-rbtree-node](/images/red-black-tree/234tree-rbtree-node.png)

根据上面的规则，任意一棵2-3-4树，都有一棵红黑树与之一一对应，两者之间可以互相转换。
![234tree-rbtree-a](/images/red-black-tree/234tree-rbtree-a.png)

如果把所有红节点压平，看成是和对应黑节点同一层的节点，看得更清楚，红黑树就是一棵2-3-4树，只是换了一种表现形式。
![234tree-rbtree-b](/images/red-black-tree/234tree-rbtree-b.png)

但红黑树这种表现形式，不需要额外的`2节点`，`3节点`，`4节点`这样的数据结构。它就是一棵普通的二叉搜索树，只是给每个节点加了一个颜色参数，就可以模拟2-3-4树的全部行为，具有自平衡性。下图为一棵典型的红黑树，外观上他和一棵普通二叉树没有区别。（图中加粗的黑色链接表示一根红链接）。
![red-black-tree-1](/images/red-black-tree/red-black-tree-1.png)

下图是一棵有200个节点的红黑树，
![red-black-tree-2](/images/red-black-tree/red-black-tree-2.png)

所以红黑数完美继承了普通二叉树的简单，以及2-3-4树自平衡的特性。甚至最常用的`search()`操作，红黑树和普通二叉树完全一样，
```java
public Node search(Node root, int n) {
    if (root == null) return null;
    if (root.val < n) return search(root.right, n);
    if (root.val > n) return search(root.left, n);
    return root;
}
```

### 红黑树的插入`insert()`操作
红黑树的插入操作2-3-4树的插入操作基本一致，只有一条规则：就是分解向下查找路径下的所有遇到的4节点。分解的动作也是统一的向上推送4节点的中间元素。

#### 分解4节点就是`flipColor()`操作
分解4节点的基本操作很简单，就是同时变换3个节点的颜色，4节点是一个黑色父节点，带着2个红色子节点。变换颜色之后，变成一个红色父节点，带2个黑色子节点。相当于将中间节点向上推送，原理和2-3-4树一致。我们把这个操作叫做`flipColor()`。
```
[*] -> 黑节点
(*) -> 红节点

         \                  \
        [A]                (A)
        / \        -->     / \
      (B) (C)            [B] [C]
```

#### 分解4节点过程中出现的2条连续红链接
唯一需要注意的是，这个推送动作会导致之前所说的连续两条红链接的情况，
> 2层3个节点之间不允许出现2条连续的红色链接。

![rbtree-dont-allowed](/images/red-black-tree/rbtree-dont-allowed.png)

可以看到，上面的4种情况都不是一个4节点。因为4节点的黑色节点在中间，2个红节点在两边。需要通过“变换颜色（Flip Color）”和“旋转（Rotate）”两种操作将其转换成符合2-3-4树属性的结构。这就是红黑树比较复杂的地方。

在分解4节点向上推送中节点过程中会出现连续2条红链接的情况，具体有以下几种不同的情况（每种对应了左右对称的2种变种）：
1. 父节点是2节点
2. 父节点是3节点，对应3节点中的左链接。
3. 父节点是3节点，对应3节点中的右链接。
4. 父节点是3节点，对应3节点中的中链接。

![234tree-rbtree-operation](/images/red-black-tree/234tree-rbtree-operation.png)

下面把上面2-3-4树插入元素的例子，用红黑树再实现一遍。

前两种情况比较简单，变换一次颜色之后，没有出现两条连续红链接，就直接完成了。
![rbtree-insert-a](/images/red-black-tree/rbtree-insert-a.png)
![rbtree-insert-b](/images/red-black-tree/rbtree-insert-b.png)

第三种情况对应最左边链接，在变换颜色之后，构成了两条连续右侧的红色链接（`zig-zig`），违反了红黑树的基本属性。需要再进行一次右旋转，以满足红黑树属性。
![rbtree-insert-c](/images/red-black-tree/rbtree-insert-c.png)

最后对应中间链接的情况最复杂，变换颜色后，出现一条左侧红链接，一条右侧红链接（`zig-zag`），这种情况需要再旋转2次。先左旋，再右旋。
![rbtree-insert-d](/images/red-black-tree/rbtree-insert-d.png)

#### 正常插入也可能出现2条红链接
还是分为刚才的`zig-zig`和`zig-zag`两种情况。

##### zig-zig
正常按顺序插入`[A, C, E]`，就会出现两条连续的右侧红链接（zig-zig），此时需要一次左旋操作（`rotateLeft()`函数）。
```
        [A]
          \
          (C)
            \
            (E)
```

##### zig-zag
正常按顺序插入`[A, C, B]`，就会出现一条右侧红链接，接一条左侧红链接（zig-zag），此时需要一次右旋（`rotateRight()`函数），再接一次左旋（`rotateLeft()`函数）。
```
        [A]
          \
          (C)
          /
        (B)
```

#### `insert()`总结
所以总的来讲，红黑树的`insert()`操作为了维护正确的2-3-4树的逻辑，只需要做2件事，
1. 用`flipColor()`函数分解4节点。
2. 遇到连续两条红链接的情况，用旋转操作配平。连续红链接分为`zig-zig`和`zig-zag`两种。`zig-zig`只需要旋转一次，`zig-zag`需要旋转两次。

#### 红黑树2条重要性质
1. 从根节点到任意子节点的路径都包含相同数量的黑色节点。
2. 不存在2个连续的红节点。

#### 旋转
再放大看单独一个右旋过程如下，
1. 先找到目标节点（下称`node`）的左子节点（下称`left`），它会是`node`未来的父节点。
2. 先处理外部关系，把`node`的父节点（下称`parent`）和`left`连接。
3. 再把`left`的右子节点（下称`leftRight`）嫁接成为`node`的左子树。
4. 然后再调转`node`和`left`之间的父子关系。把`node`变红，`left`变黑。

整个过程总结起来就是：**“由外而内，自上而下”** 的一个过程。


![right-rotate](/images/red-black-tree/right-rotate.png)

左旋过程如下，
![left-rotate](/images/red-black-tree/left-rotate.png)

下图具体演示了，向红黑树中插入新的`G`元素的全过程。图中加粗的链接为红色链接。
![rbtree-insert-example](/images/red-black-tree/rbtree-insert-example.png)

### 红黑树的删除`delete()`操作
`delete()`和`insert()`一样，底层逻辑也是2-3-4树。从一个`3`节点或`4`节点删除一个元素很简单，可以直接删除。但是从`2`中删除一个元素，会留下一个空链接，这样会破坏树的完美平衡性。

所以`delete()`操作的核心思想就是，
> 从根节点向下，确保当前节点不是`2`节点。

在树的根部时，
1. 如果两个子节点都是`2`节点，合并成一个`4`节点。

比如根节点`Y`是2节点，两个子节点`S`和`Z`都是`2`节点，
![rbtree-delete-root-1](/images/red-black-tree/rbtree-delete-root-1.png)

可以直接合并成一个`4`节点`[S,Y,Z]`，
![rbtree-delete-root-2](/images/red-black-tree/rbtree-delete-root-2.png)

2. 如果`2`子节点的兄弟节点不是`2`节点，可以借一个节点过来。

比如`B`是`2`节点，`[X,Y,Z]`不是，
![rbtree-delete-root-3](/images/red-black-tree/rbtree-delete-root-3.png)

可以向`[X,Y,Z]`借一个`X`过来。当然不是直接拿，要先把`X`推送到根节点，替换`S`。再把根节点的`S`拿过来，
![rbtree-delete-root-4](/images/red-black-tree/rbtree-delete-root-4.png)


在向下搜索过程中遇到`2`节点，有两种处理，
1. 如果当前`2`节点的兄弟节点也是`2`节点，向父辈节点借一个节点合并成一个`4`节点

比如`B`和`S`都是`2`节点，
![rbtree-delete-down-1](/images/red-black-tree/rbtree-delete-down-1.png)

可以向父节点借一个`F`过来，构成`4`节点，
![rbtree-delete-down-2](/images/red-black-tree/rbtree-delete-down-2.png)

2. 如果兄弟节点不是`2`节点，可以向兄弟节点借一个节点。

比如`N`是`2`节点，但兄弟节点`[E,F]`不是，
![rbtree-delete-down-3](/images/red-black-tree/rbtree-delete-down-3.png)

可以向兄弟借一个`F`过来。当然不是直接拿，而是把`F`推送到父节点，替代`H`，然后把父节点中的`H`拿下来，
![rbtree-delete-down-4](/images/red-black-tree/rbtree-delete-down-4.png)

确保了上面这个断言，删除节点只存在两种情况：
1. 需要删除的节点在树的底部，直接删除。

比如要删除`A`，
![rbtree-delete-bottom-1](/images/red-black-tree/rbtree-delete-bottom-1.png)

因为`A`所在节点是`3`节点，且在底部，可以直接删除，
![rbtree-delete-bottom-2](/images/red-black-tree/rbtree-delete-bottom-2.png)

2. 如果不在树的底部，需要将目标节点和它的后继节点交换，把问题转化成再一棵根节点不是`2`节点的子树中删除最小节点。

比如要删除`C`，
![rbtree-delete-inner-1](/images/red-black-tree/rbtree-delete-inner-1.png)

先把`C`替换成后继节点`E`，
![rbtree-delete-inner-2](/images/red-black-tree/rbtree-delete-inner-2.png)

然后再用`deleteMin()`函数，删除它右子树的最小节点`E`，
![rbtree-delete-inner-3](/images/red-black-tree/rbtree-delete-inner-3.png)


下面再完整感受一下，删除下面整棵`2-3-4`树的全过程，
![rbtree-delete-1](/images/red-black-tree/rbtree-delete-1.png)
![rbtree-delete-2](/images/red-black-tree/rbtree-delete-2.png)
![rbtree-delete-3](/images/red-black-tree/rbtree-delete-3.png)
![rbtree-delete-4](/images/red-black-tree/rbtree-delete-4.png)
![rbtree-delete-5](/images/red-black-tree/rbtree-delete-5.png)
![rbtree-delete-6](/images/red-black-tree/rbtree-delete-6.png)
![rbtree-delete-7](/images/red-black-tree/rbtree-delete-7.png)
![rbtree-delete-8](/images/red-black-tree/rbtree-delete-8.png)
![rbtree-delete-9](/images/red-black-tree/rbtree-delete-9.png)
![rbtree-delete-10](/images/red-black-tree/rbtree-delete-10.png)
![rbtree-delete-11](/images/red-black-tree/rbtree-delete-11.png)
![rbtree-delete-12](/images/red-black-tree/rbtree-delete-12.png)
![rbtree-delete-13](/images/red-black-tree/rbtree-delete-13.png)
![rbtree-delete-14](/images/red-black-tree/rbtree-delete-14.png)
![rbtree-delete-15](/images/red-black-tree/rbtree-delete-15.png)


然后红黑树的`delete()`操作就是通过`rotateLeft()`，`rotateRight()`和`flipColor()`函数，实现上面`2-3-4`树删除元素的全部操作。

### 代码
下面是自己写的最简化的红黑树。实现了`search()`和`insert()`接口。
```java
public class RedBlackTree<T extends Comparable> {

    Node<T> root;

    public RedBlackTree() {
        root = null;
    }
    public RedBlackTree(Node<T> root) {
        this.root = root;
    }

    static final boolean RED = false;
    static final boolean BLACK = true;

    static class Node<T> {

        T val;
        Node<T> parent;
        Node<T> left, right;
        boolean color; // true = black, false = red

        Node(T t) {
            val = t;
        }

        /** serialize a single node */
        final String BLACK_LEFT = "[";
        final String BLACK_RIGHT = "]";
        final String RED_LEFT = "(";
        final String RED_RIGHT = ")";
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append((color == BLACK)? BLACK_LEFT : RED_LEFT);
            sb.append(val.toString());
            sb.append((color == BLACK)? BLACK_RIGHT : RED_RIGHT);
            return sb.toString();
        }

    }

    public boolean search(T t) {
        return searchHelper(root, t);
    }

    private boolean searchHelper(Node<T> node, T t) {
        if (node == null) return false;
        if (node.val == t) return true;
        return searchHelper(node.left, t) || searchHelper(node.right, t);
    }

    public void insert(T t) {
        Node<T> newNode = new Node<>(t);
        if (root == null) {
            root = newNode;
            root.color = BLACK;
        } else {
            insertHelper(newNode, root);
        }
    }

    /** assertion: node != null */
    void insertHelper(Node<T> newNode, Node<T> node) {
        checkFourNode(node);
        checkRotation(node);
        if (newNode.val.compareTo(node.val) < 0) {
            if (node.left == null) {
                node.left = newNode;
                newNode.parent = node;
                checkRotation(newNode);
            } else {
                insertHelper(newNode, node.left);
            }
        } else if (newNode.val.compareTo(node.val) > 0) {
            if (node.right == null) {
                node.right = newNode;
                newNode.parent = node;
                checkRotation(newNode);
            } else {
                insertHelper(newNode, node.right);
            }
        }
    }

    /** assertion: node != null */
    void checkFourNode(Node<T> node) {
        if (node.color == BLACK &&
            node.left != null && node.left.color == RED &&
            node.right != null && node.right.color == RED) {
                flipColor(node);
            } else {
            }
    }

    /** assertion: node != null */
    void checkRotation(Node<T> node) {
        Node<T> p = node.parent;
        if (node.color == BLACK || p == null || p.color == BLACK) return;
        Node<T> gp = p.parent;
        if (p == gp.left) {
            if (node == p.left) {
                rotateRight(gp);
            } else {
                rotateLeft(p);
                rotateRight(gp);
            }
        } else {
            if (node == p.right) {
                rotateLeft(gp);
            } else {
                rotateRight(p);
                rotateLeft(gp);
            }
        }
    }

    /**
     * assertion: suppose this is a black node with two red children.
     * Or, we can say a "4 node" in 2-3-4 tree
     */
    void flipColor(Node<T> node) {
        node.color = RED;
        node.left.color = BLACK;
        node.right.color = BLACK;
        if (node == root) node.color = BLACK;
    }

    void rotateRight(Node<T> node) {
        Node<T> left = node.left;
        Node<T> parent = node.parent;
        left.parent = parent;
        if (parent != null) {
            if (parent.left == node) {
                parent.left = left;
            } else {
                parent.right = left;
            }
        }
        Node<T> leftRight = left.right;
        node.left = leftRight;
        if (leftRight != null) leftRight.parent = node;
        left.right = node;
        node.parent = left;
        left.color = node.color;
        node.color = RED;
        if (root == node) root = left;
    }

    void rotateLeft(Node<T> node) {
        Node<T> right = node.right;
        Node<T> parent = node.parent;
        right.parent = parent;
        if (parent != null) {
            if (parent.left == node) {
                parent.left = right;
            } else {
                parent.right = right;
            }
        }
        Node<T> rightLeft = right.left;
        node.right = rightLeft;
        if (rightLeft != null) rightLeft.parent = node;
        right.left = node;
        node.parent = right;
        right.color = node.color;
        node.color = RED;
        if (root == node) root = right;
    }

    /** serialize the whole red black tree */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Node<T>> queue = new ArrayList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            StringBuilder level = new StringBuilder("\n{");
            boolean ignoreThisLevel = true;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node<T> node = queue.remove(0);
                level.append((node == null)? "null" : node.toString());
                if (i + 1 < size) level.append(", ");
                if (node != null) {
                    ignoreThisLevel = false;
                    queue.add(node.left);
                    queue.add(node.right);
                }
            }
            level.append("}");
            if (level.length() > 2 && !ignoreThisLevel) sb.append(level.toString());
        }
        return sb.toString();
    }
}
```

### 基于2-3树的红黑树
晚些时候，Sedgewick又改用2-3树来实现红黑树。区别是节点最多能容纳2个元素，以及3个子节点。基本原理不变，但代码更加简洁。



### 参考资料
* [【普林斯顿大学红黑树讲义】](https://www.cs.princeton.edu/~rs/talks/LLRB/RedBlack.pdf)
* [【Sedgewick本人关于红黑树的讲座】](https://www.coursera.org/lecture/algorithms-part1/2-3-search-trees-wIUNW)
* [【Matt Klassen's DigiPen Math Pages】](https://azrael.digipen.edu/~mmead/www/Courses/CS280/Trees-2-3-4-delete.html)

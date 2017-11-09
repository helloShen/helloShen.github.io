---
layout: post
title: "How Tomcat Works - Chapter 10 - Security"
date: 2017-11-04 21:50:04
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["security"]
description: >
---

![context-security-1](/images/how-tomcat-works-chapter-ten-security/context-security-1.png)

### 框架
Context容器需要关联以下4个组件，
1. `Realm`
2. `Constraint`
3. `LoginConfig`
4. `Authenticator`

其中`Realm`,`constraint`,`LoginConfig`在`Bootstrap1`里配置，最后一个`Authenticator`在`SimpleLoginConfig`里配置。


### `Realm`接口
Realm是储存一个Context容器所有用户名和密码的组件。它和Context容器一一对应。Context容器有一个`Realm`成员字段（可以为空，为空时不加载登录器进行登录），反过来Realm内部也保留着它所从属的Context容器的引用。
```java
public abstract class ContainerBase implements Container, Lifecycle, Pipeline {

    // large number of code omitted ...

    /**
     * The Realm with which this Container is associated.
     */
    protected Realm realm = null;

    // large number of code omitted ...

}
```

另外一个比较别扭的地方是：`Authenticator`的`authenticate()`函数最终会调用和Context容器相关联的`Realm`对象的`authenticate()`方法来实际检查用户名和密码是否匹配。所以检查密码的逻辑不在`Authenticator`里，而是在`Realm`里。



### `LoginConfig`类
`LoginConfig`也是Context容器的一个组件。只起到记录登录配置的功能。`getRealmName()`方法能获得领域对象的名字（字符串）。`getAuthName()`方法能获得`Authenticator`登录器的名字，登录器的名字必须是以下4个名字之一：BASIC, DIGEST,FORM或CLIENT-CERT。

LoginConfig对象内部没有保留Context容器的引用。所以只能通过Context容器找到所属的LoginConfig对象，反过来则找不到。

实际部署中，Tomcat在启动时读取`web.xml`文件的内容。如果`web.xml`文件包含`login-config`元素的配置，Tomcat就会创建一个`LoginConfig`对象。在`com.ciaoshen.howtomcatworks.ex10.startup.Bootstrap1`中手动创建了一个`LoginConfig`实例。

### `Authenticator`接口
实现了`Authenticator`接口的类都是一个 **“登录器”**。顾名思义，它的`authenticate()`方法负责用户名，密码的验证，`accessControl()`方法负责访问权限验证等等。

但`Authenticator`接口只是一个 **“标记型接口”**，也就是接口中没有定义任何方法。包括重要的`authenticator()`和`accessControl()`方法都不是接口的一部分。

抽象类`org.apathe.catalina.authenticator.AuthenticatorBase`抽象类是`Authenticator`接口的基本实现。它同时继承自`org.apache.catalina.valves.ValveBase`类，后者实现了`org.apache.Valve.catalina.Valve`接口，所以它也是一个 **阀**。 所以一般也叫 **“登录器阀”**。它的`invoke()`方法调用了`authenticate()`和`accessControl()`函数。
```java
public abstract class AuthenticatorBase extends ValveBase implements Authenticator, Lifecycle {

    // code omitted ...

}
```

`org.apache.catalina.authenticator`包下有4个`AuthenticatorBase`抽象类的实现类，
![authenticators-1](/images/how-tomcat-works-chapter-ten-security/authenticators-1.png)

无论使用的是哪个类型的登录器，它都是以一个阀的形式添加到容器关联的管道中。然后在Context容器的`start()`方法中，通过`Context#invoke() -> Pipeline#invoke() -> Valve#invoke()`的调用链顺利被启动。而且登陆过程以基础阀的装饰器的身份，在基础阀运行之前被调用，很合理。

### `SecurityConstraint`
`SecurityConstraint`是一条具体的安全策略的抽象。`com.ciaoshen.howtomcatworks.ex10.startup.Boostrap1`里的这段代码，表明访问我的`DocBase`路径下`/Users/Wei/github/HowTomcatWorks/webapps/`的所有资源，必须使用HTTP协议的`GET`方法，并且用户必须拥有`manager`的管理员权限。安全策略的制定还是比较机械化的。
```java
// add constraint
SecurityCollection securityCollection = new SecurityCollection();
securityCollection.addPattern("/");
securityCollection.addMethod("GET");

SecurityConstraint constraint = new SecurityConstraint();
constraint.addCollection(securityCollection);
constraint.addAuthRole("manager");
```

一个Context容器中可以包含多条`SecurityConstraint`，是以一个数组作为Context容器的成员字段的形式绑定到一个容器上。
```java
public class StandardContext extends ContainerBase implements Context {

    // code omitted ...

    /**
     * The security constraints for this web application.
     */
    private SecurityConstraint constraints[] = new SecurityConstraint[0];

    // code omitted ...

}
```
书上关于`SecurityConstraint`怎么生效的没有说名。只说了`BasicAuthenticator`作为一个阀被运行以后，`invoke()`函数会调用`authenticate()`函数进行身份验证。但实际上`authenticate()`函数只负责`[用户名，密码]`的验证。并没有用到之前在`Bootstrap1`中创建的`SecurityConstraint`访问权限的限制。

但例子里又确实进行了访问权限的验证，如果我们输入`[cindy,bamboo]`的用户名和密码，系统提示登录未通过。必须输入`[ken,blackcomb]`才能顺利登录。因为`cindy`只有普通程序员的权限，不具备管理员权限，有管理员权限的用户只有`ken`。说明`SecurityConstraint`里设置的`constraint.addAuthRole("manager")`确实生效了。

实际的访问权限验证过程也是在验证器的`invoke()`函数里被调用的。`BasicAuthenticator`的基类`AuthenticatorBase`类定义了`invoke()`函数，在调用`authenticate()`函数验证用户名和密码之后，有调用了`accessControl()`函数进行用户访问权限验证。
```java
public abstract class AuthenticatorBase
    extends ValveBase
    implements Authenticator, Lifecycle {

    // ... large number of code omitted ...
    // ... large number of code omitted ...
    // ... large number of code omitted ...

    public void invoke(Request request, Response response, ValveContext context)
        throws IOException, ServletException {

        // some code omitted ...

        // Enforce any user data constraint for this security constraint
        if (debug >= 1)
            log(" Calling checkUserData()");
        if (!checkUserData(hrequest, hresponse, constraint)) {
            if (debug >= 1)
                log(" Failed checkUserData() test");
            // ASSERT: Authenticator already set the appropriate
            // HTTP status code, so we do not have to do anything special
            return;
        }

        // Authenticate based upon the specified login configuration
        if (constraint.getAuthConstraint()) {
            if (debug >= 1)
                log(" Calling authenticate()");
            if (!authenticate(hrequest, hresponse, config)) {
                if (debug >= 1)
                    log(" Failed authenticate() test");
                // ASSERT: Authenticator already set the appropriate
                // HTTP status code, so we do not have to do anything special
                return;
            }
        }

        // Perform access control based on the specified role(s)
        if (constraint.getAuthConstraint()) {
            if (debug >= 1)
                log(" Calling accessControl()");
            /**
             * 用户访问权限验证（没有管理员权限的cindy无法通过验证。只有ken能访问资源）
             */
            if (!accessControl(hrequest, hresponse, constraint)) {
                if (debug >= 1)
                    log(" Failed accessControl() test");
                // ASSERT: AccessControl method has already set the appropriate
                // HTTP status code, so we do not have to do anything special
                return;
            }
        }

        // some code omitted ...

    }

    // some code omitted ...
}
```

### `Bootstrap1.java`
`com.ciaoshen.howtomcatworks.ex10.startup.Bootstrap1`先做准备工作，为Context容器创建一系列必要组件，
```
org.apache.catalina.core.StandardContext
    |
    +-> org.apache.catalina.deploy.SecurityConstraint
    |  |
    |  +-> org.apache.catalina.deploy.SecurityCollection
    |
    +-> org.apache.catalina.deploy.LoginConfig
    |
    +-> com.ciaoshen.howtomcatworks.ex10.realm.SimpleRealm
```

然后调用Context容器的`start()`函数触发`START_EVENT`事件，因此`SimpleContextConfig`的`lifecycleEvent()`函数被触发。经过一系列调用，最终创建了一个`org.apache.catalina.authenticator.BasicAuthenticator`实例，并以一个`Valve`的身份，加入到了Context的`Pipeline`管道中。
```
org.apache.catalina.core.StandardContext#start()
    |
    +-> Lifecycle.START_EVENT
        |
        +-> com.ciaoshen.howtomcatworks.ex10.core.SimpleContextConfig#lifecycleEvent()
        |
        +-> com.ciaoshen.howtomcatworks.ex10.core.SimpleContextConfig#authenticatorConfig()
```

之后`org.apache.catalina.authenticator.BasicAuthenticator`就会作为Context容器的一个阀被运行。之后的过程就和第五章里描述的调用链相同，总之最终它的`invoke()`函数被调用，开始了安全验证的过程，
```
org.apache.catalina.authenticator.BasicAuthenticator#apacheinvoke()
    |
    +-> org.apache.catalina.authenticator.BasicAuthenticator#authenticate()
    |
    +-> org.apache.catalina.authenticator.BasicAuthenticator#accessControl()
```

`SimpleRealm`类用了一个最简单的内嵌类`User`封装用户信息。实际的Tomcat框架里有更丰富和健壮的`User`类：`org.apache.catalina.users.MemoryUser`。
```java
/** 最基本的对用户[用户名，密码]对的抽象 */
class User {

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String username;
  public ArrayList roles = new ArrayList();
  public String password;

  public void addRole(String role) {
    roles.add(role);
  }
  public ArrayList getRoles() {
    return roles;
  }
}
```

然后用户的信息也尽可能从简，直接从`createDatabase()`函数里硬编码，只是为了演示`Realm`类是怎么工作的。
```java
private void createUserDatabase() {
  User user1 = new User("ken", "blackcomb");
  user1.addRole("manager");
  user1.addRole("programmer");
  User user2 = new User("cindy", "bamboo");
  user2.addRole("programmer");

  users.add(user1);
  users.add(user2);
}
```

### `Bootstrap2.java`
`Bootstrap2`和`Bootstrap1`最大的区别是`Realm`对象。`Bootstrap2`使用了`com.ciaoshen.howtomcatworks.ex10.realm.SimpleUserDatabaseRealm`作为领域的实例。`SimpleUserDatabaseRealm`的最大特点是用`org.apache.catalina.users.MemoryUserDatabase`作为内存中储存用户信息的数据结构。`MemoryUserDatabase`实现了`org.apache.catalina.UserDatabase`接口。 具体是由`SimpleUserDatabaseRealm#createUserDatabase()`函数负责创建创建`org.apache.catalina.users.MemoryUserDatabase`。

#### `MemoryUserDatabase`
`org.apach.catalina.users.MemoryUserDatabase`最大的特点是可以从一个本地XML文件读取持久化的用户信息。所以它的一个重要属性是`pathname`，用来设置记录用户登录信息的XML文件的具体位置。这个路径可以是个绝对路径，也可以是个相对`catalina.base`的路径。
```java
/**
 * The relative (to <code>catalina.base</code>) or absolute pathname to
 * the XML file in which we will save our persistent information.
 */
protected String pathname = "conf/tomcat-users.xml";
```

如果不人为设置，硬编码的默认位置如下，是一个以`catalina.base`为基的相对路径，可见用户名和密码可以是跨应用通用的，
> [catalina.base]/conf/tomcat-users.xml

`Bootstrap2`调用`SimpleUserDatabaseRealm#createUserDatabase()`函数又手动设置一遍`conf/tomcat-users.xml`，这步其实可以省略。总之最终用户名密码数据文件在我系统上的绝对路径为：
> /Users/Wei/github/HowTomcatWorks/webapps/conf/tomcat-users.xml

一个用户的所有信息被抽象成一个`org.apache.catalina.User`对象。`MemoryUserDatabase`的用户信息被储存在一个`HashMap<User>`里。这里的`User`就不是一个简单的玩具内嵌类，而是健壮的`org.apache.catalina.users.MemoryUser`类的实例。
```java
/**
 * The set of {@link User}s defined in this database, keyed by
 * user name.
 */
protected HashMap users = new HashMap();
```
可以通过用户名查找用户，
```java
public User findUser(String username) {

    synchronized (users) {
        return ((User) users.get(username));
    }
}
```

`MemoryUserDatabase#open()`函数从`CATALINA_BASE/conf/tomcat-users.xml`文件读取预设用户信息。一个`Digester`实例会被创建，用来解析XML文件。读取出的用户信息被封装在一系列`User`对象中。
```java
/**
 * Initialize access to this user database.
 *
 * @exception Exception if any exception is thrown during opening
 */
public void open() throws Exception {

    synchronized (groups) {
        synchronized (users) {

            // Erase any previous groups and users
            users.clear();
            groups.clear();
            roles.clear();

            // Construct a reader for the XML input file (if it exists)
            File file = new File(pathname);
            if (!file.isAbsolute()) {
                file = new File(System.getProperty("catalina.base"),
                                pathname);
            }
            if (!file.exists()) {
                return;
            }
            FileInputStream fis = new FileInputStream(file);

            // Construct a digester to read the XML input file
            Digester digester = new Digester();
            digester.addFactoryCreate
                ("tomcat-users/group",
                 new MemoryGroupCreationFactory(this));
            digester.addFactoryCreate
                ("tomcat-users/role",
                 new MemoryRoleCreationFactory(this));
            digester.addFactoryCreate
                ("tomcat-users/user",
                 new MemoryUserCreationFactory(this));

            // Parse the XML input file to load this database
            try {
                digester.parse(fis);
                fis.close();
            } catch (Exception e) {
                try {
                    fis.close();
                } catch (Throwable t) {
                    ;
                }
                throw e;
            }

        }
    }

}
```

书中的应用程序自带的`conf/tomcat-users.xml`文件的内容如下，
```xml
<?xml version='1.0' encoding='utf-8'?>
<tomcat-users>
  <role rolename="tomcat"/>
  <role rolename="role1"/>
  <role rolename="manager"/>
  <role rolename="admin"/>
  <user username="tomcat" password="tomcat" roles="tomcat"/>
  <user username="role1" password="tomcat" roles="role1"/>
  <user username="both" password="tomcat" roles="tomcat,role1"/>
  <user username="admin" password="password" roles="admin,manager"/>
</tomcat-users>
```

由于在`Bootstrap2`中设定只有“管理员(manager)”才能访问`Primitive`和`Modern`应用，所以最终只有`[admin,password]`这一组用户名和密码能顺利登陆。
```java
constraint.addAuthRole("manager");
```

#### `SimpleUserDatabaseRealm`怎么利用`MemoryUserDatabase`检查用户名和密码？
前面说了，`org.apache.catalina.authenticator.BasicAuthenticator#authenticate()`函数最终会调用和Context容器关联的Realm的`authenticate()`函数来检查用户输入的用户名和密码是否正确。**实际的用户名，密码匹配过程封装在`Realm`实例中**。
```java
principal = context.getRealm().authenticate(username, password);
```

下面是`com.ciaoshen.howtomcatworks.ex10.realm.SimpleUserDatabaseRealm`的`authenticate()`函数的代码。简单说分3步走，
1. 检查用户名和密码是否匹配
2. 如果用户名和密码正确匹配，开始收集用户所有角色信息
3. 最后将用户名，密码，角色信息封装成`java.security.Pricipal`对象，并返回给调用者

检查用户名和密码的时候，先会从`MemoryUserDatabase`里根据用户名查找到目标用户，然后密码会先被编码成十六进制字符串，然后进行比较。
```java
public Principal authenticate(String username, String credentials) {
  // Does a user with this username exist?
  User user = database.findUser(username);
  if (user == null) {
    return (null);
  }

  // Do the credentials specified by the user match?
  // FIXME - Update all realms to support encoded passwords
  boolean validated = false;
  if (hasMessageDigest()) {
    /** 实际检查用户名和密码是否匹配。 密码会先被编码成十六进制字符串，然后进行比较 */
    // Hex hashes should be compared case-insensitive
    validated = (digest(credentials).equalsIgnoreCase(user.getPassword()));
  }
  else {
    validated = (digest(credentials).equals(user.getPassword()));
  }
  if (!validated) {
    return null;
  }

  /** Collect user's roles name(ArrayList<String>) */
  ArrayList combined = new ArrayList();
  Iterator roles = user.getRoles();
  while (roles.hasNext()) {
    Role role = (Role) roles.next();
    String rolename = role.getRolename();
    if (!combined.contains(rolename)) {
      combined.add(rolename);
    }
  }
  Iterator groups = user.getGroups();
  while (groups.hasNext()) {
    Group group = (Group) groups.next();
    roles = group.getRoles();
    while (roles.hasNext()) {
      Role role = (Role) roles.next();
      String rolename = role.getRolename();
      if (!combined.contains(rolename)) {
        combined.add(rolename);
      }
    }
  }

  /** construct un Priciple with username, password, and roles */
  return (new GenericPrincipal(this, user.getUsername(),
    user.getPassword(), combined));
}
```

### 安全架构总览
第五章的时候，画了一个Context容器的架构图。Context容器最主要的组件是一条由多个`Valve`阀连接成的`Pipeline`管道。另外内部还包含`Loader`和`Mapper`组件，它们和Context对象，一对一互相关联。它们的实例作为Context容器的成员字段，并且它们自身实例的内部同时保留了外部Context容器的引用，能够互相找到对方。

到了这一章，首先`Authenticator`作为`Pipeline`管道中的一个`Valve`阀，像管道中所有其他阀一样依次被启动，引导整个安全检查过程。检查过程中需要用到的三组信息：
* [用户名,密码]: 封装在一个`Realm`实例中
* 访问权限及其他安全策略: 封装在一组`SecurityConstraint`实例构成的数组中
* 登录器名，领域名等一些元信息: 封装在一个`LoginConfig`实例中

其中只有`Realm`和之前的`Loader`和`Mapper`一样有着相对独立的地位，在作为Context一个组件存在的同时，内部也保留了Context实例的引用，能互相找到对方。而`SecurityConstraint`数组和`LoginConfig`对象和Context的关系没那么平等，Context能看到它们，但它们没有保留Context的引用，看不到Context。
![context-security-1](/images/how-tomcat-works-chapter-ten-security/context-security-1.png)

一句话总结Tomcat的安全架构就是：
> Authenticator是安全验证的总导演，当它需要某些数据的时候就向它所处的Context容器要。Context容器就再到自己内部组件中去找。用户名密码到Realm里去找，其他安全策略到SecurityConstraint里找，登录器名称等元信息到LoginConfig里去找。

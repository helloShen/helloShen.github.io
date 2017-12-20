---
layout: post
title: "How JSP Works"
date: 2017-12-15 22:20:15
author: "Wei SHEN"
categories: ["java","web"]
tags: ["jsp"]
description: >
---

![jsp-processing](/images/how-jsp-works/jsp-processing.jpg)

### 一张图看懂JSP是怎么工作的
以下步骤表明了 Web 服务器是如何使用JSP来创建网页的：
1. 就像其他普通的网页一样，您的浏览器发送一个 HTTP 请求给服务器。
2. Web 服务器识别出这是一个对 JSP 网页的请求，并且将该请求传递给 JSP 引擎。通过使用 URL或者 .jsp 文件来完成。
3. JSP 引擎从磁盘中载入 JSP 文件，然后将它们转化为 Servlet。这种转化只是简单地将所有模板文本改用 println() 语句，并且将所有的 JSP 元素转化成 Java 代码。
4. JSP 引擎将 Servlet 编译成可执行类，并且将原始请求传递给 Servlet 引擎。
5. Web 服务器的某组件将会调用 Servlet 引擎，然后载入并执行 Servlet 类。在执行过程中，Servlet 产生 HTML 格式的输出并将其内嵌于 HTTP response 中上交给 Web 服务器。
6. Web 服务器以静态 HTML 网页的形式将 HTTP response 返回到您的浏览器中。
最终，Web 浏览器处理 HTTP response 中动态产生的HTML网页，就好像在处理静态网页一样。

关键就在于第3步，如果对Servlet了解的话，JSP一下子就懂了：JSP文件会被翻译成Servlet，然后执行。然后Servlet这边就是通过Response（输出流）动态生成并返回HTML页面。一句话总结的很好，
> JSP是Servlet的扩展。就是用一种机器能懂的方式写Servlet。然后这种方式本身又能很好地嵌入到HTML标签中。

### 一个简单的例子
比如一个`test1.jsp`文件，
```html
<!-- 表明此为一个JSP页面 -->
<%@ page contentType="text/html; charset=gb2312" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
    <HEAD>
          <TITLE>第一个JSP页面</TITLE>
    </HEAD>

    <BODY>
    <!-- 下面是Java脚本-->
        <%for(int i = 0 ; i < 10; i++)
          {
            out.println(i);
        %>
       <br>
        <%}
        %>
    </BODY>
</HTML>
```

经过编译后生成的Servlet文件：`test1_jsp.java`
```java
package org.apache.jsp;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

//继承HttpJspBase类，该类其实是个HttpServlet的子类(jasper是tomcat的jsp engine)
public final class test1_jsp extends org.apache.jasper.runtime.HttpJspBase
         implements org.apache.jasper.runtime.JspSourceDependent
    {
         private static java.util.Vector _jspx_dependants;
         public java.util.List getDependants() {
             return _jspx_dependants;
         }
         //用于响应用户的方法
         public void _jspService(HttpServletRequest request,
            HttpServletResponse response)
            throws java.io.IOException, ServletException
         {   //built-in objects(variavles) are created here.
             //获得页面输出流
             JspFactory _jspxFactory = null;
             PageContext pageContext = null;
             HttpSession session = null;
             ServletContext application = null;
             ServletConfig config = null;
             //获得页面输出流
             JspWriter out = null; //not PrintWriter. JspWriter is buffered defautly.
             Object page = this;
             JspWriter _jspx_out = null;
             PageContext _jspx_page_context = null;
             //开始生成响应
             try
             {
                 _jspxFactory = JspFactory.getDefaultFactory();
                //设置输出的页面格式
            response.setContentType("text/html; charset=gb2312");
            pageContext = _jspxFactory.getPageContext(this, request,
            response, null, true, 8192, true);
            _jspx_page_context = pageContext;
            application = pageContext.getServletContext();
            config = pageContext.getServletConfig();
            session = pageContext.getSession();
            //页面输出流

           out = pageContext.getOut();
            _jspx_out = out;
            //输出流，开始输出页面文档
            out.write("rn");
            //下面输出HTML标签
            out.write("<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0
            Transitional//EN">rn");
            out.write("<HTML>rn");
            out.write("<HEAD>rn");
            out.write("<TITLE>first Jsp</TITLE>rn");
            out.write("</HEAD>rn");
            out.write("<BODY>rn");
            //页面中的循环，在此处循环输出
            for(int i = 0 ; i < 10; i++)
            {
             out.println(i);
             out.write("rn");
             out.write("<br>rn");
            }
            out.write("rn");
            out.write("</BODY>rn");
            out.write("</HTML>rn");
            out.write("rn");
           }
           catch (Throwable t)
           {
            if (!(t instanceof SkipPageException))
            {
               out = _jspx_out;
               if (out != null && out.getBufferSize() != 0)
                 out.clearBuffer();
               if (_jspx_page_context != null) _jspx_page_context.handle
               PageException(t);
            }
          }
           finally
         {
           if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_
           page_context);
        }
      }
}
```

JSP向Servlet会做如下转换：
* JSP页面的静态内容, JSP脚本都会转换成Servlet的xxxService()方法，类似于自行创建Servlet时service()方法。    
* JSP声明部分，转换成Servlet的成员部分。所有JSP声明部分可以使用private,protected,public,static等修饰符，其他地方则不行。    
* JSP的输出表达式(<%= ..%>部分)，输出表达式会转换成Servlet的xxxService()方法里的输出语句。    
* 九个内置对象要么是xxxService()方法的形参，要么是该方法的局部变量，所以九个内置对象只能在JSP脚本和输出表达式中使用。// 不能在jsp Declaration中使用

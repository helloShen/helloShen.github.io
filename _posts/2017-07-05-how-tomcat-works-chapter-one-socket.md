---
layout: post
title: "[Note] How Tomcat Works - Chapter 1 - java.net.Socket"
date: 2017-07-05 21:17:38
author: "Wei SHEN"
categories: ["java","web","how tom cat works"]
tags: ["http","socket"]
description: >
---

### `java.net.Socket`
面向连接
### **`java.net.Socket`** 类的一个最简单Demo
下面的代码，演示了怎么用`java.net.Socket`和本地服务器通话。
```java
/**
 * 演示java.net.Socket类最简单的应用，
 *      1. 向本地Http Server发送Request
 *      2. 从本地Http Server接收Response，并打印在Console
 */
package com.ciaoshen.howtomcatworks.ex01;
import java.util.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

/** 包不可见组件 */
class HttpServerConnector {
    /** 需要修改参数，直接在这里修改。为了读起来简明，就不写POJO了. */
    private String host = "127.0.0.1";
    private int port = 80;
    private String path = "/index.jsp";

    /** 构造器允许设置最简单的 Host 和 Port 参数 */
    private HttpServerConnector() {}
    private HttpServerConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }
    /** 可以设置访问目标页面 */
    private void lookfor(String path) {
        this.path = path;
    }

    /**
     * “连接”的本质就是一个Socket对象，和一对IO流：
     *   Socket ->
     *      1. OutputStream
     *      2. InputStream
     */
    private Socket socket = null; // !!!核心!!!
    private PrintWriter out = null; // 封装好的OutputStream
    private BufferedReader in = null; // 封装好的InputStream

    /**
     * Socket和OutputStream以及InputStream构建的过程。
     * 单独拎出来，看得更清楚。
     * 调用后面的connect()函数之前，必须先调用这个init()
     */
    private void init(){
        try {
            socket = new Socket(host,port);
            OutputStream os = socket.getOutputStream();
            boolean autoflush = true;
            out = new PrintWriter(os,autoflush);
            InputStream is = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));
        } catch (UnknownHostException uhe) {
            System.out.println(uhe);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    /**
     * Socket通过OutputStream和InputStream，和Http Server通话的过程。
     */
    private void connect() {
        // 向Http Server发送Request
        out.println("GET " + path + " HTTP/1.1");
        out.println("Host: " + host + ":" + port);
        out.println("Connection: Close");
        out.println();

        // 从Http Server接收Response
        boolean loop = true;
        StringBuffer sb = new StringBuffer(8096);
        while (loop) {
            try {
                if (in.ready()) {
                    int i = 0;
                    while (i != -1) {
                        i = in.read();
                        sb.append((char)i);
                    }
                    loop = false;
                }
                Thread.currentThread().sleep(50);
            } catch (IOException ioe) {
                System.out.println(ioe);
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
        }

        // 在Console打印收到的消息
        System.out.println(sb.toString());
    }
    /**
     * 断开连接
     */
    private void close() {
        try {
            socket.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
    public static void main(String[] args) {
        HttpServerConnector connector = new HttpServerConnector();
        connector.init();       // 连接前初始化Socket, OutputStream, InputStream
        connector.connect();    // 发送Request，接收Response
        connector.close();      // 断开连接
    }
}
```

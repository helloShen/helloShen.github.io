---
layout: post
title: "RMI Hello World Demo(draft)"
date: 2018-01-03 18:10:27
author: "Wei SHEN"
categories: ["java","web"]
tags: ["rmi","j2ee","serialization","distributed"]
description: >
---

### 文件结构
```
ROOT/
    |
    +-> server/
    |         |
    |         +-> src/
    |         |      |
    |         |      +-> com/ciaoshen/masteringejb/rmidemo/server/FileInterface.java
    |         |      |
    |         |      +-> com/ciaoshen/masteringejb/rmidemo/server/FileImpl.java
    |         |      |
    |         |      +-> com/ciaoshen/masteringejb/rmidemo/server/FileServer.java
    |         |
    |         +-> bin/
    |         |      |
    |         |      +-> com/ciaoshen/masteringejb/rmidemo/server/FileInterface.class
    |         |      |
    |         |      +-> com/ciaoshen/masteringejb/rmidemo/server/FileImpl.class
    |         |      |
    |         |      +-> com/ciaoshen/masteringejb/rmidemo/server/FileServer.class
    |         |      |
    |         |      +-> com/ciaoshen/masteringejb/rmidemo/server/FileImpl_Stub.class
    |         |
    |         +-> sh/
    |         |     |
    |         |     +-> generate-stub.sh
    |         |     |
    |         |     +-> run-registry.sh
    |         |     |
    |         |     +-> run-server.sh
    |         |
    |         +-> META-INF/
    |                     |
    |                     +-> policy.txt
    |                     |
    |                     +-> targetTestFile.txt
    |
    +-> client/
    |         |
    |         +-> src/
    |         |      |
    |         |      +->  com/ciaoshen/masteringejb/rmidemo/client/FileClient.java
    |         |
    |         +-> bin/
    |         |      |
    |         |      +->  com/ciaoshen/masteringejb/rmidemo/client/FileClient.class
    |         |
    |         +-> sh/
    |         |     |
    |         |     +->  run-client.sh
    |         |
    |         +-> lib/
    |                |          
    |                +-> com/ciaoshen/masteringejb/rmidemo/server/FileInterface.class
    |
    +-> run-registry.sh
    |
    +-> README.txt
```

### 待续

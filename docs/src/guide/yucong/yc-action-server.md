# yucong action server

功能比较简单，给 yucong 项目一些 function 的支持。

java 语言开发，主要框架 springboot 3 + mybatisplus， mysql 存储，redis 作缓存及消息队列用。

### Feature

1. 更丰富的功能

2. 其他功能

### Release Note.

###### v1.0.0 / 2023-12-08

1. 基础功能

### 启动

```angular2html
1. 执行 sql， sql/20231016/yucong.sql
2. 配置文件根据自己的环境配置
3. 启动 Spring
```

###### 说明

方法调用，查找存储中配置的 class/groovy 方法和对应的文件，执行并返回。
如果配置了 groovy scripts，默认存放的位置 /home/scripts。项目启动时，根据存储中的 groovy 记录，会读取 resources/scripts
下的文件，并放到 /home/scripts 下。

### http

1. 保存 groovy script

```http request
POST http://localhost:8180/yc/function/groovy/save
Content-Type: application/json

{
  "functionName": "get_current_weather",
  "groovyName": "Weather.groovy",
  "groovyUrl": "/home/scripts/weather/",
  "userName": "yao"
}
```

2. 上传 groovy script 文件到 groovy url 

```http request
POST http://localhost:8180/yc/function/groovy/scripts/upload
Content-Type: application/x-www-form-urlencoded

```

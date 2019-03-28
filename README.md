# Grails Rest Seed

grails rest-api的模板工程，其脱胎于实际的项目经验，集合了我们Grails项目的初始工具包和插件：
- 数据库
  - postgresql-extensions
  - Hikari连接池
  - postgresql jdbc driver
  - Java 8日期类型支持：grails-java8和hibernate-java8
- 安全
  - spring-security-core
  - spring-security-rest
- Servlet容器
  - spring-boot-starter-undertow
- Migration
  - database-migration
  - liquibase-core
- build信息
  - gradle-git-properties
- 阿里云OSS

## 基本配置
- 日志
  - 应用滚动日志。
  - 独立的SQL调试日志，需设置“DEBUG_SQL”环境变量。
- 数据库和GORM
  - 启用Hikari连接池。
  - 为Domain Class的id缺省启用PG的sequence。
  - 采用实际DB作为测试基准库，避免因测试和实际DB不一致而引发的意外。
  - 包含了一个示例类，用于展示如何使用PG的丰富类型（jsonb和数组）和java 8日期类型。并且还包括了对应的测试代码。
- 安全
  - Spring Security和Spring Security Rest配置。
  - User、Role、UserRole初始化。
  - 记录登录历史。
  - 口令加密，采用默认的“bcrypt”算法，其已经具备salt功能。
  - 自定义Login返回的JSON。
- Build
  - 引入gradle-git-properties后，可以通过`actuator`暴露git的相关信息，比如`commit id`, `commit message`, `commit time`等等相关信息
  - 该插件会在classpath中产生一个`git.properties`文件
  - 通过调用`GitProperties`这个class可以打印出类似于如下的日志: `2018-08-23 18:34:55.265  INFO BootStrap                        : Application running at commit: 47f5f5a, branch: master, commit time: Mon Aug 20 22:50:05 CST 2018, build time: Thu Aug 23 18:26:56 CST 2018`
  - 如果希望启用`actuator`，需要在`application.yml`中设置`endpoints.info.enable: true`
- Json View
  - 缺省采用`deep`策略，但激活它有个前提：相应的关联需要被初始化，即在mapping中采用`fetch: join`。（注意：对单端关联可以采用这一策略，但对于多端，不建议。）
- 服务端签名后直传阿里云OSS，见[此链接](https://help.aliyun.com/document_detail/31926.html)。
  - 即后端传给前端授权码，之后由前端直接上传OSS，不经过后端。
  - 修改application.yml中【aliyun/oss】部分。

## 主要功能
- 用户管理
  - 普通用户：注册、修改密码、重置密码、验证码
  - 管理员：用户管理、冻结、解冻、重置密码、查询
- 异步任务队列，并且已经内置的任务有：
  - 短信通知

## 使用指南

  1. git clone
  1. 修改相应的包名，目前包的根为：top.dteam.earth.backend
  1. 自由发挥

### 异步任务队列

异步任务队列需要配合**clock**使用，只需将任务内容放入**Job** Domain Class中即可，值得注意之处：
- 每个任务有相应的Topic，它可视为job的分类。
- 任务支持回调，需要回调的任务需要在job的body中指明callback属性
- 任务支持优先级，最小为1，最大为10，越大优先级越高
- 目前每个任务的重试次数为3次

插入任务之后，clock会负责执行，关于每个Topic的属性，如delay、重试次数等，都由clock的配置决定。目前为统一配置。

目前基于这个设计重新实现了SMS，开发者可自行参考。若某类job的body有格式要求，可参考SMS Topic的实现。

CALLBACK主题任务由clock负责创建，并且优先级最高。

### 数据库

使用下面的命令初始化数据库和项目用户：
~~~
./create_db.sh 数据库名字
~~~
缺省创建规则（若数据库名字为db）：
- 产品库：db
- 测试库：db_test
- 用户名和密码：db_admin/admin
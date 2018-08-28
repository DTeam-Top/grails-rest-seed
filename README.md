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
  - 通过调用`GitProperties`这个class可以打印出类似于如下的日志: `2018-08-23 18:34:55.265  INFO backend.BootStrap                        : Application running at commit: 47f5f5a, branch: master, commit time: Mon Aug 20 22:50:05 CST 2018, build time: Thu Aug 23 18:26:56 CST 2018`
  - 如果希望启用`actuator`，需要在`application.yml`中设置`endpoints.info.enable: true`

## 使用指南

  1. git clone
  1. 修改相应的包名，目前包的根为：top.dteam.earth.backend
  1. 自由发挥

使用下面的命令初始化数据库和项目用户：
~~~
./create_db.sh 数据库名字
~~~
缺省创建规则（若数据库名字为db）：
- 产品库：db
- 测试库：db_test
- 用户名和密码：db_admin/admin
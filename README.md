# 怎样博客

## 主要技术栈
JDK 21、Spring Boot 4.0.9、Spring Cloud 2025.1.4、Spring Cloud Alibaba 2025.1.0.0；MySQL、Redis、Kafka、Elasticsearch HTTP API。依赖版本由根 POM 和官方 BOM 统一管理。

升级范围、版本依据和后续验证事项见 [JDK 21 升级记录](doc/upgrade-java21.md)。

## 数据库初始化

在 MySQL 中执行 `doc/sql/how-blog.sql`，创建 `how-blog` 数据库和全部 16 张表。文章评论、吐槽分别存储在 `tb_comment`、`tb_spit`，不再需要 MongoDB。

脚本只创建不存在的库表，不删除数据，也不导入测试数据。文章服务和用户服务共用 `how-blog`；数据库地址、账号和密码已直接填写在 Nacos YAML 中，可直接编辑调试。

## Nacos 与本地应用启动

NAS 使用 r-nacos v0.8.8，部署见 [部署说明](doc/deploy/nacos/README.md)，配置和用户持久化在 `/home/ubuntu/nacos/data`；原 MySQL `nacos_config` 保留供回退。业务应用连接 `how-blog`。应用现使用 Spring Boot 4.0 / Spring Cloud 2025.1，Nacos Client 3.1.1 由 Alibaba BOM 管理。新客户端与 r-nacos 的联调待后续统一测试。

1. 在 NAS 启动 r-nacos，打开内网无鉴权控制台 `http://192.168.20.5:8848/rnacos/`。应用使用 `192.168.20.5:8848`，本机需要能访问 NAS 的 `8848` 和 `9848` 两个端口。`10848/rnacos/` 另提供需要登录的控制台。
2. 启动升级后的应用前，在 public 命名空间逐份发布或更新 [doc/nacos-public](doc/nacos-public) 中的 5 个配置。Data ID 使用完整文件名，例如 `how-article-dev.yaml`；Group 为 `DEFAULT_GROUP`，格式为 YAML，内容原样复制。文件已填写当前内网连接信息和凭据。`doc/nacos-dev` 提供相同配置，可选用其中一套。
3. 五个模块的 `src/main/resources/application.properties` 已直接填写 Nacos 地址 `192.168.20.5:8848`、账号密码、public 命名空间、`DEFAULT_GROUP` 和 `dev` 配置，无需在 IDE 设置环境变量或加载 `app.env`。如果改用其他命名空间，直接修改这些文件的 namespace ID 和配置导入地址，并将业务 YAML 发布到对应命名空间。
4. 将 IDE 的项目 SDK、Maven Runner 和各启动项 JRE 均设为 JDK 21，再运行各模块的启动类。可以先启动文章服务验证 MySQL/Redis，再按需启动用户、网关、搜索和短信服务；用户服务还需要 Kafka，搜索需要 Elasticsearch，短信需要 Kafka 和阿里云短信配置。首次使用 Kafka 时先启动短信服务以创建 topic。

从后端根目录构建（只编译生产源码，不执行测试）：

```powershell
# JAVA_HOME 指向本机 JDK 21，且将其 bin 加入当前终端 PATH。
.\mvnw.cmd -B -ntp compile
```

Linux / macOS 使用 `bash ./mvnw -B -ntp compile`。Wrapper 固定 Maven 3.9.16，并校验下载包 SHA-256。完整打包时使用 `package`；暂缓测试时可显式传入 `-DskipTests`。构建强制要求 JDK 21 和 Maven 3.9.x。

Windows / IntelliJ IDEA 中，直接运行下表中的 Application 启动类即可。中间件配置修改位置为 `doc/nacos-public` 或 `doc/nacos-dev`，编辑后需发布到 Nacos；Nacos 连接信息直接修改各模块的 `application.properties`。`doc/deploy/app.env.example` 仅供查看当前连接值，应用不再依赖该文件。

文章、用户和网关的 `jwt.config.key` 已填写同一把现有密钥；更换时同步修改三份 YAML。短信服务的 `aliyun.sms` 四项目前为空字符串，发送真实短信前直接填写模板编号、签名、AccessKey ID 和 AccessKey Secret。

| 模块 | IDE 启动类 | 默认端口 |
| --- | --- | --- |
| how_article | `com.liushao.ArticleApplication` | 9001 |
| how_search | `com.liushao.SearchApplication` | 9002 |
| how_user | `com.liushao.UserApplication` | 9003 |
| how_sms | `com.liushao.SmsApplication` | 9004 |
| how_gateway_web | `com.liushao.GatewayWebApplication` | 9005 |

API 文档采用 OpenAPI 3。文章、用户、搜索服务的入口为 `/swagger-ui/index.html`，定义为 `/v3/api-docs`；网关 `http://localhost:9005/swagger-ui/index.html` 提供三个服务的文档列表，原 `/doc.html` 入口已停用。

搜索服务直接配置为 `http://192.168.20.5:9200`，账号 `elastic`，密码已填写在 `how-search-dev.yaml`。索引使用 `smartcn`，Elasticsearch 需安装同版本 `analysis-smartcn` 插件；已有旧分词器索引需要重建并通过文章服务重新同步业务数据，不能直接修改既有字段的分词器。

验证码消息使用 Kafka `192.168.20.10:9092`，不再依赖 RabbitMQ。用户服务通过 `KafkaTemplate` 发布 JSON 消息（`mobile`、`code`），以手机号为 key；收到 broker 确认后才返回成功。短信服务使用 `@KafkaListener` 消费，成功处理后提交 offset，超过五分钟的消息会丢弃，阿里云返回失败时抛出异常交由 Kafka 消费错误处理器重试。消息可能重复投递，不承诺短信恰好发送一次。

两个服务的 `spring.kafka.bootstrap-servers` 均为 `192.168.20.10:9092`，`messaging.sms.topic` 均为 `sms`；短信服务的 `spring.kafka.consumer.group-id` 为 `how-sms`，多实例共用此消费组。短信服务启动时声明一个分区、一个副本的 topic；已有 topic 可继续使用。连接按无鉴权的 PLAINTEXT 配置。旧 `doc/软件安装(docker版)/rabbitMongo/docker-compose.yml` 仅作历史资料。

此次 ES / Kafka 及配置明文调整已同步到 `doc/nacos-public` 和 `doc/nacos-dev`；尚未发布到远程 Nacos。启用前请将五份 YAML 更新到 Nacos。按要求不执行测试或启动应用。

--模块列表  
how_article         ----文章模块  （合并基础、问答、吐槽模块模块）
how_common          ----公共模块  
how_gateway_web     ----前台网关  （合并后台网关模块）
how_search          ----搜索模块（elasticsearch）  
how_sms             ----消息模块（Kafka）\
how_user            ----用户模块（权限管理）（合并交友模块）
活动模块砍掉

tips:服务器拉取git有问题时,https改为gits

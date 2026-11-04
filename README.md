# 怎样博客

## 主要技术栈
JDK 21、Spring Boot 4.0.9、Spring Cloud 2025.1.4、Spring Cloud Alibaba 2025.1.0.0；MySQL、Redis、RabbitMQ、Elasticsearch HTTP API。依赖版本由根 POM 和官方 BOM 统一管理。

升级范围、版本依据和后续验证事项见 [JDK 21 升级记录](doc/upgrade-java21.md)。

## 数据库初始化

在 MySQL 中执行 `doc/sql/how-blog.sql`，创建 `how-blog` 数据库和全部 16 张表。文章评论、吐槽分别存储在 `tb_comment`、`tb_spit`，不再需要 MongoDB。

脚本只创建不存在的库表，不删除数据，也不导入测试数据。文章服务和用户服务共用 `how-blog`；业务配置中的 `${ENV}` 占位符由应用进程环境解析，数据库密码通过应用启动环境提供。

## Nacos 与本地应用启动

NAS 使用 r-nacos v0.8.8，部署见 [部署说明](doc/deploy/nacos/README.md)，配置和用户持久化在 `/home/ubuntu/nacos/data`；原 MySQL `nacos_config` 保留供回退。业务应用连接 `how-blog`。应用现使用 Spring Boot 4.0 / Spring Cloud 2025.1，Nacos Client 3.1.1 由 Alibaba BOM 管理。新客户端与 r-nacos 的联调待后续统一测试。

1. 在 NAS 启动 r-nacos，打开内网无鉴权控制台 `http://192.168.20.5:8848/rnacos/`。应用使用 `192.168.20.5:8848`，本机需要能访问 NAS 的 `8848` 和 `9848` 两个端口。`10848/rnacos/` 另提供需要登录的控制台。
2. 启动升级后的应用前，在 public 命名空间逐份发布或更新 [doc/nacos-public](doc/nacos-public) 中的 5 个配置。Data ID 使用完整文件名，例如 `how-article-dev.yaml`；Group 为 `DEFAULT_GROUP`，格式为 YAML，内容原样复制。保留 `${MYSQL_PASSWORD}` 等占位符，由各应用启动时解析。`doc/nacos-dev` 提供相同模板，可选用其中一套。
3. 按 [应用环境变量示例](doc/deploy/app.env.example) 为本机各个启动进程设置环境变量。示例中的空账号和密码是待填写项；Nacos 的 `NACOS_USERNAME`、`NACOS_PASSWORD` 均必填，使用已初始化的登录凭据。`NACOS_SERVER_ADDR=192.168.20.5:8848`，`NACOS_NAMESPACE` 留空表示 public，`NACOS_GROUP=DEFAULT_GROUP`，`SPRING_PROFILES_ACTIVE=dev`。如果改用其他命名空间，请填写其 ID，并将配置发布到对应命名空间。
4. 将 IDE 的项目 SDK、Maven Runner 和各启动项 JRE 均设为 JDK 21，再运行各模块的启动类。可以先启动文章服务验证 MySQL/Redis，再按需启动用户、网关、搜索和短信服务；用户服务还需要 RabbitMQ，搜索需要 Elasticsearch，短信需要 RabbitMQ 和阿里云短信配置。

从后端根目录构建（只编译生产源码，不执行测试）：

```powershell
# JAVA_HOME 指向本机 JDK 21，且将其 bin 加入当前终端 PATH。
.\mvnw.cmd -B -ntp compile
```

Linux / macOS 使用 `bash ./mvnw -B -ntp compile`。Wrapper 固定 Maven 3.9.16，并校验下载包 SHA-256。完整打包时使用 `package`；暂缓测试时可显式传入 `-DskipTests`。构建强制要求 JDK 21 和 Maven 3.9.x。

Windows / IntelliJ IDEA 中，在“运行 → 编辑配置”中为各个 Application 启动项设置“环境变量”，逐项填写示例中的键和值，再启动相应进程。每个服务均需 Nacos 变量；文章和用户服务还需 `MYSQL_USERNAME`、`MYSQL_PASSWORD` 及实际 MySQL 地址，`MYSQL_DATABASE=how-blog`。所需变量和默认地址见示例文件中的分组注释，地址、端口应与实际部署一致。

普通 `.env` 或 `app.env` 文件不会被本项目的 Spring Boot 自动读取；`app.env.example` 是填写参考文件。若在 PowerShell 中使用 `$env:变量名 = '值'` 设置变量，再从该终端启动 Java 进程，这些变量会被子进程继承；已经打开的 IDE 不会因此自动获得更新。Nacos 服务端 Compose 的 `.env` 只用于服务端容器，应用进程需要分别配置自己的环境变量。

文章、用户和网关服务必须使用同一个 `JWT_SECRET`，值为 32 字节随机数据的 Base64 编码。可在 Windows PowerShell 中生成一次，将输出保存在本机并填写到这三个启动配置：

```powershell
$jwtBytes = New-Object byte[] 32
$jwtRng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $jwtRng.GetBytes($jwtBytes)
    [Convert]::ToBase64String($jwtBytes)
} finally {
    $jwtRng.Dispose()
}
```

`JWT_SECRET` 与 Nacos 服务端的 `NACOS_AUTH_TOKEN` 分别生成、分别配置。真实密码和密钥保留在本机环境中，不提交到仓库。

| 模块 | IDE 启动类 | 默认端口 |
| --- | --- | --- |
| how_article | `com.liushao.ArticleApplication` | 9001 |
| how_search | `com.liushao.SearchApplication` | 9002 |
| how_user | `com.liushao.UserApplication` | 9003 |
| how_sms | `com.liushao.SmsApplication` | 9004 |
| how_gateway_web | `com.liushao.GatewayWebApplication` | 9005 |

API 文档采用 OpenAPI 3。文章、用户、搜索服务的入口为 `/swagger-ui/index.html`，定义为 `/v3/api-docs`；网关 `http://localhost:9005/swagger-ui/index.html` 提供三个服务的文档列表，原 `/doc.html` 入口已停用。

搜索服务改用 `ELASTICSEARCH_URIS=http://192.168.20.5:9200`，不再使用 9300 Transport 协议。索引使用 `smartcn`，Elasticsearch 需安装同版本 `analysis-smartcn` 插件；已有旧分词器索引需要重建并通过文章服务重新同步业务数据，不能直接修改既有字段的分词器。

RabbitMQ 的 Docker Compose 示例位于 `doc/软件安装(docker版)/rabbitMongo/docker-compose.yml`，其中已移除 MongoDB 服务。

--模块列表  
how_article         ----文章模块  （合并基础、问答、吐槽模块模块）
how_common          ----公共模块  
how_gateway_web     ----前台网关  （合并后台网关模块）
how_search          ----搜索模块（elasticsearch）  
how_sms             ----消息模块（rabbitMQ）  
how_user            ----用户模块（权限管理）（合并交友模块）
活动模块砍掉

tips:服务器拉取git有问题时,https改为gits
